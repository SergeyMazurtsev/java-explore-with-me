package ru.practicum.statistic.service;

import ru.practicum.statistic.dto.EndpointHitDto;
import ru.practicum.statistic.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticService {
    void postStatistic(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getStatistic(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
