package ru.practicum.ewm.admin;

import ru.practicum.ewm.admin.dto.CategoryDto;
import ru.practicum.ewm.admin.dto.UserDto;
import ru.practicum.ewm.compilations.dto.CompilationDtoIn;
import ru.practicum.ewm.compilations.dto.CompilationDtoOut;
import ru.practicum.ewm.events.EventState;
import ru.practicum.ewm.events.dto.EventDtoIn;
import ru.practicum.ewm.events.dto.EventDtoOutFull;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface AdminService {
    UserDto createUser(UserDto userDto);

    Collection<UserDto> getUsers(List<Long> userIds, Integer from, Integer size);

    void deleteUser(Long userID);

    CategoryDto createCategory(CategoryDto categoryDto);

    CategoryDto patchCategory(CategoryDto categoryDto);

    void deleteCategory(Long catId);

    Collection<EventDtoOutFull> getEvents(
            List<Long> users,
            List<EventState> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Integer from,
            Integer size);

    EventDtoOutFull patchEvent(Long eventId, EventDtoIn eventDtoIn);

    EventDtoOutFull publishEvent(Long eventId);

    EventDtoOutFull rejectEvent(Long eventId);

    CompilationDtoOut createCompilation(CompilationDtoIn compilationDtoIn);

    void deleteCompilation(Long compId);

    void deleteEventFromCompilation(Long compId, Long eventId);

    void addEventToCompilation(Long compId, Long eventId);

    void unpinCompilation(Long compId);

    void pinCompilation(Long compId);
}
