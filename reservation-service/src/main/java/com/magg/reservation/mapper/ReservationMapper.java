package com.magg.reservation.mapper;

import com.magg.common.mapper.ObjectConverter;
import com.magg.reservation.api.model.ReservationModel;
import com.magg.reservation.domain.Reservation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReservationMapper extends ObjectConverter<Reservation, ReservationModel>
{
    Reservation apiModelToDataModel(ReservationModel apiModel);

    ReservationModel dataModelToApiModel(Reservation domain);
}
