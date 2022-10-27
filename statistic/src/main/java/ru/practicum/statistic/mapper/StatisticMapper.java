package ru.practicum.statistic.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.practicum.statistic.dto.EndpointHitDto;
import ru.practicum.statistic.dto.ViewStatsDto;
import ru.practicum.statistic.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface StatisticMapper {
    StatisticMapper INSTATCE = Mappers.getMapper(StatisticMapper.class);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "timestamp", target = "createdOn")
    @Mapping(source = "uri", target = "attributes.uri")
    @Mapping(source = "ip", target = "attributes.ip")
    EndpointHit toEndpointHitFromEndpointHitDto(EndpointHitDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "attributes.uri", target = "uri")
    ViewStatsDto toViewStatsDtoFromEndpointHit(EndpointHit endpointHit);
}
