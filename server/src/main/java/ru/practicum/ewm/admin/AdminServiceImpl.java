package ru.practicum.ewm.admin;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.admin.dto.CategoryDto;
import ru.practicum.ewm.admin.dto.UserDto;
import ru.practicum.ewm.admin.mapper.CategoryMapper;
import ru.practicum.ewm.admin.mapper.UserMapper;
import ru.practicum.ewm.admin.model.Category;
import ru.practicum.ewm.admin.model.User;
import ru.practicum.ewm.categories.CategoryRepository;
import ru.practicum.ewm.common.CommonService;
import ru.practicum.ewm.compilations.CompilationRepository;
import ru.practicum.ewm.compilations.dto.CompilationDtoIn;
import ru.practicum.ewm.compilations.dto.CompilationDtoOut;
import ru.practicum.ewm.compilations.mapper.CompilationMapper;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.events.EventRepository;
import ru.practicum.ewm.events.EventState;
import ru.practicum.ewm.events.dto.EventDtoIn;
import ru.practicum.ewm.events.dto.EventDtoOutFull;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.exceptions.IntegrityViolationException;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.exceptions.ValidationException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;
    private final CommonService commonService;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.INSTANCE.toUser(userDto);
        try {
            return UserMapper.INSTANCE.toUserDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("Error with method %s in %s",
                    "Save user method", "admin category"));
        }
    }

    @Override
    public Collection<UserDto> getUsers(List<Long> userIds, Integer from, Integer size) {
        List<User> users = new ArrayList<>();
        for (Long userId : userIds) {
            Optional<User> check = userRepository.findById(userId);
            if (!check.isPresent()) {
                continue;
            }
            users.add(check.get());
        }
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        Page<User> userPage = new PageImpl<User>(
                users, commonService.getPagination(from, size, null), users.size());
        return userPage.stream().map(UserMapper.INSTANCE::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Long userId) {
        try {
            userRepository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new IntegrityViolationException(String.format("Error with method %s in %s",
                    "Delete user method", "admin category"));
        }
    }

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = CategoryMapper.INSTANCE.toCategory(categoryDto);
        List<Category> categories = categoryRepository.findAllByName(category.getName());
        if (categories.size() > 0) {
            throw new ValidationException("Name is not unique.");
        }
        try {
            return CategoryMapper.INSTANCE.toCategoryDto(categoryRepository.save(category));
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("Error with method %s in %s",
                    "Save category method", "admin category"));
        }
    }

    @Override
    public CategoryDto patchCategory(CategoryDto categoryDto) {
        Category category = commonService.getCategoryInDb(categoryDto.getId());
        List<Category> categories = categoryRepository.findAllByName(category.getName());
        CategoryMapper.INSTANCE.patchingCategory(categoryDto, category);
        try {
            return CategoryMapper.INSTANCE.toCategoryDto(categoryRepository.save(category));
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("Error with method %s in %s",
                    "Patch category method", "admin category"));
        }
    }

    @Override
    public void deleteCategory(Long catId) {
        Category category = commonService.getCategoryInDb(catId);
        List<Event> events = eventRepository.findAllByCategoryId(category);
        if (events.size() > 0) {
            throw new ValidationException("There is no events in this Category.");
        }
        try {
            categoryRepository.deleteById(catId);
        } catch (EmptyResultDataAccessException e) {
            throw new IntegrityViolationException(String.format("Error with method %s in %s",
                    "Delete category method", "admin category"));
        }
    }


    @Override
    public Collection<EventDtoOutFull> getEvents(@Nullable List<Long> users, @Nullable List<EventState> states,
                                                 @Nullable List<Long> categories, @Nullable LocalDateTime rangeStart,
                                                 @Nullable LocalDateTime rangeEnd, Integer from, Integer size) {
        List<Event> events = new ArrayList<>();
        if (users != null) {
            if (states != null) {
                for (Long userId : users) {
                    for (EventState state : states) {
                        val elements = eventRepository.findAllByInitiatorAndState(
                                commonService.getUserInDb(userId), state);
                        if (!elements.isEmpty()) {
                            events.addAll(elements);
                        }
                    }
                }
            } else {
                for (Long userId : users) {
                    val elements = eventRepository.findAllByInitiator(
                            commonService.getUserInDb(userId));
                    if (!elements.isEmpty()) {
                        events.addAll(elements);
                    }
                }
            }
        } else if (states != null) {
            for (EventState state : states) {
                val elements = eventRepository.findAllByState(state);
                if (!elements.isEmpty()) {
                    events.addAll(elements);
                }
            }
        } else {
            events = eventRepository.findAll();
        }
        events = commonService.filterEventsByCategory(events, categories);
        events = commonService.filterEventsByRangeStart(events, rangeStart);
        events = commonService.filterEventsByRangeEnd(events, rangeEnd);
        return new PageImpl<>(events.stream().map(commonService::addViewsToEventFull).collect(Collectors.toList()),
                commonService.getPagination(from, size, null), events.size()).stream()
                .collect(Collectors.toList());
    }

    @Override
    public EventDtoOutFull patchEvent(Long eventId, EventDtoIn eventDtoIn) {
        Event event = commonService.getEventInDb(eventId);
        EventMapper.INSTANCE.patchAdminEvent(eventDtoIn, event);
        try {
            return commonService.addViewsToEventFull(eventRepository.save(event));
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("Error with method %s in %s",
                    "Patch event method", "admin category"));
        }
    }

    @Override
    public EventDtoOutFull publishEvent(Long eventId) {
        Event event = commonService.getEventInDb(eventId);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1)) ||
                !event.getState().equals(EventState.PENDING)) {
            throw new ValidationException("Only pending events can be publish");
        }
        event.setState(EventState.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
        try {
            return commonService.addViewsToEventFull(eventRepository.save(event));
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("Error with method %s in %s",
                    "Patch event publish method", "admin category"));
        }
    }

    @Override
    public EventDtoOutFull rejectEvent(Long eventId) {
        Event event = commonService.getEventInDb(eventId);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1)) ||
                event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Only pending events can be publish");
        }
        event.setState(EventState.CANCELED);
        try {
            return commonService.addViewsToEventFull(eventRepository.save(event));
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("Error with method %s in %s",
                    "Patch event reject method", "admin category"));
        }
    }


    @Override
    public CompilationDtoOut createCompilation(CompilationDtoIn compilationDtoIn) {
        Compilation compilation = CompilationMapper.INSTANCE.toCompilationFromCompilationDroIn(compilationDtoIn);
        Set<Event> events = compilationDtoIn.getEvents().stream()
                .map(commonService::getEventInDb).collect(Collectors.toSet());
        compilation.setEvents(events);
        try {
            return CompilationMapper.INSTANCE
                    .toCompilationDtoOutFromCompilation(compilationRepository.save(compilation));
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("Error with method %s in %s",
                    "Save compilation method", "admin category"));
        }
    }

    @Override
    public void deleteCompilation(Long compId) {
        try {
            compilationRepository.deleteById(compId);
        } catch (EmptyResultDataAccessException e) {
            throw new IntegrityViolationException(String.format("Error with method %s in %s",
                    "Delete compilation method", "admin category"));
        }
    }

    @Override
    public void deleteEventFromCompilation(Long compId, Long eventId) {
        Compilation compilation = commonService.getCompilationInDb(compId);
        Optional<Event> checkEvent = compilation.getEvents().stream()
                .filter(i -> i.getId().equals(eventId)).findFirst();
        if (!checkEvent.isPresent()) {
            throw new NotFoundException("There is no this event in compilation");
        }
        Set<Event> eventSet = compilation.getEvents().stream()
                .filter(i -> !i.getId().equals(eventId))
                .collect(Collectors.toSet());
        compilation.setEvents(eventSet);
        try {
            compilationRepository.save(compilation);
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("Error with method %s in %s",
                    "Delete event from compilation method", "admin category"));
        }
    }

    @Override
    public void addEventToCompilation(Long compId, Long eventId) {
        Compilation compilation = commonService.getCompilationInDb(compId);
        val checkEvent = compilation.getEvents().stream()
                .filter(i -> i.getId().equals(eventId)).findFirst();
        if (checkEvent.isPresent()) {
            throw new NotFoundException("This event is already in compilation");
        }
        Set<Event> events = compilation.getEvents();
        events.add(commonService.getEventInDb(eventId));
        compilation.setEvents(events);
        try {
            compilationRepository.save(compilation);
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("Error with method %s in %s",
                    "Add event to compilation method", "admin category"));
        }
    }

    @Override
    public void unpinCompilation(Long compId) {
        Compilation compilation = commonService.getCompilationInDb(compId);
        compilation.setPinned(false);
        try {
            compilationRepository.save(compilation);
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("Error with method %s in %s",
                    "Unpin compilation method", "admin category"));
        }
    }

    @Override
    public void pinCompilation(Long compId) {
        Compilation compilation = commonService.getCompilationInDb(compId);
        compilation.setPinned(true);
        try {
            compilationRepository.save(compilation);
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("Error with method %s in %s",
                    "Pin compilation method", "admin category"));
        }
    }
}
