package ru.practicum.ewm.statistics;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.statistics.model.EndpointHit;
import ru.practicum.ewm.statistics.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Component
@FeignClient(name = "${stat-server}", url = "${stat-server.url}")
public interface StatisticClient {
    @GetMapping(path = "/stats")
    @ResponseStatus(HttpStatus.OK)
    List<ViewStats> getViewOfEvent(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(required = false) Boolean unique);

    @PostMapping(path = "/hit")
    @ResponseStatus(HttpStatus.OK)
    void postViewOfEvent(@RequestBody EndpointHit endpointHit);
}
