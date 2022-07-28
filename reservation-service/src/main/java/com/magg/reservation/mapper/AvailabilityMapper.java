package com.magg.reservation.mapper;

import com.magg.common.mapper.ObjectConverter;
import com.magg.reservation.api.model.AvailabilityModel;
import com.magg.reservation.domain.Availability;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface AvailabilityMapper extends ObjectConverter<Availability, AvailabilityModel>
{

    @Mappings({
        @Mapping(target="startTime", source="apiModel.startTime"),
        @Mapping(target="endTime", source="apiModel.endTime")
    })
    Availability apiModelToDataModel(AvailabilityModel apiModel);

    @Mappings({
        @Mapping(target="startTime", source="domain.startTime"),
        @Mapping(target="endTime", source="domain.endTime")
    })
    AvailabilityModel dataModelToApiModel(Availability domain);

}
