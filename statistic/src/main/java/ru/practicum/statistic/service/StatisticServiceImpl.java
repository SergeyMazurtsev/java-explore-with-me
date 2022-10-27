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
import java.util.*;
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
                            "org.hibernate.exception.ConstraintViolationException: could not execute statement",
                    "Save statistic method", "statistic category"));
        }
    }

    @Override
    public List<ViewStatsDto> getStatistic(
            LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<ViewStatsDto> viewStatsDtos = statisticRepository.findAllByCreatedOnBetween(start, end)
                .stream()
                .map(StatisticMapper.INSTATCE::toViewStatsDtoFromEndpointHit)
                .collect(Collectors.toList());
        if (uris != null) {
            viewStatsDtos = viewStatsDtos.stream()
                    .filter(element -> {
                        for (String i : uris) {
                            if (element.getUri().equals(i)) {
                                return true;
                            }
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
        }
        if (unique) {
             Set<ViewStatsDto> uniqueViewPointDto = new HashSet<>(viewStatsDtos);
             return uniqueViewPointDto.stream().collect(Collectors.toList());
        }
        return viewStatsDtos;
    }
}
