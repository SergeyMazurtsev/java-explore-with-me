package ru.practicum.ewm.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.admin.model.Category;
import ru.practicum.ewm.admin.model.User;
import ru.practicum.ewm.events.model.Event;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiator(User user, Pageable pageable);
    List<Event> findAllByInitiator(User user);

    List<Event> findAllByStateAndAnnotationContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrTitleContainingIgnoreCaseAndAndPaid(
            EventState state, String annotation, String description, String title, Boolean paid);
    List<Event> findAllByStateAndAnnotationContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrTitleContainingIgnoreCase(
            EventState state, String annotation, String description, String title);
    List<Event> findAllByPaidAndState(Boolean paid, EventState state);

    List<Event> findAllByState(EventState published);

    List<Event> findAllByCategoryId(Category category);

    List<Event> findAllByInitiatorAndState(User user, EventState state);
}
