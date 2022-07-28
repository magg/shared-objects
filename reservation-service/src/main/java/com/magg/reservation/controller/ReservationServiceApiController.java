package com.magg.reservation.controller;

import com.magg.reservation.api.model.AvailabilityModel;
import com.magg.reservation.api.model.AvailabilityPage;
import com.magg.reservation.api.model.ReservationModel;
import com.magg.reservation.api.model.ReservationPage;
import com.magg.reservation.api.model.UserModel;
import com.magg.reservation.service.AvailabilityService;
import com.magg.reservation.service.ReservationService;
import java.util.List;
import java.util.Map;
import om.magg.reservation.api.DefaultApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReservationServiceApiController implements
    DefaultApi,
    ReservationController,
    AvailabilityController
{

    private final ReservationService reservationService;
    private final AvailabilityService availabilityService;


    public ReservationServiceApiController(ReservationService reservationService, AvailabilityService availabilityService)
    {
        this.reservationService = reservationService;
        this.availabilityService = availabilityService;
    }


    @Override
    public ResponseEntity<AvailabilityPage> apiV1AvailabilitiesGet(
        List<String> ids, List<String> userIds, Integer pageSize, Integer pageNumber, String sortField, String sortDirection)
    {
        return availabilitiesGet(ids, userIds, pageNumber, pageSize, sortField, sortDirection);
    }


    @Override
    public ResponseEntity<Void> apiV1AvailabilitiesIdDelete(String id)
    {
        return deleteAvailability(id);
    }


    @Override
    public ResponseEntity<AvailabilityModel> apiV1AvailabilitiesIdGet(String id)
    {
        return availabilityGet(id);
    }


    @Override
    public ResponseEntity<AvailabilityModel> apiV1AvailabilitiesIdPatch(String id, Map<String, Object> requestBody)
    {
        return availabilityPatch(id, requestBody);
    }


    @Override
    public ResponseEntity<AvailabilityModel> apiV1AvailabilitiesPost(AvailabilityModel availabilityModel)
    {
        return availabilityPost(availabilityModel);
    }


    @Override
    public ResponseEntity<ReservationPage> apiV1ReservationsGet(
        List<String> ids, List<String> userIds, List<String> email, Integer pageSize, Integer pageNumber, String sortField, String sortDirection)
    {
        return reservationsGet(ids, userIds, email, pageSize, pageNumber, sortField, sortDirection);
    }


    @Override
    public ResponseEntity<Void> apiV1ReservationsIdDelete(String id, String email)
    {
        return deleteReservation(id, email);
    }


    @Override
    public ResponseEntity<ReservationModel> apiV1ReservationsIdGet(String id)
    {
        return reservationGet(id);
    }


    @Override
    public ResponseEntity<ReservationModel> apiV1ReservationsIdPatch(String id, Map<String, Object> requestBody)
    {
        return reservationPatch(id, requestBody);
    }


    @Override
    public ResponseEntity<ReservationModel> apiV1ReservationsPost(ReservationModel reservationModel)
    {
        return reservationPost(reservationModel);
    }


    @Override
    public ResponseEntity<UserModel> apiV1UsersIdGet(String id)
    {
        return DefaultApi.super.apiV1UsersIdGet(id);
    }


    @Override
    public ResponseEntity<UserModel> apiV1UsersPost(UserModel userModel)
    {
        return DefaultApi.super.apiV1UsersPost(userModel);
    }


    @Override
    public ReservationService getReservationService()
    {
        return reservationService;
    }


    @Override
    public AvailabilityService getAvailabilityService()
    {
        return availabilityService;
    }
}
