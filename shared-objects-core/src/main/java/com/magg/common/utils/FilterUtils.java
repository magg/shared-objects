package com.magg.common.utils;

import com.magg.common.api.FilterComparison;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

public class FilterUtils<U> implements Specification<U> {
    private static final Integer PAGE_ONE = 0;
    private static final int DEFAULT_PAGE_SIZE = 25;

    private final List<FilterComparison> params;
    private final String joinTable;

    private Join<Object,Object> join;

    public FilterUtils(List<FilterComparison> params, String joinTable) {
        this.params = params;
        this.joinTable = joinTable;
    }

    @Override
    public Predicate toPredicate(Root<U> root, CriteriaQuery<?> criteria, CriteriaBuilder builder) {
        //create a new predicate list

        if (joinTable != null) {
            join = root.join(joinTable, JoinType.INNER);
        }

        List<Predicate> predicates = new ArrayList<>();

        //add criteria to predicates
        for (FilterComparison comparison : params) {
            if (comparison.getOperator() == FilterComparison.Operator.EQ) {
                Path<Object> field = getPath(comparison, root);
                if (comparison.getValue().size() > 1) {

                    var myPredicate = builder.in(field);

                    if (comparison.isaBoolean()) {
                        comparison.getValue().stream().map(Boolean::valueOf).collect(Collectors.toList()).forEach(myPredicate::value);
                    } else {
                        comparison.getValue().forEach(myPredicate::value);
                    }
                    predicates.add(myPredicate);

                } else {

                    if (comparison.isaBoolean()) {
                        predicates.add(builder.equal(field, Boolean.valueOf(comparison.getValue().get(0))));
                    } else {
                        predicates.add(builder.equal(field, comparison.getValue().get(0)));
                    }

                }
            } else {
                throw new UnsupportedOperationException(
                    "Operator " + comparison.getOperator() + " is not supported");
            }
        }

        return builder.and(predicates.toArray(new Predicate[0]));
    }

    private Path<Object> getPath(FilterComparison comparison, Root<U> root) {
        Path<Object> field;
        if (comparison.isOtherSide()) {
            field = join.get(comparison.getField());
        } else {
            field = root.get(comparison.getField());
        }


        return field;
    }

    /**
     * Builds a Page request.
     *
     * @param pageNumber pageNumber
     * @param pageSize pageSize
     * @param sortField sortField
     * @param sortDirection sortDirection
     * @return a Page
     */
    public Pageable pageRequest(Integer pageNumber, Integer pageSize, String sortField,
        Sort.Direction sortDirection) {

        pageNumber = pageNumber == null ? PAGE_ONE : pageNumber - 1;
        pageSize = pageSize == null ? DEFAULT_PAGE_SIZE : pageSize;
        sortDirection = sortDirection == null ? Sort.Direction.ASC : sortDirection;

        if (StringUtils.isEmpty(sortField)) {
            return PageRequest.of(pageNumber, pageSize);
        } else {
            return PageRequest.of(pageNumber, pageSize, sortDirection, sortField);
        }
    }
}
