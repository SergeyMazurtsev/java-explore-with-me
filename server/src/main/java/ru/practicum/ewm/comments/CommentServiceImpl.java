package ru.practicum.ewm.comments;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.admin.model.User;
import ru.practicum.ewm.comments.dto.CommentDtoIn;
import ru.practicum.ewm.comments.dto.CommentDtoOut;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.common.CommonService;
import ru.practicum.ewm.events.EventState;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.exceptions.IntegrityViolationException;
import ru.practicum.ewm.exceptions.ValidationException;
import ru.practicum.ewm.requests.model.Request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommonService commonService;
    private final CommentRepository commentRepository;

    @Override
    public CommentDtoOut createComment(Long userId, Long eventId, CommentDtoIn commentDtoIn) {
        User user = commonService.getUserInDb(userId);
        Event event = commonService.getEventInDb(eventId);
        final val checkInDb = commentRepository.findAllByCommentorAndEvent(user, event);
        if (!checkInDb.isEmpty()) {
            throw new ValidationException("Such comment is already in base.");
        }
        Set<User> users = event.getRequests().stream()
                .filter(element -> element.getStatus().equals(EventState.CONFIRMED))
                .map(Request::getRequester)
                .collect(Collectors.toSet());
        if (!users.contains(user)) {
            throw new ValidationException("Left comments can only users with confirmed requests.");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Event can't be Pending.");
        }
        Comment comment = CommentMapper.INSTANCE.toCommentFromCommentDtoIn(commentDtoIn);
        comment.setCommentor(user);
        comment.setEvent(event);
        comment.setCreated(LocalDateTime.now());
        try {
            return CommentMapper.INSTANCE.toCommentDtoOutFromComment(commentRepository.save(comment));
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException("Error to save comment in base.");
        }
    }

    @Override
    public CommentDtoOut patchComment(Long userId, Long commId, CommentDtoIn commentDtoIn) {
        User user = commonService.getUserInDb(userId);
        Comment comment = commonService.getCommentInDb(commId);
        if (!comment.getCommentor().equals(user)) {
            throw new ValidationException("Patch comment can only commentator in comment.");
        }
        CommentMapper.INSTANCE.patchComment(commentDtoIn, comment);
        try {
            return CommentMapper.INSTANCE.toCommentDtoOutFromComment(commentRepository.save(comment));
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException("Error to save comment in base.");
        }
    }

    @Override
    public void deleteComment(Long commId, Long userId) {
        User user = commonService.getUserInDb(userId);
        Comment comment = commonService.getCommentInDb(commId);
        if (!comment.getCommentor().equals(user)) {
            throw new ValidationException("Delete comment can only commentator in comment.");
        }
        try {
            commentRepository.deleteById(commId);
        } catch (DataIntegrityViolationException e) {
            throw new IntegrityViolationException("Error to delete comment in base.");
        }
    }

    @Override
    public CommentDtoOut getComment(Long commId) {
        return CommentMapper.INSTANCE.toCommentDtoOutFromComment(commonService.getCommentInDb(commId));
    }

    @Override
    public Collection<CommentDtoOut> searchComment(String text, Integer from, Integer size) {
        List<Comment> comments = new ArrayList<>();
        if (text != null) {
            comments = commentRepository.findAllByCommentContainingIgnoreCase(text, commonService.getPagination(from, size, null));
        } else {
            comments = commentRepository.findAll(commonService.getPagination(from, size, null))
                    .stream().collect(Collectors.toList());
        }
        return comments.stream()
                .map(CommentMapper.INSTANCE::toCommentDtoOutFromComment)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<CommentDtoOut> getCommentsByRating(List<Integer> ratings) {
        List<Comment> comments = new ArrayList<>();
        for (Integer rating : ratings) {
            List<Comment> searchComment = new ArrayList<>();
            searchComment = commentRepository.findAllByRating(rating);
            if (!searchComment.isEmpty()) {
                comments.addAll(searchComment);
            }
        }
        return comments.stream()
                .map(CommentMapper.INSTANCE::toCommentDtoOutFromComment)
                .collect(Collectors.toList());
    }
}
