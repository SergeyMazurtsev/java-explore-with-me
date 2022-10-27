package ru.practicum.ewm.comments;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comments.dto.CommentDtoIn;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/users/{userId}/events/{eventId}")
    public ResponseEntity<Object> createComment(
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "eventId") Long eventId,
            @Valid @RequestBody CommentDtoIn commentDtoIn) {
        log.info("Create comment for event id = {}, by user id = {}", eventId, userId);
        log.info("with comment = {}", commentDtoIn);
        return new ResponseEntity<>(commentService.createComment(userId, eventId, commentDtoIn), HttpStatus.OK);
    }

    @PatchMapping("/{commId}/users/{userId}")
    public ResponseEntity<Object> patchComment(
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "commId") Long commId,
            @Valid @RequestBody CommentDtoIn commentDtoIn) {
        log.info("Patching comment id = {}, by user id = {}", commId, userId);
        log.info("Patching comment = {}", commentDtoIn);
        return new ResponseEntity<>(commentService.patchComment(userId, commId, commentDtoIn), HttpStatus.OK);
    }

    @DeleteMapping("/{commId}/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteComment(
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "commId") Long commId) {
        log.info("Delete comment id = {}, by user id = {}", commId, userId);
        commentService.deleteComment(commId, userId);
    }

    @GetMapping("/{commId}")
    public ResponseEntity<Object> getComment(
            @PathVariable(name = "commId") Long commId) {
        log.info("Get comment id = {}", commId);
        return new ResponseEntity<>(commentService.getComment(commId), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Object> searchComment(
            @RequestParam(name = "text", required = false) String text,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get comments with search text in comment = {}", text);
        log.info("with pagination from = {}, size = {}", from, size);
        return new ResponseEntity<>(commentService.searchComment(text, from, size), HttpStatus.OK);
    }

    @GetMapping("/rating")
    public ResponseEntity<Object> getCommentsByRating(
            @RequestParam(name = "ratings") List<Integer> ratings) {
        log.info("Get comments by ratings = {}", ratings);
        return new ResponseEntity<>(commentService.getCommentsByRating(ratings), HttpStatus.OK);
    }
}
