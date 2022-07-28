package com.magg.reservation.controller;

import com.magg.common.api.ApiConstants;
import com.magg.common.api.FilterComparison;
import com.magg.common.utils.PageUtils;
import com.magg.reservation.api.model.AvailabilityModel;
import com.magg.reservation.api.model.AvailabilityPage;
import com.magg.reservation.service.AvailabilityService;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static com.magg.common.api.ApiConstants.ID_PATH_PARAM;
import static com.magg.common.utils.PageUtils.FIRST;
import static com.magg.common.utils.PageUtils.LAST;
import static com.magg.common.utils.PageUtils.NEXT;
import static com.magg.common.utils.PageUtils.PREV;

public interface AvailabilityController
{

    AvailabilityService getAvailabilityService();

    /**
     *  Handler for POST /availabilities/.
     * @param availabilityModel {@link AvailabilityModel}
     * @return {@link ResponseEntity}
     */
    default ResponseEntity<AvailabilityModel> availabilityPost(AvailabilityModel availabilityModel) {
        AvailabilityModel newEntity  = getAvailabilityService().insert(availabilityModel);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path(String.format("/{%s}", ID_PATH_PARAM))
            .buildAndExpand(newEntity.getId())
            .toUri();

        return ResponseEntity.created(location).body(newEntity);
    }

    /**
     * Implementation for GET /availabilities/{id}.
     * @param id Auction ID (required)
     * @return {@link ResponseEntity}
     */
    default ResponseEntity<AvailabilityModel> availabilityGet(String id)
    {
        Optional<AvailabilityModel> modelCase = getAvailabilityService().getObject(id);
        if (modelCase.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(modelCase.get());

    }


    /**
     * Implementation for GET /auctions.
     * @return {@link ResponseEntity}
     */
    default ResponseEntity<AvailabilityPage> availabilitiesGet(
        List<String> ids,
        List<String> userIds,
        Integer pageNumber,
        Integer pageSize,
        String sortField,
        String sortDirection) {

        List<FilterComparison> filters = new ArrayList<>();
        if (ids != null) {
            filters.add(new FilterComparison("id", ids));
        }
        if (userIds != null) {
            filters.add(new FilterComparison("userId", userIds));
        }

        filters.add(new FilterComparison("booked", "false", true));


        Sort.Direction direction = sortDirection == null
            ? Sort.Direction.ASC
            : Sort.Direction.valueOf(sortDirection.toUpperCase());

        Page<AvailabilityModel> page = getAvailabilityService().getObjectList(filters,
            pageNumber, pageSize, sortField, direction);

        AvailabilityPage payload = new AvailabilityPage();
        payload.setData(page.getContent());
        payload.setTotalCount(page.getTotalElements());
        Map<String, String> linkValues = PageUtils
            .getPageLinks(page, pageSize, pageNumber, sortField, sortDirection);

        setLinks(linkValues, payload);


        return ResponseEntity.ok()
            .header(ApiConstants.X_TOTAL_COUNT_HEADER, String.valueOf(page.getTotalElements()))
            .body(payload);
    }

    /**
     * Implementation for PATCH /availabilities/{id}.
     *
     * @param id availability ID (required)
     * @param requestBody Patch request body
     * @return {@link ResponseEntity}
     */
    default  ResponseEntity<AvailabilityModel> availabilityPatch(String id, @Valid Map<String, Object> requestBody) {
        try {
            AvailabilityModel updated = getAvailabilityService().patch(id, requestBody);
            return ResponseEntity.ok(updated);
        } catch (MethodArgumentNotValidException r) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, r.getParameter().getParameterName());
        }
    }

    /**
     * Implementation for DELETE /availabilities/{id}.
     *
     * @param id Availability ID (required)
     * @return {@link ResponseEntity}
     */
    default ResponseEntity<Void> deleteAvailability(String id) {
        int deleted = getAvailabilityService().delete(List.of(id));
        if (deleted == 0) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().build();
        }
    }

    default void setLinks(Map<String, String> linkValues, AvailabilityPage page) {
        if (linkValues.containsKey(FIRST)) {
            page.setFirst(URI.create(linkValues.get(FIRST)));
        }

        if (linkValues.containsKey(NEXT)) {
            page.setNext(URI.create(linkValues.get(NEXT)));
        }

        if (linkValues.containsKey(LAST)) {
            page.setLast(URI.create(linkValues.get(LAST)));
        }

        if (linkValues.containsKey(PREV)) {
            page.setPrev(URI.create(linkValues.get(PREV)));
        }
    }
}
