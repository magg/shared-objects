package com.magg.reservation.service;

import com.magg.common.mapper.ObjectConverter;
import com.magg.common.service.ObjectService;
import com.magg.reservation.api.model.AvailabilityModel;
import com.magg.reservation.domain.Availability;
import com.magg.reservation.repository.AvailabilityRepository;
import org.springframework.stereotype.Service;

@Service
public class AvailabilityService extends ObjectService<Availability, AvailabilityModel, AvailabilityRepository>
{

    public AvailabilityService(AvailabilityRepository repository,
        ObjectConverter<Availability, AvailabilityModel> converter)
    {
        super(AvailabilityModel.class, repository, converter);
    }
}
