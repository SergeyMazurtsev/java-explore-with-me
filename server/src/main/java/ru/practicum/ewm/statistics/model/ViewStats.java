package ru.practicum.ewm.statistics.model;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewStats {
    private String app;
    private String uri;
    private Long hits;
}
