package ru.practicum.ewm.comments;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.admin.dto.UserDto;
import ru.practicum.ewm.admin.model.Category;
import ru.practicum.ewm.admin.model.User;
import ru.practicum.ewm.comments.dto.CommentDtoIn;
import ru.practicum.ewm.comments.dto.CommentDtoOut;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.common.CommonService;
import ru.practicum.ewm.events.EventState;
import ru.practicum.ewm.events.model.Event;
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
public class CommentServiceImplTest {
    private final CommonService commonService = Mockito.mock(CommonService.class);
    private final CommentRepository commentRepository = Mockito.mock(CommentRepository.class);
    private final CommentService commentService = new CommentServiceImpl(commonService, commentRepository);
    private Comment comment1;
    private Comment comment2;
    private CommentDtoOut commentDtoOut1;
    private CommentDtoOut commentDtoOut2;
    private CommentDtoIn commentDtoIn;
    private User user1;
    private User user2;
    private UserDto userDto1;
    private UserDto userDto2;
    private Event event1;
    private Event event2;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .id(21L)
                .name("Lex")
                .email("qwerty@qqq.ru")
                .build();
        user2 = User.builder()
                .id(32L)
                .name("Nik")
                .email("asdfgh@www.ru")
                .build();
        userDto1 = UserDto.builder()
                .id(user1.getId())
                .name(user1.getName())
                .email(user1.getEmail())
                .build();
        userDto2 = UserDto.builder()
                .id(user2.getId())
                .name(user2.getName())
                .email(user2.getEmail())
                .build();
        event1 = Event.builder()
                .id(1L)
                .annotation("Testing event1")
                .categoryId(Category.builder().id(2L).build())
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
                .state(EventState.PUBLISHED)
                .title("Testing title.")
                .requests(new HashSet<>(Arrays.asList(Request.builder()
                        .id(1L)
                        .status(EventState.CONFIRMED)
                        .requester(user1)
                        .created(LocalDateTime.now())
                        .eventId(event1)
                        .build())))
                .build();
        event2 = Event.builder()
                .id(2L)
                .annotation("Testing event2")
                .categoryId(Category.builder().id(3L).build())
                .createdOn(LocalDateTime.now())
                .description("Testing description")
                .eventDate(LocalDateTime.now().plusDays(1))
                .initiator(user2)
                .locationLon(10.10)
                .locationLat(12.1)
                .paid(true)
                .participantLimit(10)
                .publishedOn(LocalDateTime.now().plusHours(1))
                .requestModeration(true)
                .state(EventState.PUBLISHED)
                .title("Testing title.")
                .requests(new HashSet<>(Arrays.asList(Request.builder()
                        .id(1L)
                        .status(EventState.CONFIRMED)
                        .requester(user1)
                        .created(LocalDateTime.now())
                        .eventId(event1)
                        .build())))
                .build();
        comment1 = comment1.builder()
                .id(12L)
                .comment("WOW!!!")
                .rating(10)
                .created(LocalDateTime.now())
                .commentor(user1)
                .event(event1)
                .build();
        comment2 = Comment.builder()
                .id(19L)
                .comment("Not bad.")
                .rating(8)
                .created(LocalDateTime.now())
                .event(event2)
                .commentor(user2)
                .build();
        commentDtoOut1 = CommentDtoOut.builder()
                .id(comment1.getId())
                .comment(comment1.getComment())
                .rating(comment1.getRating())
                .created(comment1.getCreated())
                .commentor(userDto1)
                .event(comment1.getEvent().getId())
                .build();
        commentDtoOut2 = CommentDtoOut.builder()
                .id(comment2.getId())
                .comment(comment2.getComment())
                .rating(comment2.getRating())
                .created(comment2.getCreated())
                .commentor(userDto2)
                .event(comment2.getEvent().getId())
                .build();
        commentDtoIn = CommentDtoIn.builder()
                .comment(commentDtoOut1.getComment())
                .rating(commentDtoOut1.getRating())
                .build();
    }

    @Test
    void createComment() {
        when(commonService.getUserInDb(anyLong()))
                .thenReturn(user1);
        when(commonService.getEventInDb(anyLong()))
                .thenReturn(event1);
        when(commentRepository.findAllByCommentorAndEvent(any(User.class), any(Event.class)))
                .thenReturn(Collections.emptyList());
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment1);
        CommentDtoOut testCommOut = commentService.createComment(user1.getId(), event1.getId(), commentDtoIn);
        assertThat(testCommOut, equalTo(commentDtoOut1));
        verify(commonService, times(1))
                .getUserInDb(anyLong());
        verify(commonService, times(1))
                .getEventInDb(anyLong());
        verify(commentRepository, times(1))
                .findAllByCommentorAndEvent(any(User.class), any(Event.class));
        verify(commentRepository, times(1))
                .save(any(Comment.class));
    }

    @Test
    void patchComment() {
        commentDtoIn.setComment("Wah Wah Wah!!!");
        Comment patchComment = comment1;
        patchComment.setComment("Wah Wah Wah!!!");
        commentDtoOut1.setComment("Wah Wah Wah!!!");
        when(commonService.getUserInDb(anyLong()))
                .thenReturn(user1);
        when(commonService.getCommentInDb(anyLong()))
                .thenReturn(comment1);
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(patchComment);
        CommentDtoOut testCommOut = commentService.patchComment(user1.getId(), comment1.getId(), commentDtoIn);
        assertThat(testCommOut, equalTo(commentDtoOut1));
        verify(commonService, times(1))
                .getUserInDb(anyLong());
        verify(commonService, times(1))
                .getCommentInDb(anyLong());
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository, times(1))
                .save(captor.capture());
        assertThat(captor.getValue().getComment(), equalTo(commentDtoIn.getComment()));
    }

    @Test
    void deleteComment() {
        when(commonService.getUserInDb(anyLong()))
                .thenReturn(user1);
        when(commonService.getCommentInDb(anyLong()))
                .thenReturn(comment1);
        doNothing().when(commentRepository).deleteById(anyLong());
        commentService.deleteComment(comment1.getId(), user1.getId());
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(commonService, times(1))
                .getUserInDb(anyLong());
        verify(commonService, times(1))
                .getCommentInDb(anyLong());
        verify(commentRepository, times(1))
                .deleteById(captor.capture());
        assertThat(captor.getValue(), equalTo(comment1.getId()));
    }

    @Test
    void getComment() {
        when(commonService.getCommentInDb(anyLong()))
                .thenReturn(comment1);
        CommentDtoOut testCommOut = commentService.getComment(comment1.getId());
        assertThat(testCommOut, equalTo(commentDtoOut1));
        verify(commonService, times(1))
                .getCommentInDb(anyLong());
    }

    @Test
    void searchComment() {
        List<Comment> comments = new ArrayList<>();
        comments.add(comment1);
        when(commentRepository.findAllByCommentContainingIgnoreCase(anyString(), any(Pageable.class)))
                .thenReturn(comments);
        when(commonService.getPagination(anyInt(), anyInt(), any()))
                .thenReturn(PageRequest.of(0, 10));
        List<CommentDtoOut> testCommOut = commentService.searchComment("wow", 0, 10).stream()
                .collect(Collectors.toList());
        assertThat(testCommOut.get(0), equalTo(commentDtoOut1));
        verify(commonService, times(1))
                .getPagination(anyInt(), anyInt(), any());
        verify(commentRepository, times(1))
                .findAllByCommentContainingIgnoreCase(anyString(), any(Pageable.class));
    }

    @Test
    void getCommentsByRating() {
        List<CommentDtoOut> comments = new ArrayList<>();
        comments.add(commentDtoOut1);
        comments.add(commentDtoOut2);
        when(commentRepository.findAllByRating(anyInt()))
                .thenReturn(List.of(comment1))
                .thenReturn(List.of(comment2));
        List<CommentDtoOut> testCommOut = commentService.getCommentsByRating(List.of(8, 10)).stream()
                .collect(Collectors.toList());
        assertThat(testCommOut.get(0), equalTo(comments.get(0)));
        assertThat(testCommOut.get(1), equalTo(comments.get(1)));
        verify(commentRepository, times(2))
                .findAllByRating(anyInt());
    }
}
