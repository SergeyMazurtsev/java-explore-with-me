package ru.practicum.ewm.events.dto;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location {
    private Double lat;
    private Double lon;
}
