package ru.practicum.statistic.dto;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewStatsDto {
    private String app;
    private String uri;
    private Long hits;
}
