package ru.practicum.ewm.requests.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.model.Request;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RequestMapper {
    RequestMapper INSTANCE = Mappers.getMapper(RequestMapper.class);

    default RequestDto toRequestDtoFromRequest(Request request) {
        if (request == null) {
            return null;
        }
        RequestDto.RequestDtoBuilder requestDto = RequestDto.builder();
        requestDto.eventId(request.getEventId().getId());
        requestDto.requester(request.getRequester().getId());
        requestDto.id(request.getId());
        requestDto.created(request.getCreated());
        requestDto.status(request.getStatus());
        return requestDto.build();
    }
}
