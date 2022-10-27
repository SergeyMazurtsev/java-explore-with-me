package ru.practicum.ewm.comments;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.admin.model.User;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.events.model.Event;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByCommentContainingIgnoreCase(String text, Pageable page);

    List<Comment> findAllByRating(Integer rating);

    List<Comment> findAllByCommentorAndEvent(User user, Event event);
}
