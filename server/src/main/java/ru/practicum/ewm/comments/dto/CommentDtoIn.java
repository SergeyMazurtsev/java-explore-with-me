package ru.practicum.ewm.comments.dto;

import lombok.*;
import ru.practicum.ewm.exceptions.ValidationException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDtoIn {
    @NotBlank
    private String comment;
    @NotNull
    private Integer rating;

    public void setRating(Integer newRating) {
        if (newRating >= 1 && newRating <= 10) {
            rating = newRating;
        } else {
            throw new ValidationException("Rating can be from 0 to 10.");
        }
    }
}
