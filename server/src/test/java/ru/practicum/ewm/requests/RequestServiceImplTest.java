package ru.practicum.ewm.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.admin.dto.UserDto;
import ru.practicum.ewm.admin.model.Category;
import ru.practicum.ewm.admin.model.User;
import ru.practicum.ewm.common.CommonService;
import ru.practicum.ewm.events.EventState;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.model.Request;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestServiceImplTest {
    private final RequestRepository requestRepository = Mockito.mock(RequestRepository.class);
    private final CommonService commonService = Mockito.mock(CommonService.class);
    private final RequestService requestService = new RequestServiceImpl(requestRepository, commonService);
    private User user1;
    private UserDto userDto1;
    private Event event1;
    private Request request;
    private RequestDto requestDto;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(91L)
                .name("Test1")
                .email("qwerty@qqq.ru")
                .build();
        userDto1 = UserDto.builder()
                .id(user1.getId())
                .name(user1.getName())
                .email(user1.getEmail())
                .build();
        event1 = Event.builder()
                .id(17L)
                .annotation("Testing event1")
                .categoryId(Category.builder().id(5L).build())
                .createdOn(LocalDateTime.now())
                .description("Testing description")
                .eventDate(LocalDateTime.now().plusDays(1))
                .initiator(user1)
                .locationLon(10.10)
                .locationLat(12.1)
                .paid(true)
                .participantLimit(10)
                .publishedOn(LocalDateTime.now().plusHours(1))
                .requestModeration(true)
                .state(EventState.PENDING)
                .title("Testing title.")
                .requests(new HashSet<>(Arrays.asList(Request.builder()
                        .id(11L)
                        .status(EventState.PUBLISHED)
                        .requester(user1)
                        .created(LocalDateTime.now())
                        .eventId(event1)
                        .build())))
                .build();
        request = Request.builder()
                .id(1L)
                .status(EventState.PUBLISHED)
                .requester(user1)
                .created(LocalDateTime.now())
                .eventId(event1)
                .build();
        requestDto = RequestDto.builder()
                .id(request.getId())
                .status(request.getStatus())
                .requester(request.getRequester().getId())
                .created(request.getCreated())
                .eventId(request.getEventId().getId())
                .build();
    }

    @Test
    void getRequestsOfUser() {
        Collection<Request> requests = new ArrayList<>();
        requests.add(request);
        when(commonService.getUserInDb(anyLong()))
                .thenReturn(user1);
        when(requestRepository.findAllByRequester(any(User.class)))
                .thenReturn(requests);
        List<RequestDto> testRequest = requestService.getRequestsOfUser(user1.getId())
                .stream().collect(Collectors.toList());
        assertThat(testRequest.get(0), equalTo(requestDto));
        verify(commonService, times(1))
                .getUserInDb(anyLong());
        verify(requestRepository, times(1))
                .findAllByRequester(any(User.class));
    }

    @Test
    void createRequestForUser() {
        event1.setState(EventState.PUBLISHED);
        when(commonService.getUserInDb(anyLong()))
                .thenReturn(user1);
        when(commonService.getEventInDb(anyLong()))
                .thenReturn(event1);
        when(requestRepository.findAllByRequesterAndEventId(any(User.class), any(Event.class)))
                .thenReturn(null);
        when(requestRepository.save(any(Request.class)))
                .thenReturn(request);
        RequestDto testRequest = requestService.createRequestForUser(user1.getId(), event1.getId());
        assertThat(testRequest, equalTo(requestDto));
        verify(commonService, times(1))
                .getUserInDb(anyLong());
        verify(commonService, times(1))
                .getEventInDb(anyLong());
        verify(requestRepository, times(1))
                .findAllByRequesterAndEventId(any(User.class), any(Event.class));
        verify(requestRepository, times(1))
                .save(any(Request.class));
    }

    @Test
    void cancelRequestByUser() {
        requestDto.setStatus(EventState.CANCELED);
        Request changeReq = request;
        changeReq.setStatus(EventState.CANCELED);
        when(commonService.getUserInDb(anyLong()))
                .thenReturn(user1);
        when(commonService.getRequestInDb(anyLong()))
                .thenReturn(request);
        when(requestRepository.save(any(Request.class)))
                .thenReturn(changeReq);
        RequestDto testRequest = requestService.cancelRequestByUser(user1.getId(), request.getId());
        assertThat(testRequest, equalTo(requestDto));
        verify(commonService, times(1))
                .getUserInDb(anyLong());
        verify(commonService, times(1))
                .getRequestInDb(anyLong());
        verify(requestRepository, times(1))
                .save(any(Request.class));
    }
}
