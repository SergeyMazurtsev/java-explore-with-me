package ru.practicum.ewm.exceptions.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {
    private List<Object> errors;
    private String message;
    private String reason;
    private String status;
    private String timestamp;
}
