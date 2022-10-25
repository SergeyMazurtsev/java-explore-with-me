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
            throw new IntegrityViolationException(String.format("could not execute statement; SQL %s; " +
                            "constraint %s; nested exception is " +
                            "org.hibernate.exception.ConstraintViolationException: could not execute statement"
                    , "Save user method", "admin category"));
        }
    }

    @Override
    public Collection<UserDto> getUsers(List<Long> userIds, Integer from, Integer size) {
        List<User> users = userIds.stream()
                .map(commonService::getUserInDb)
                .collect(Collectors.toList());
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
            throw new IntegrityViolationException(String.format("could not execute statement; SQL %s; " +
                            "constraint %s; nested exception is " +
                            "org.hibernate.exception.ConstraintViolationException: could not execute statement"
                    , "Delete user method", "admin category"));
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
            throw new IntegrityViolationException(String.format("could not execute statement; SQL %s; " +
                            "constraint %s; nested exception is " +
                            "org.hibernate.exception.ConstraintViolationException: could not execute statement"
                    , "Save category method", "admin category"));
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
            throw new IntegrityViolationException(String.format("could not execute statement; SQL %s; " +
                            "constraint %s; nested exception is " +
                            "org.hibernate.exception.ConstraintViolationException: could not execute statement"
                    , "Patch category method", "admin category"));
        }
    }

    @Override
    public void deleteCategory(Long catId) {
        Category category = commonService.getCategoryInDb(catId);
        List<Event> events = eventRepository.findAllByCategoryId(category);
        if (events.size() > 0) {
            throw new ValidationException("There is events in this Category.");
        }
        try {
            categoryRepository.deleteById(catId);
        } catch (EmptyResultDataAccessException e) {
            throw new IntegrityViolationException(String.format("could not execute statement; SQL %s; " +
                            "constraint %s; nested exception is " +
                            "org.hibernate.exception.ConstraintViolationException: could not execute statement"
                    , "Delete category method", "admin category"));
        }
    }


    @Override
    public Collection<EventDtoOutFull> getEvents(@Nullable List<Long> users, @Nullable List<EventState> states,
                                                 @Nullable List<Long> categories, @Nullable LocalDateTime rangeStart,
                                                 @Nullable LocalDateTime rangeEnd, Integer from, Integer size) {
        List<Event> events = eventRepository.findAll(commonService.getPagination(from, size, null))
                .stream().collect(Collectors.toList());
        if (users != null) {
            events = events.stream().filter(element -> {
                for (Long i : users) {
                    if (element.getInitiator().getId().equals(i)) {
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toList());
        }
        if (states != null) {
            events = events.stream().filter(element -> {
                for (EventState state : states) {
                    if (element.getState().equals(state)) {
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toList());
        }
        if (categories != null) {
            events = events.stream().filter(element -> {
                for (Long i : categories) {
                    if (element.getCategoryId().getId().equals(i)) {
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toList());
        }
        if (rangeStart != null) {
            events = events.stream().filter(element -> element.getEventDate().isAfter(rangeStart))
                    .collect(Collectors.toList());
        }
        if (rangeEnd != null) {
            events = events.stream().filter(element -> element.getEventDate().isBefore(rangeEnd))
                    .collect(Collectors.toList());
        }
        return events.stream().map(commonService::addViewsToEventFull).collect(Collectors.toList());
    }

    @Override
    public EventDtoOutFull patchEvent(Long eventId, EventDtoIn eventDtoIn) {
        Event event = commonService.getEventInDb(eventId);
        EventMapper.INSTANCE.patchAdminEvent(eventDtoIn, event);
        try {
            return commonService.addViewsToEventFull(eventRepository.save(event));
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("could not execute statement; SQL %s; " +
                            "constraint %s; nested exception is " +
                            "org.hibernate.exception.ConstraintViolationException: could not execute statement"
                    , "Patch event method", "admin category"));
        }
    }

    @Override
    public EventDtoOutFull publishEvent(Long eventId) {
        Event event = commonService.getEventInDb(eventId);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1)) ||
                !event.getState().equals(EventState.PENDING)) {
            throw new ValidationException("Only pending or canceled events can be changed");
        }
        event.setState(EventState.PUBLISHED);
        event.setPublishedOn(LocalDateTime.now());
        try {
            return commonService.addViewsToEventFull(eventRepository.save(event));
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("could not execute statement; SQL %s; " +
                            "constraint %s; nested exception is " +
                            "org.hibernate.exception.ConstraintViolationException: could not execute statement"
                    , "Patch event publish method", "admin category"));
        }
    }

    @Override
    public EventDtoOutFull rejectEvent(Long eventId) {
        Event event = commonService.getEventInDb(eventId);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1)) ||
                event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Only pending or canceled events can be changed");
        }
        event.setState(EventState.CANCELED);
        try {
            return commonService.addViewsToEventFull(eventRepository.save(event));
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("could not execute statement; SQL %s; " +
                            "constraint %s; nested exception is " +
                            "org.hibernate.exception.ConstraintViolationException: could not execute statement"
                    , "Patch event reject method", "admin category"));
        }
    }


    @Override
    public CompilationDtoOut createCompilation(CompilationDtoIn compilationDtoIn) {
        Compilation compilation = CompilationMapper.INSTANCE.toCompilationFromCompilationDroIn(compilationDtoIn);
        try {
            return CompilationMapper.INSTANCE
                    .toCompilationDtoOutFromCompilation(compilationRepository.save(compilation));
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("could not execute statement; SQL %s; " +
                            "constraint %s; nested exception is " +
                            "org.hibernate.exception.ConstraintViolationException: could not execute statement"
                    , "Save compilation method", "admin category"));
        }
    }

    @Override
    public void deleteCompilation(Long compId) {
        try {
            compilationRepository.deleteById(compId);
        } catch (EmptyResultDataAccessException e) {
            throw new IntegrityViolationException(String.format("could not execute statement; SQL %s; " +
                            "constraint %s; nested exception is " +
                            "org.hibernate.exception.ConstraintViolationException: could not execute statement"
                    , "Delete compilation method", "admin category"));
        }
    }

    @Override
    public void deleteEventFromCompilation(Long compId, Long eventId) {
        Compilation compilation = commonService.getCompilationInDb(compId);
        Optional<Event> checkEvent = compilation.getEvents().stream()
                .filter(i -> i.getId().equals(eventId)).findFirst();
        if (!checkEvent.isPresent()) {
            throw new NotFoundException("Only pending or canceled events can be changed");
        }
        Set<Event> eventSet = compilation.getEvents().stream()
                .filter(i -> !i.getId().equals(eventId))
                .collect(Collectors.toSet());
        compilation.setEvents(eventSet);
        try {
            compilationRepository.save(compilation);
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("could not execute statement; SQL %s; " +
                            "constraint %s; nested exception is " +
                            "org.hibernate.exception.ConstraintViolationException: could not execute statement"
                    , "Delete event from compilation method", "admin category"));
        }
    }

    @Override
    public void addEventToCompilation(Long compId, Long eventId) {
        Compilation compilation = commonService.getCompilationInDb(compId);
        val checkEvent = compilation.getEvents().stream()
                .filter(i -> i.getId().equals(eventId)).findFirst();
        if (checkEvent.isPresent()) {
            throw new NotFoundException("Only pending or canceled events can be changed");
        }
        Set<Event> events = compilation.getEvents();
        events.add(commonService.getEventInDb(eventId));
        compilation.setEvents(events);
        try {
            compilationRepository.save(compilation);
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("could not execute statement; SQL %s; " +
                            "constraint %s; nested exception is " +
                            "org.hibernate.exception.ConstraintViolationException: could not execute statement"
                    , "Add event to compilation method", "admin category"));
        }
    }

    @Override
    public void unpinCompilation(Long compId) {
        Compilation compilation = commonService.getCompilationInDb(compId);
        compilation.setPinned(false);
        try {
            compilationRepository.save(compilation);
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("could not execute statement; SQL %s; " +
                            "constraint %s; nested exception is " +
                            "org.hibernate.exception.ConstraintViolationException: could not execute statement"
                    , "Unpin compilation method", "admin category"));
        }
    }

    @Override
    public void pinCompilation(Long compId) {
        Compilation compilation = commonService.getCompilationInDb(compId);
        compilation.setPinned(true);
        try {
            compilationRepository.save(compilation);
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException(String.format("could not execute statement; SQL %s; " +
                            "constraint %s; nested exception is " +
                            "org.hibernate.exception.ConstraintViolationException: could not execute statement"
                    , "Pin compilation method", "admin category"));
        }
    }
}
