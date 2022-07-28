package com.magg.common.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.magg.common.api.FilterComparison;
import com.magg.common.exception.InvalidParameterException;
import com.magg.common.mapper.ObjectConverter;
import com.magg.common.mapper.PlatformObjectMapperFactory;
import com.magg.common.model.BaseFields;
import com.magg.common.utils.FilterUtils;
import com.magg.common.utils.JsonUtils;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.EntityNotFoundException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static java.util.Objects.requireNonNull;

public abstract class ObjectService<DomainT extends BaseFields<DomainT> , ApiT, RepoU extends CrudRepository<DomainT, String> & JpaSpecificationExecutor<DomainT>>
{

    protected static final String DEFAULT_ID_FIELD = "id";
    protected static final ObjectMapper OBJECT_MAPPER = PlatformObjectMapperFactory.getInstance();
    protected static final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();
    protected static final Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();
    protected final ObjectConverter<DomainT, ApiT> converter;
    protected final Class<ApiT> clazz;

    protected final Class<DomainT> domainClass;
    protected final RepoU repository;
    protected final Logger LOG;

    protected ObjectService(
        Class<ApiT> clazz,
        RepoU repository,
        ObjectConverter<DomainT, ApiT> converter)
    {
        this.repository = repository;
        this.domainClass = getPersistenceClass();
        this.converter = converter;
        this.LOG = LoggerFactory.getLogger(domainClass);
        this.clazz = clazz;
    }


    /**
     * Creates the object and returns the result with id and operational properties populated.
     * Enforces the custom schema associated with the collection of this service.
     *
     * @param apiObject The object from the API.
     * @return API object with id and audit properties.
     */
    public ApiT insert(ApiT apiObject) {
        return insertObject(apiObject);
    }

    private ApiT insertObject(ApiT apiObject) {
        return Optional
            .ofNullable(apiObject)
            .map(this::createPreTransformHook)
            .map(this::apiToDomainModel)
            .map(this::createPreCommitHook)
            .map(this::doCreate)
            .map(this::createPostCommitHook)
            .map(this::domainToApiModel)
            .map(this::createPostTransformHook)
            .orElseThrow(NullPointerException::new);
    }

    /**
     * Retrieve an object by providing the identifier for it.
     * @param objectId The object's identifier
     * @return an object extending {@link }
     */
    public Optional<ApiT> getObject(String objectId) {
        requireNonNull(objectId);

        Optional<DomainT> domainObject = doGetById(objectId);

        domainObject.ifPresent(this::getPreTransformHook);

        Optional<ApiT> apiObject = domainObject.map(this::domainToApiModel);

        apiObject.ifPresent(this::getPostCommitHook);

        return apiObject;
    }

    protected DomainT doCreate(DomainT domainT)
    {
        DomainT domainT1;
        domainT1 = repository.save(domainT);
        return domainT1;
    }

    /**
     * Delete objects with the given ids.
     * @param ids list of ids
     * @return the number of objects deleted
     */
    public int delete(List<String> ids)
    {
        if (ids == null || ids.isEmpty())
        {
            return 0;
        }
        return doDelete(ids);
    }

    /**
     * Do the actual delete operation.
     * @param ids list of object ids to delete
     * @return the number of objects actually deleted.
     */
    protected int doDelete(List<String> ids)
    {

        FilterUtils<DomainT> filterUtils = new FilterUtils<DomainT>(getDeleteFilters(ids), null);
        List<DomainT> deleted = repository.findAll(filterUtils);

        repository.deleteAll(deleted);

        return deleted.size();

    }

    protected List<FilterComparison> getDeleteFilters(List<String> ids) {
        return  List.of(new FilterComparison(DEFAULT_ID_FIELD, ids));
    }


    private Optional<DomainT> doGetById(String domainId) throws EntityNotFoundException
    {
        return repository.findById(domainId);
    }

    private Class<DomainT> getPersistenceClass() {
        try {
            return (Class<DomainT>) Class.forName(((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0].getTypeName());
        } catch (ClassNotFoundException e) {
            // Tidy this up - prevent service start if we cannot determine persistence class
            throw new RuntimeException(e);
        }
    }

    /**
     * Patch an update with the properties in the object graph passed to it.
     * Enforces the custom schema associated with the collection of this service.
     * @param objectId - the identifier of the object to be patched
     * @param patch - the graph which hold the properties to replace
     * @return the patched API object
     */
    public ApiT patch(String objectId, Map<String, Object> patch) throws MethodArgumentNotValidException {
        return patchObject(objectId, patch);
    }


    private ApiT patchObject(String objectId, Map<String, Object> patch)
        throws MethodArgumentNotValidException {

        // Validate the requested object exists to be patched
        DomainT currentObject = doGetById(objectId)
            .orElseThrow(() -> new EntityNotFoundException(String.valueOf(objectId)));

        // Get the current state of the object
        ApiT currentObjectState = domainToApiModel(currentObject);

        ApiT patchedObjectState = patchObject(currentObjectState, patch);

        validatePatchedObject(patchedObjectState);

        DomainT domainPatch = apiToDomainModel(patchedObjectState);

        domainPatch = doPatch(domainPatch);

        return domainToApiModel(domainPatch);
    }

    protected ApiT patchObject(ApiT currentObject, Map<String, Object> patchProperties) {
        // convert to graph
        Map<String, Object> currentObjectGraph = OBJECT_MAPPER.convertValue(currentObject, new TypeReference<>() {});
        // patch the object
        JsonUtils.mapProperties(patchProperties, currentObjectGraph);
        // convert back to API object

        return OBJECT_MAPPER.convertValue(currentObjectGraph, clazz);
    }

    private void validatePatchedObject(ApiT patchedObjectState)
        throws MethodArgumentNotValidException {
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(patchedObjectState, domainClass.getName());
        SpringValidatorAdapter adapter = new SpringValidatorAdapter(VALIDATOR);
        adapter.validate(patchedObjectState, result);

        if (result.hasErrors()) {
            throw new MethodArgumentNotValidException(null, result);
        }
    }

    /**
     * Persist the patch on the object.
     * @param patch the patched object
     * @return the updated object
     */
    protected DomainT doPatch(DomainT patch) {
        return repository.save(patch);
    }


    /**
     * Retrieve a paged and sorted list of objects.
     * @param filters any filters we support for the subclass of this service.
     * @param pageNumber the number of the page (1-based). Defaults to {@link this.PAGE_ONE}
     * @param pageSize the maximum number of objects to return in a page. Defaults to {@link this.DEFAULT_PAGE_SIZE}
     * @param sortField the field to sort by. Can be null for no sort performed.
     * @param sortDirection the direction to sort by. If sortField is provided, defaults to ASC
     *
     * @return {@link Page} of {@link ApiT} objects
     */
    public Page<ApiT>  getObjectList(
        List<FilterComparison> filters,
        final Integer pageNumber,
        final Integer pageSize,
        final String sortField,
        final Sort.Direction sortDirection) {

        if (pageNumber != null && pageNumber == 0) {
            throw new InvalidParameterException("pageNumber must be greater than 0, null is allowed"
                + " and will default to 1", null);
        }

        FilterUtils<DomainT> filterUtils = new FilterUtils<DomainT>(filters, null);

        Pageable pageable = filterUtils
            .pageRequest(pageNumber, pageSize, sortField, sortDirection);

        Page<DomainT> pageOfObjects = repository.findAll(filterUtils, pageable);

        return pageOfObjects.map(this::domainToApiModel);
    }


    /**
     * Maps an api object into a domain object. Perform validation of custom fields if present.
     * @param apiObject The source object
     * @return A new domain object
     */
    public DomainT apiToDomainModel(ApiT apiObject) {
        DomainT domainObject = converter.apiModelToDataModel(apiObject);

        return domainObject;
    }

    /**
     * Maps a domain object into an api object.
     * @param object The domain object to convert
     * @return An api object
     */
    protected ApiT domainToApiModel(DomainT object) {
        return converter.dataModelToApiModel(object);
    }

    /**
     * Override this method to perform any operations necessary before the posted object
     * is transformed to the data domain.
     *
     * @param apiObject the api model object
     * @return {@link ApiT}
     */
    protected ApiT createPreTransformHook(ApiT apiObject) {
        return apiObject;
    }

    /**
     * Override this method to perform any operations necessary before the posted object
     * is committed to the data store.
     *
     * @param domainObject the domain object
     * @return {@link DomainT}
     */
    protected DomainT createPreCommitHook(DomainT domainObject) {
        return domainObject;
    }

    /**
     * Override this method to perform any operations necessary after the posted object
     * has been committed to the data store.
     *
     * @param domainObject the domain object
     * @return {@link DomainT}
     */
    protected DomainT createPostCommitHook(DomainT domainObject) {
        return domainObject;
    }

    /**
     * Override this method to perform any operations necessary after the posted object
     * has been committed and transformed back to the api model.
     *
     * @param apiObject the api model object
     * @return {@link ApiT}
     */
    protected ApiT createPostTransformHook(ApiT apiObject) {
        return apiObject;
    }

    /**
     * Override this method to perform any operations necessary before the retrieved object
     * is transformed to the api model object.
     *
     * @param domainObject the domain model object
     */
    protected void getPreTransformHook(DomainT domainObject) {

    }

    /**
     * Override this method to perform any operations necessary before the retrieved object
     * is returned to the caller.
     *
     * @param apiObject the api model object
     */
    protected void getPostCommitHook(ApiT apiObject) {

    }

}
