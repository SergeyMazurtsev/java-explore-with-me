package ru.practicum.ewm.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.EventState;
import ru.practicum.ewm.events.dto.EventDtoIn;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping("/admin/events")
public class AdminEventController {
    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<Object> getEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime rangeStart,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
            @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Get admin events, by users={}", users);
        log.info("states={}", states);
        log.info("categories={}", categories);
        log.info("rangeStart={}, rangeEnd={}", rangeStart, rangeEnd);
        log.info("from={}, size={}", from, size);
        return new ResponseEntity<>(adminService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size),
                HttpStatus.OK);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<Object> patchEvent(
            @PathVariable Long eventId,
            @RequestBody EventDtoIn eventDtoIn) {
        log.info("Put admin event id = {}", eventId);
        log.info("event = {}", eventDtoIn);
        return new ResponseEntity<>(adminService.patchEvent(eventId, eventDtoIn), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}/publish")
    public ResponseEntity<Object> publishEvent(@PathVariable Long eventId) {
        log.info("Publish admin event id = {}", eventId);
        return new ResponseEntity<>(adminService.publishEvent(eventId), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}/reject")
    public ResponseEntity<Object> rejectEvent(@PathVariable Long eventId) {
        log.info("Reject admin event id = {}", eventId);
        return new ResponseEntity<>(adminService.rejectEvent(eventId), HttpStatus.OK);
    }
}
