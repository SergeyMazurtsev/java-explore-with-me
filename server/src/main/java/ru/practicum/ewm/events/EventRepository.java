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

    List<Event> findAllByStateAndAnnotationContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrTitleContainingIgnoreCase(
            EventState state, String annotation, String description, String title, Pageable pageable);

    List<Event> findAllByState(EventState published, Pageable pagination);

    List<Event> findAllByCategoryId(Category category);

    Page<Event> findAll(Pageable pageable);
}
