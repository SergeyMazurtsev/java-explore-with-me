package ru.practicum.ewm.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.admin.mapper.CategoryMapper;
import ru.practicum.ewm.admin.mapper.UserMapper;
import ru.practicum.ewm.admin.model.Category;
import ru.practicum.ewm.events.EventState;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.model.Event;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    default Event toEventFromEventDtoIn(EventDtoIn eventDtoIn) {
        if (eventDtoIn == null) {
            return null;
        }
        Event.EventBuilder event = Event.builder();
        event.id(eventDtoIn.getId());
        event.annotation(eventDtoIn.getAnnotation());
        event.createdOn(LocalDateTime.now());
        event.description(eventDtoIn.getDescription());
        event.eventDate(eventDtoIn.getEventDate());
        event.locationLat(eventDtoIn.getLocation().getLat());
        event.locationLon(eventDtoIn.getLocation().getLon());
        event.paid(eventDtoIn.getPaid());
        event.participantLimit(eventDtoIn.getParticipantLimit());
        event.requestModeration(eventDtoIn.getRequestModeration());
        event.state(EventState.PENDING);
        event.title(eventDtoIn.getTitle());
        return event.build();
    }

    default EventDtoOutShort toEventDtoOutShortFromEvent(Event event) {
        if (event == null) {
            return null;
        }
        EventDtoOutShort.EventDtoOutShortBuilder eventDtoOutShort = EventDtoOutShort.builder();
        eventDtoOutShort.id(event.getId());
        eventDtoOutShort.annotation(event.getAnnotation());
        eventDtoOutShort.category(CategoryMapper.INSTANCE.toCategoryDto(event.getCategoryId()));
        eventDtoOutShort.confirmedRequests(
                (event.getRequests() != null) ?
                        event.getRequests().stream()
                                .filter(i -> i.getStatus().equals(EventState.CONFIRMED))
                                .count() : 0);
        eventDtoOutShort.eventDate(event.getEventDate());
        eventDtoOutShort.initiator(UserMapper.INSTANCE.toUserDto(event.getInitiator()));
        eventDtoOutShort.paid(event.getPaid());
        eventDtoOutShort.title(event.getTitle());
        return eventDtoOutShort.build();
    }

    default EventDtoOutFull toEventDtoOutFullFromEvent(Event event) {
        if (event == null) {
            return null;
        }
        EventDtoOutFull.EventDtoOutFullBuilder eventDtoOutFull = EventDtoOutFull.builder();
        eventDtoOutFull.location(Location.builder()
                .lat(event.getLocationLat())
                .lon(event.getLocationLon())
                .build());
        eventDtoOutFull.id(event.getId());
        eventDtoOutFull.annotation(event.getAnnotation());
        eventDtoOutFull.category(CategoryMapper.INSTANCE.toCategoryDto(event.getCategoryId()));
        eventDtoOutFull.confirmedRequests(
                (event.getRequests() != null) ?
                        event.getRequests().stream()
                                .filter(i -> i.getStatus().equals(EventState.CONFIRMED))
                                .count() : 0);
        eventDtoOutFull.createdOn(event.getCreatedOn());
        eventDtoOutFull.description(event.getDescription());
        eventDtoOutFull.eventDate(event.getEventDate());
        eventDtoOutFull.initiator(UserMapper.INSTANCE.toUserDto(event.getInitiator()));
        eventDtoOutFull.paid(event.getPaid());
        eventDtoOutFull.participantLimit(event.getParticipantLimit());
        eventDtoOutFull.publishedOn(event.getPublishedOn());
        eventDtoOutFull.requestModeration(event.getRequestModeration());
        eventDtoOutFull.state(event.getState());
        eventDtoOutFull.title(event.getTitle());
        return eventDtoOutFull.build();
    }

    default void patchingEvent(EventDtoInPatch eventDtoInPatch, @MappingTarget Event event) {
        if (eventDtoInPatch == null) {
            return;
        }
        if (eventDtoInPatch.getAnnotation() != null) {
            event.setAnnotation(eventDtoInPatch.getAnnotation());
        }
        if (eventDtoInPatch.getCategory() != null) {
            event.setCategoryId(Category.builder().id(eventDtoInPatch.getCategory()).build());
        }
        if (eventDtoInPatch.getDescription() != null) {
            event.setDescription(eventDtoInPatch.getDescription());
        }
        if (eventDtoInPatch.getEventDate() != null) {
            event.setEventDate(eventDtoInPatch.getEventDate());
        }
        if (eventDtoInPatch.getPaid() != null) {
            event.setPaid(eventDtoInPatch.getPaid());
        }
        if (eventDtoInPatch.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDtoInPatch.getParticipantLimit());
        }
        if (eventDtoInPatch.getTitle() != null) {
            event.setTitle(eventDtoInPatch.getTitle());
        }
    }

    default void patchAdminEvent(EventDtoIn eventDtoIn, @MappingTarget Event event) {
        if (eventDtoIn == null) {
            return;
        }
        if (eventDtoIn.getLocation() != null) {
            event.setLocationLat(eventDtoIn.getLocation().getLat());
            event.setLocationLon(eventDtoIn.getLocation().getLon());
        }
        if (eventDtoIn.getId() != null) {
            event.setId(eventDtoIn.getId());
        }
        if (eventDtoIn.getAnnotation() != null) {
            event.setAnnotation(eventDtoIn.getAnnotation());
        }
        if (eventDtoIn.getCategory() != null) {
            event.setCategoryId(Category.builder().id(eventDtoIn.getCategory()).build());
        }
        if (eventDtoIn.getDescription() != null) {
            event.setDescription(eventDtoIn.getDescription());
        }
        if (eventDtoIn.getEventDate() != null) {
            event.setEventDate(eventDtoIn.getEventDate());
        }
        if (eventDtoIn.getPaid() != null) {
            event.setPaid(eventDtoIn.getPaid());
        }
        if (eventDtoIn.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDtoIn.getParticipantLimit());
        }
        if (eventDtoIn.getRequestModeration() != null) {
            event.setRequestModeration(eventDtoIn.getRequestModeration());
        }
        if (eventDtoIn.getTitle() != null) {
            event.setTitle(eventDtoIn.getTitle());
        }
    }
}
