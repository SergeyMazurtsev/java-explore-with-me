package ru.practicum.ewm.requests;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.admin.model.User;
import ru.practicum.ewm.common.CommonService;
import ru.practicum.ewm.events.EventState;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.exceptions.IntegrityViolationException;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.exceptions.ValidationException;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.mapper.RequestMapper;
import ru.practicum.ewm.requests.model.Request;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final CommonService commonService;

    @Override
    public Collection<RequestDto> getRequestsOfUser(Long userId) {
        return requestRepository.findAllByRequester(
                        commonService.getUserInDb(userId)).stream()
                .map(RequestMapper.INSTANCE::toRequestDtoFromRequest)
                .collect(Collectors.toList());
    }

    @Override
    public RequestDto createRequestForUser(Long userId, Long eventId) {
        User user = commonService.getUserInDb(userId);
        Event event = commonService.getEventInDb(eventId);
        val checkInDb = requestRepository.findAllByRequesterAndEventId(user, event);
        if (!checkInDb.isEmpty()) {
            throw new ValidationException("Such request is saved.");
        }
        if (event.getInitiator().getId().equals(user.getId())) {
            throw new ValidationException("User can't be initiator of event.");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Event must be published.");
        }
        if (event.getParticipantLimit() <= event.getRequests()
                .stream().filter(i -> i.getStatus().equals(EventState.PUBLISHED))
                .collect(Collectors.toList())
                .size()) {
            throw new ValidationException("Partition limit is reached.");
        }
        Request request = Request.builder()
                .created(LocalDateTime.now())
                .eventId(event)
                .requester(user)
                .status((event.getRequestModeration()) ? EventState.PENDING : EventState.CONFIRMED)
                .build();
        try {
            return RequestMapper.INSTANCE.toRequestDtoFromRequest(requestRepository.save(request));
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("could not execute statement; SQL %s; " +
                            "constraint %s; nested exception is " +
                            "org.hibernate.exception.ConstraintViolationException: could not execute statement",
                    "Save request of user method", "request category"));
        }
    }

    @Override
    public RequestDto cancelRequestByUser(Long userId, Long requestId) {
        User user = commonService.getUserInDb(userId);
        Request request = commonService.getRequestInDb(requestId);
        if (!user.getId().equals(request.getRequester().getId())) {
            throw new NotFoundException("Id of user and request requester is not equal.");
        }
        request.setStatus(EventState.CANCELED);
        try {
            return RequestMapper.INSTANCE.toRequestDtoFromRequest(requestRepository.save(request));
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("could not execute statement; SQL %s; " +
                            "constraint %s; nested exception is " +
                            "org.hibernate.exception.ConstraintViolationException: could not execute statement",
                    "Cancel request of user method", "request category"));
        }
    }
}
