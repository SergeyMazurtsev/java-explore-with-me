package ru.practicum.statistic.model;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Attributes implements Serializable {
    private String uri;
    private String ip;
}
