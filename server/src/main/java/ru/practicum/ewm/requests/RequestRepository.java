package ru.practicum.ewm.requests;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.admin.model.User;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.requests.model.Request;

import java.util.Collection;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Collection<Request> findAllByRequesterAndEventId(User user, Event event);

    Collection<Request> findAllByRequester(User user);

    Collection<Request> findAllByEventId(Event event);
}
