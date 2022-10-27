package ru.practicum.ewm.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.EventDtoOutFull;
import ru.practicum.ewm.events.dto.EventDtoOutShort;
import ru.practicum.ewm.statistics.StatisticClient;
import ru.practicum.ewm.statistics.model.EndpointHit;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventPublicController {
    private final EventService eventService;
    private final StatisticClient statisticClient;
    private final String app = "EWM";

    @GetMapping
    public ResponseEntity<Object> getPublicEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime rangeStart,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false", required = false) Boolean onlyAvailable,
            @RequestParam(required = false) EventPublicSort sort,
            @PositiveOrZero @RequestParam(defaultValue = "0", required = false) Integer from,
            @Positive @RequestParam(defaultValue = "10", required = false) Integer size,
            HttpServletRequest request) {
        log.info("Get public events with: text={}", text);
        log.info("categories={}", categories);
        log.info("paid={}, rangeStart={}, rangeEnd={}", paid, rangeStart, rangeEnd);
        log.info("onlyAvailable={}, sort={}", onlyAvailable, sort);
        log.info("from={}, size={}", from, size);
        Collection<EventDtoOutShort> result = eventService.getPublicEvents(text, categories, paid, rangeStart,
                rangeEnd, onlyAvailable, sort, from, size);
        sendStatistic(request);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getPublicEvent(@PathVariable(name = "id") Long eventId,
                                                 HttpServletRequest request) {
        log.info("Get public event id = {}", eventId);
        EventDtoOutFull result = eventService.getPublicEvent(eventId);
        sendStatistic(request);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private void sendStatistic(HttpServletRequest request) {
        log.info("Sending info to statistic");
        log.info("app = {}", app);
        log.info("endpoint path = {}", request.getRequestURI());
        log.info("client ip = {}", request.getRemoteAddr());
        statisticClient.postViewOfEvent(EndpointHit.builder()
                .app(app)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
    }
}
