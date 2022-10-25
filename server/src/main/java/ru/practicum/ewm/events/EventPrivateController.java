package ru.practicum.ewm.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.EventDtoIn;
import ru.practicum.ewm.events.dto.EventDtoInPatch;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("/users")
public class EventPrivateController {
    private final EventService eventService;

    @GetMapping("/{userId}/events")
    public ResponseEntity<Object> getEventsUser(
            @PathVariable(name = "userId") Long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Get private events of user = {}, from = {}, size = {}", userId, from, size);
        return new ResponseEntity<>(eventService.getEventsUser(userId, from, size), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/events")
    public ResponseEntity<Object> patchEvent(
            @PathVariable(name = "userId") Long userId,
            @Valid @RequestBody EventDtoInPatch eventDtoInPatch) {
        log.info("Patch private event of user = {}, {}", userId, eventDtoInPatch);
        return new ResponseEntity<>(eventService.patchEvent(userId, eventDtoInPatch), HttpStatus.OK);
    }

    @PostMapping("/{userId}/events")
    public ResponseEntity<Object> createEvent(
            @PathVariable(name = "userId") Long userId,
            @Valid @RequestBody EventDtoIn eventDtoIn) {
        log.info("Create private event of user = {}, {}", userId, eventDtoIn);
        return new ResponseEntity<>(eventService.createEvent(userId, eventDtoIn), HttpStatus.OK);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public ResponseEntity<Object> getEventOfUser(
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "eventId") Long eventId) {
        log.info("Get private event = {}, of user = {}", eventId, userId);
        return new ResponseEntity<>(eventService.getEventOfUser(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public ResponseEntity<Object> cancelEventOfUser(
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "eventId") Long eventId) {
        log.info("Cancel event = {}, of user = {}", eventId, userId);
        return new ResponseEntity<>(eventService.cancelEventOfUser(userId, eventId), HttpStatus.OK);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public ResponseEntity<Object> getRequestsInEventOfUser(
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "eventId") Long eventId) {
        log.info("Get information of requests in event = {}, of user = {}", eventId, userId);
        return new ResponseEntity<>(eventService.getRequestsInEventOfUser(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/confirm")
    public ResponseEntity<Object> patchRequestInEventOfUserConfirm(
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "eventId") Long eventId,
            @PathVariable(name = "reqId") Long reqId) {
        log.info("Confirm request = {}, in event = {}, of user = {}", reqId, eventId, userId);
        return new ResponseEntity<>(eventService.patchRequestInEventOfUserConfirm(userId, eventId, reqId), HttpStatus.OK);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests/{reqId}/reject")
    public ResponseEntity<Object> patchRequestInEventOfUserReject(
            @PathVariable(name = "userId") Long userId,
            @PathVariable(name = "eventId") Long eventId,
            @PathVariable(name = "reqId") Long reqId) {
        log.info("Reject request = {}, in event = {}, of user = {}", reqId, eventId, userId);
        return new ResponseEntity<>(eventService.patchRequestInEventOfUserReject(userId, eventId, reqId), HttpStatus.OK);
    }
}
