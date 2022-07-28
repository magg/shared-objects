package com.magg.reservation.controller;

import com.magg.common.api.ApiConstants;
import com.magg.common.api.FilterComparison;
import com.magg.common.utils.PageUtils;
import com.magg.reservation.api.model.ReservationModel;
import com.magg.reservation.api.model.ReservationPage;
import com.magg.reservation.service.ReservationService;
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

public interface ReservationController
{
    ReservationService getReservationService();

    /**
     *  Handler for POST /reservations/.
     * @param reservationModel {@link ReservationModel}
     * @return {@link ResponseEntity}
     */
    default ResponseEntity<ReservationModel> reservationPost(ReservationModel reservationModel) {
        ReservationModel newEntity  = getReservationService().insert(reservationModel);

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path(String.format("/{%s}", ID_PATH_PARAM))
            .buildAndExpand(newEntity.getId())
            .toUri();

        return ResponseEntity.created(location).body(newEntity);
    }

    /**
     * Implementation for GET /reservations/{id}.
     * @param id Auction ID (required)
     * @return {@link ResponseEntity}
     */
    default ResponseEntity<ReservationModel> reservationGet(String id)
    {
        Optional<ReservationModel> modelCase = getReservationService().getObject(id);
        if (modelCase.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(modelCase.get());

    }


    /**
     * Implementation for GET /auctions.
     * @return {@link ResponseEntity}
     */
    default ResponseEntity<ReservationPage> reservationsGet(
        List<String> ids,
        List<String> userIds,
        List<String> email,
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
        if (email != null) {
            filters.add(new FilterComparison("email", email));
        }

        Sort.Direction direction = sortDirection == null
            ? Sort.Direction.ASC
            : Sort.Direction.valueOf(sortDirection.toUpperCase());

        Page<ReservationModel> page = getReservationService().getObjectList(filters,
            pageNumber, pageSize, sortField, direction);

        ReservationPage payload = new ReservationPage();
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
     * Implementation for PATCH /reservations/{id}.
     *
     * @param id reservation ID (required)
     * @param requestBody Patch request body
     * @return {@link ResponseEntity}
     */
    default  ResponseEntity<ReservationModel> reservationPatch(String id, @Valid Map<String, Object> requestBody) {
        try {
            ReservationModel updated = getReservationService().patch(id, requestBody);
            return ResponseEntity.ok(updated);
        } catch (MethodArgumentNotValidException r) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, r.getParameter().getParameterName());
        }
    }

    /**
     * Implementation for DELETE /reservations/{id}.
     *
     * @param id Reservation ID (required)
     * @param email Reservation ID (required)
     * @return {@link ResponseEntity}
     */
    default ResponseEntity<Void> deleteReservation(String id, String email) {
        int deleted = getReservationService().deleteAndValidateEmail(List.of(id), email);
        if (deleted == 0) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().build();
        }
    }

    default void setLinks(Map<String, String> linkValues, ReservationPage page) {
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
