package com.magg.reservation.service;

import com.magg.common.api.FilterComparison;
import com.magg.common.mapper.ObjectConverter;
import com.magg.common.service.ObjectService;
import com.magg.reservation.api.model.ReservationModel;
import com.magg.reservation.domain.Availability;
import com.magg.reservation.domain.Reservation;
import com.magg.reservation.repository.AvailabilityRepository;
import com.magg.reservation.repository.ReservationRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class ReservationService extends ObjectService<Reservation, ReservationModel, ReservationRepository>
{

    private List<FilterComparison> deleteFilter = new ArrayList<>();
    private final AvailabilityRepository availabilityRepository;

    public ReservationService(
        ReservationRepository repository,
        ObjectConverter<Reservation, ReservationModel> converter, AvailabilityRepository availabilityRepository)
    {
        super(ReservationModel.class, repository, converter);
        this.availabilityRepository = availabilityRepository;
    }


    public int deleteAndValidateEmail(List<String> ids, String email) {
        deleteFilter = List.of(new FilterComparison(DEFAULT_ID_FIELD, ids), new FilterComparison("email", email));
        return doDelete(ids);
    }

    @Override
    protected List<FilterComparison> getDeleteFilters(List<String> ids)
    {
        return deleteFilter;
    }


    @Override
    protected Reservation createPostCommitHook(Reservation domainObject)
    {
       String availabilityId = domainObject.getAvailabilityId();
       Instant reservationEndTime = domainObject.getEndTime();
       Instant reservationStartTime = domainObject.getStartTime();

       Optional<Availability> availabilityOptional  = availabilityRepository.findById(availabilityId);

       if (availabilityOptional.isPresent()) {
           Availability availability = availabilityOptional.get();
           Instant availableEndTime = availability.getEndTime();
           Instant availableStartTime = availability.getStartTime();

           if (reservationEndTime.isBefore(availableEndTime) && reservationStartTime.isAfter(availableStartTime)) {
               availability.setBooked(true);
           } else if (reservationStartTime.equals(availableStartTime) && reservationEndTime.isBefore(availableEndTime)) {
               availability.setStartTime(reservationEndTime);
           } else if (reservationStartTime.isAfter(availableStartTime) && reservationEndTime.equals(availableEndTime)) {
               availability.setEndTime(reservationStartTime);
           } else {
            availability.setBooked(true);
           }

           availabilityRepository.save(availability);

       }
        return domainObject;
    }
}
