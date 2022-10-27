package ru.practicum.statistic.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.statistic.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.Collection;

public interface StatisticRepository extends JpaRepository<EndpointHit, Long> {
    Collection<EndpointHit> findAllByCreatedOnBetween(LocalDateTime start, LocalDateTime end);
}
