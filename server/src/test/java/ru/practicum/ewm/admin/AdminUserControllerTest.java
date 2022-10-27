package ru.practicum.ewm.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AdminUserControllerTest {
    private MockMvc mvc;
    @Mock
    private AdminService adminService;
    @InjectMocks
    private AdminUserController adminUserController;

    private ObjectMapper mapper = new ObjectMapper();

    private UserDto userDto1;
    private UserDto userDto2;
    private final String url = "/admin/users";

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(adminUserController).build();
        userDto1 = UserDto.builder()
                .id(1L)
                .name("Test1")
                .email("qwerty@qqq.ru")
                .build();
        userDto2 = UserDto.builder()
                .id(2L)
                .name("Test2")
                .email("wertyu@www.ru")
                .build();
    }

    @Test
    void createUser() throws Exception {
        when(adminService.createUser(any(UserDto.class)))
                .thenReturn(userDto1);
        mvc.perform(post(url)
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto1.getName())))
                .andExpect(jsonPath("$.email", is(userDto1.getEmail())));

        verify(adminService, times(1))
                .createUser(any(UserDto.class));
    }

    @Test
    void getUsers() throws Exception {
        Collection<UserDto> userDtos = Stream.of(userDto1, userDto2)
                .sorted(Comparator.comparing(UserDto::getId)).collect(Collectors.toList());
        when(adminService.getUsers(anyList(), anyInt(), anyInt()))
                .thenReturn(userDtos);
        mvc.perform(get(url)
                        .param("ids", "1,2")
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(userDto1.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto1.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto1.getEmail())))
                .andExpect(jsonPath("$[1].id", is(userDto2.getId()), Long.class));
        verify(adminService, times(1))
                .getUsers(anyList(), anyInt(), anyInt());
    }

    @Test
    void deleteUser() throws Exception {
        doNothing().when(adminService).deleteUser(anyLong());
        mvc.perform(delete(url + "/{userId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(adminService, times(1))
                .deleteUser(anyLong());
    }
}
