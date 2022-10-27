package ru.practicum.ewm.comments;

import ru.practicum.ewm.comments.dto.CommentDtoIn;
import ru.practicum.ewm.comments.dto.CommentDtoOut;

import java.util.Collection;
import java.util.List;

public interface CommentService {
    CommentDtoOut createComment(Long userId, Long eventId, CommentDtoIn commentDtoIn);

    CommentDtoOut patchComment(Long userId, Long commId, CommentDtoIn commentDtoIn);

    void deleteComment(Long commId, Long userId);

    CommentDtoOut getComment(Long commId);

    Collection<CommentDtoOut> searchComment(String text, Integer from, Integer size);

    Collection<CommentDtoOut> getCommentsByRating(List<Integer> ratings);
}
