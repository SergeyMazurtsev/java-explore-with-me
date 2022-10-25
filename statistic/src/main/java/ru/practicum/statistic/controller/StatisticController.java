package ru.practicum.statistic.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.statistic.dto.EndpointHitDto;
import ru.practicum.statistic.service.StatisticService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
public class StatisticController {
    private final StatisticService statisticService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.OK)
    void postStatistic(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("Saving statistic from ewm = {}", endpointHitDto);
        statisticService.postStatistic(endpointHitDto);
    }

    @GetMapping("/stats")
    public ResponseEntity<Object> getStatistic(
            @RequestParam(required = false) LocalDateTime start,
            @RequestParam(required = false) LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(required = false) Boolean unique) {
        log.info("Getting statistic start = {}, end = {}, uris = {}, unique = {}", start, end, uris, unique);
        return new ResponseEntity<>(statisticService.getStatistic(start, end, uris, unique), HttpStatus.OK);
    }
}
