package ru.practicum.ewm.comments;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.ewm.admin.dto.UserDto;
import ru.practicum.ewm.comments.dto.CommentDtoIn;
import ru.practicum.ewm.comments.dto.CommentDtoOut;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {
    private MockMvc mvc;
    @Mock
    private CommentService commentService;
    @InjectMocks
    private CommentController commentController;
    private ObjectMapper mapper = new ObjectMapper();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private String url = "/comments";
    private CommentDtoOut commentDtoOut1;
    private CommentDtoOut commentDtoOut2;
    private CommentDtoIn commentDtoIn;
    private UserDto userDto1;
    private UserDto userDto2;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(commentController).build();
        mapper.registerModule(new JavaTimeModule());
        userDto1 = UserDto.builder()
                .id(21L)
                .name("Lex")
                .email("qwerty@qqq.ru")
                .build();
        userDto2 = UserDto.builder()
                .id(32L)
                .name("Nik")
                .email("asdfgh@www.ru")
                .build();
        commentDtoOut1 = CommentDtoOut.builder()
                .id(12L)
                .comment("WOW!!!")
                .rating(10)
                .created(LocalDateTime.now())
                .commentor(userDto1)
                .event(11L)
                .build();
        commentDtoOut2 = CommentDtoOut.builder()
                .id(19L)
                .comment("Not bad.")
                .rating(8)
                .created(LocalDateTime.now())
                .event(11L)
                .commentor(userDto2)
                .build();
        commentDtoIn = CommentDtoIn.builder()
                .comment(commentDtoOut1.getComment())
                .rating(commentDtoOut1.getRating())
                .build();
    }

    @Test
    void createComment() throws Exception {
        when(commentService.createComment(anyLong(), anyLong(), any(CommentDtoIn.class)))
                .thenReturn(commentDtoOut1);
        mvc.perform(post(url + "/users/" + userDto1.getId() + "/events/" + commentDtoOut1.getEvent())
                        .content(mapper.writeValueAsString(commentDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoOut1.getId()), Long.class))
                .andExpect(jsonPath("$.comment", is(commentDtoOut1.getComment())))
                .andExpect(jsonPath("$.created", is(
                        dateTimeFormatter.format(commentDtoOut1.getCreated()))))
                .andExpect(jsonPath("$.rating", is(commentDtoOut1.getRating()), Integer.class))
                .andExpect(jsonPath("$.event", is(commentDtoOut1.getEvent()), Long.class))
                .andExpect(jsonPath("$.commentor.id", is(commentDtoOut1.getCommentor().getId()), Long.class))
                .andExpect(jsonPath("$.commentor.name", is(commentDtoOut1.getCommentor().getName())))
                .andExpect(jsonPath("$.commentor.email", is(commentDtoOut1.getCommentor().getEmail())));
        verify(commentService, times(1))
                .createComment(anyLong(), anyLong(), any(CommentDtoIn.class));

    }

    @Test
    void patchComment() throws Exception {
        commentDtoIn.setComment("Wah Wah Wah!!!");
        commentDtoOut1.setComment("Wah Wah Wah!!!");
        when(commentService.patchComment(anyLong(), anyLong(), any(CommentDtoIn.class)))
                .thenReturn(commentDtoOut1);
        mvc.perform(patch(url + "/" + commentDtoOut1.getId() + "/users/" + userDto1.getId())
                        .content(mapper.writeValueAsString(commentDtoIn))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoOut1.getId()), Long.class))
                .andExpect(jsonPath("$.comment", is(commentDtoOut1.getComment())))
                .andExpect(jsonPath("$.created", is(
                        dateTimeFormatter.format(commentDtoOut1.getCreated()))))
                .andExpect(jsonPath("$.rating", is(commentDtoOut1.getRating()), Integer.class))
                .andExpect(jsonPath("$.event", is(commentDtoOut1.getEvent()), Long.class))
                .andExpect(jsonPath("$.commentor.id", is(commentDtoOut1.getCommentor().getId()), Long.class))
                .andExpect(jsonPath("$.commentor.name", is(commentDtoOut1.getCommentor().getName())))
                .andExpect(jsonPath("$.commentor.email", is(commentDtoOut1.getCommentor().getEmail())));
        verify(commentService, times(1))
                .patchComment(anyLong(), anyLong(), any(CommentDtoIn.class));
    }

    @Test
    void deleteComment() throws Exception {
        doNothing().when(commentService).deleteComment(anyLong(), anyLong());
        mvc.perform(delete(url + "/" + commentDtoOut1.getId() + "/users/" + userDto1.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(commentService, times(1))
                .deleteComment(anyLong(), anyLong());
    }

    @Test
    void getComment() throws Exception {
        when(commentService.getComment(anyLong()))
                .thenReturn(commentDtoOut2);
        mvc.perform(get(url + "/" + commentDtoOut2.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoOut2.getId()), Long.class))
                .andExpect(jsonPath("$.comment", is(commentDtoOut2.getComment())))
                .andExpect(jsonPath("$.created", is(
                        dateTimeFormatter.format(commentDtoOut2.getCreated()))))
                .andExpect(jsonPath("$.rating", is(commentDtoOut2.getRating()), Integer.class))
                .andExpect(jsonPath("$.event", is(commentDtoOut2.getEvent()), Long.class))
                .andExpect(jsonPath("$.commentor.id", is(commentDtoOut2.getCommentor().getId()), Long.class))
                .andExpect(jsonPath("$.commentor.name", is(commentDtoOut2.getCommentor().getName())))
                .andExpect(jsonPath("$.commentor.email", is(commentDtoOut2.getCommentor().getEmail())));
        verify(commentService, times(1))
                .getComment(anyLong());
    }

    @Test
    void searchComment() throws Exception {
        List<CommentDtoOut> commentDtoOuts = new ArrayList<>();
        commentDtoOuts.add(commentDtoOut2);
        when(commentService.searchComment(anyString(), anyInt(), anyInt()))
                .thenReturn(commentDtoOuts);
        mvc.perform(get(url)
                        .param("text", "bad")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(commentDtoOut2.getId()), Long.class))
                .andExpect(jsonPath("$[0].comment", is(commentDtoOut2.getComment())))
                .andExpect(jsonPath("$[0].created", is(
                        dateTimeFormatter.format(commentDtoOut2.getCreated()))))
                .andExpect(jsonPath("$[0].rating", is(commentDtoOut2.getRating()), Integer.class))
                .andExpect(jsonPath("$[0].event", is(commentDtoOut2.getEvent()), Long.class))
                .andExpect(jsonPath("$[0].commentor.id", is(commentDtoOut2.getCommentor().getId()), Long.class))
                .andExpect(jsonPath("$[0].commentor.name", is(commentDtoOut2.getCommentor().getName())))
                .andExpect(jsonPath("$[0].commentor.email", is(commentDtoOut2.getCommentor().getEmail())));
        verify(commentService, times(1))
                .searchComment(anyString(), anyInt(), anyInt());
    }

    @Test
    void getCommentsByRating() throws Exception {
        List<CommentDtoOut> commentDtoOuts = new ArrayList<>();
        commentDtoOuts.add(commentDtoOut1);
        commentDtoOuts.add(commentDtoOut2);
        when(commentService.getCommentsByRating(anyList()))
                .thenReturn(commentDtoOuts);
        mvc.perform(get(url + "/rating")
                        .param("ratings", "8,10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(commentDtoOut1.getId()), Long.class))
                .andExpect(jsonPath("$[0].comment", is(commentDtoOut1.getComment())))
                .andExpect(jsonPath("$[0].created", is(
                        dateTimeFormatter.format(commentDtoOut1.getCreated()))))
                .andExpect(jsonPath("$[0].rating", is(commentDtoOut1.getRating()), Integer.class))
                .andExpect(jsonPath("$[0].event", is(commentDtoOut1.getEvent()), Long.class))
                .andExpect(jsonPath("$[0].commentor.id", is(commentDtoOut1.getCommentor().getId()), Long.class))
                .andExpect(jsonPath("$[0].commentor.name", is(commentDtoOut1.getCommentor().getName())))
                .andExpect(jsonPath("$[0].commentor.email", is(commentDtoOut1.getCommentor().getEmail())))
                .andExpect(jsonPath("$[1].id", is(commentDtoOut2.getId()), Long.class))
                .andExpect(jsonPath("$[1].comment", is(commentDtoOut2.getComment())))
                .andExpect(jsonPath("$[1].created", is(
                        dateTimeFormatter.format(commentDtoOut2.getCreated()))))
                .andExpect(jsonPath("$[1].rating", is(commentDtoOut2.getRating()), Integer.class))
                .andExpect(jsonPath("$[1].event", is(commentDtoOut2.getEvent()), Long.class))
                .andExpect(jsonPath("$[1].commentor.id", is(commentDtoOut2.getCommentor().getId()), Long.class))
                .andExpect(jsonPath("$[1].commentor.name", is(commentDtoOut2.getCommentor().getName())))
                .andExpect(jsonPath("$[1].commentor.email", is(commentDtoOut2.getCommentor().getEmail())));
        verify(commentService, times(1))
                .getCommentsByRating(anyList());

    }
}