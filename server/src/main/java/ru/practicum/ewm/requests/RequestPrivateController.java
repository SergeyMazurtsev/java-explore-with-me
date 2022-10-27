package ru.practicum.ewm.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("/users")
public class RequestPrivateController {
    private final RequestService requestService;

    @GetMapping("/{userId}/requests")
    public ResponseEntity<Object> getRequestsOfUser(@PathVariable(name = "userId") Long userId) {
        log.info("Get requests of user id = {}", userId);
        return new ResponseEntity<>(requestService.getRequestsOfUser(userId), HttpStatus.OK);
    }

    @PostMapping("/{userId}/requests")
    public ResponseEntity<Object> createRequestForUser(
            @PathVariable(name = "userId") Long userId,
            @RequestParam(name = "eventId") Long eventId) {
        log.info("Create request to event = {}", eventId);
        log.info("from user id = {}", userId);
        return new ResponseEntity<>(requestService.createRequestForUser(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ResponseEntity<Object> cancelRequestByUser(
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "requestId") Long requestId) {
        log.info("Cancel request id = {}", requestId);
        log.info("by user id = {}", userId);
        return new ResponseEntity<>(requestService.cancelRequestByUser(userId, requestId), HttpStatus.OK);
    }
}
