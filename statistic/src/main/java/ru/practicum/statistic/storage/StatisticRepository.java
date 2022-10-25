package ru.practicum.statistic.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.statistic.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface StatisticRepository extends JpaRepository<EndpointHit, Long> {
    Collection<EndpointHit> findAllByCreatedOnBetween(LocalDateTime start, LocalDateTime end);

    @Query(value = "select stat.* from statistic stat where stat.attributes ->> 'uri' = ?1 " +
            "and  stat.created_on >= start and stat.created_on <= end", nativeQuery = true)
    Collection<EndpointHit> findAllByUrisAndCreatedOn(List<String> uris, LocalDateTime start, LocalDateTime end);

    Collection<EndpointHit> findDistinctByCreatedOnBetween(LocalDateTime start, LocalDateTime end);

    @Query(value = "select distinct stat.* from statistic stat where stat.attributes ->> 'uri' = ?1 " +
            "and  stat.created_on >= start and stat.created_on <= end", nativeQuery = true)
    Collection<EndpointHit> findAllDistinctByUrisAndCreatedOn(List<String> uris, LocalDateTime start, LocalDateTime end);
}
