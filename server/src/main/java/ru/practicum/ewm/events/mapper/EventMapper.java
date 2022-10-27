package ru.practicum.ewm.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.admin.mapper.CategoryMapper;
import ru.practicum.ewm.admin.mapper.UserMapper;
import ru.practicum.ewm.admin.model.Category;
import ru.practicum.ewm.comments.CommentMapper;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.events.EventState;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.model.Event;

import java.time.LocalDateTime;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

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
        OptionalDouble averageRating = (event.getComments() != null) ?
                event.getComments().stream()
                        .map(Comment::getRating)
                        .mapToDouble(a -> a)
                        .average() : OptionalDouble.empty();
        return EventDtoOutShort.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.INSTANCE.toCategoryDto(event.getCategoryId()))
                .confirmedRequests(
                        (event.getRequests() != null) ?
                                event.getRequests().stream()
                                        .filter(i -> i.getStatus().equals(EventState.CONFIRMED))
                                        .count() : 0)
                .eventDate(event.getEventDate())
                .initiator(UserMapper.INSTANCE.toUserDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .averageRating((averageRating.isPresent()) ? averageRating.getAsDouble() : null)
                .comments(
                        (event.getComments() != null) ?
                                event.getComments().stream()
                                        .map(CommentMapper.INSTANCE::toCommentDtoOutFromComment)
                                        .collect(Collectors.toSet()) : null)
                .build();
    }

    default EventDtoOutFull toEventDtoOutFullFromEvent(Event event) {
        if (event == null) {
            return null;
        }
        OptionalDouble averageRating = (event.getComments() != null) ?
                event.getComments().stream()
                        .map(Comment::getRating)
                        .mapToDouble(a -> a)
                        .average() : OptionalDouble.empty();
        return EventDtoOutFull.builder()
                .id(event.getId())
                .location(Location.builder()
                        .lat(event.getLocationLat())
                        .lon(event.getLocationLon())
                        .build())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.INSTANCE.toCategoryDto(event.getCategoryId()))
                .confirmedRequests(
                        (event.getRequests() != null) ?
                                event.getRequests().stream()
                                        .filter(i -> i.getStatus().equals(EventState.CONFIRMED))
                                        .count() : 0)
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.INSTANCE.toUserDto(event.getInitiator()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .averageRating((averageRating.isPresent()) ? averageRating.getAsDouble() : null)
                .comments(
                        (event.getComments() != null) ?
                                event.getComments().stream()
                                        .map(CommentMapper.INSTANCE::toCommentDtoOutFromComment)
                                        .collect(Collectors.toSet()) : null)
                .build();
    }

    default void patchingEvent(EventDtoInPatch eventDtoInPatch, @MappingTarget Event eventTarget) {
        if (eventDtoInPatch == null) {
            return;
        }
        if (eventDtoInPatch.getAnnotation() != null) {
            eventTarget.setAnnotation(eventDtoInPatch.getAnnotation());
        }
        if (eventDtoInPatch.getCategory() != null) {
            eventTarget.setCategoryId(Category.builder().id(eventDtoInPatch.getCategory()).build());
        }
        if (eventDtoInPatch.getDescription() != null) {
            eventTarget.setDescription(eventDtoInPatch.getDescription());
        }
        if (eventDtoInPatch.getEventDate() != null) {
            eventTarget.setEventDate(eventDtoInPatch.getEventDate());
        }
        if (eventDtoInPatch.getPaid() != null) {
            eventTarget.setPaid(eventDtoInPatch.getPaid());
        }
        if (eventDtoInPatch.getParticipantLimit() != null) {
            eventTarget.setParticipantLimit(eventDtoInPatch.getParticipantLimit());
        }
        if (eventDtoInPatch.getTitle() != null) {
            eventTarget.setTitle(eventDtoInPatch.getTitle());
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
