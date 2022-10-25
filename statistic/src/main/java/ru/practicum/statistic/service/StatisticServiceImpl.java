package ru.practicum.statistic.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.statistic.dto.EndpointHitDto;
import ru.practicum.statistic.dto.ViewStatsDto;
import ru.practicum.statistic.mapper.StatisticMapper;
import ru.practicum.statistic.model.EndpointHit;
import ru.practicum.statistic.storage.StatisticRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
    private final StatisticRepository statisticRepository;

    @Override
    public void postStatistic(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = StatisticMapper.INSTATCE.toEndpointHitFromEndpointHitDto(endpointHitDto);
        try {
            statisticRepository.save(endpointHit);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException(String.format("could not execute statement; SQL %s; " +
                            "constraint %s; nested exception is " +
                            "org.hibernate.exception.ConstraintViolationException: could not execute statement"
                    , "Save statistic method", "statistic category"));
        }
    }

    @Override
    public List<ViewStatsDto> getStatistic(
            LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (uris == null && unique == null) {
            return statisticRepository.findAllByCreatedOnBetween(start, end)
                    .stream()
                    .map(StatisticMapper.INSTATCE::toViewStatsDtoFromEndpointHit)
                    .collect(Collectors.toList());
        } else if (uris != null && unique == null) {
            return statisticRepository.findAllByUrisAndCreatedOn(uris, start, end)
                    .stream()
                    .map(StatisticMapper.INSTATCE::toViewStatsDtoFromEndpointHit)
                    .collect(Collectors.toList());
        } else if (uris == null && unique != null) {
            return statisticRepository.findDistinctByCreatedOnBetween(start, end)
                    .stream()
                    .map(StatisticMapper.INSTATCE::toViewStatsDtoFromEndpointHit)
                    .collect(Collectors.toList());
        } else return statisticRepository.findAllDistinctByUrisAndCreatedOn(uris, start, end)
                .stream()
                .map(StatisticMapper.INSTATCE::toViewStatsDtoFromEndpointHit)
                .collect(Collectors.toList());
    }
}
