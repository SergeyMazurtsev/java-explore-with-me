package ru.practicum.ewm.compilations.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompilationDtoOut {
    private Long id;
    @NotNull
    private Set<Long> events;
    @NotNull
    private Boolean pinned;
    @NotBlank
    private String title;
}
