package ru.practicum.ewm.comments;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.admin.mapper.UserMapper;
import ru.practicum.ewm.comments.dto.CommentDtoIn;
import ru.practicum.ewm.comments.dto.CommentDtoOut;
import ru.practicum.ewm.comments.model.Comment;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    default Comment toCommentFromCommentDtoIn(CommentDtoIn commentDtoIn) {
        if (commentDtoIn == null) {
            return null;
        }
        return Comment.builder()
                .comment(commentDtoIn.getComment())
                .rating(commentDtoIn.getRating())
                .build();
    }

    default CommentDtoOut toCommentDtoOutFromComment(Comment comment) {
        if (comment == null) {
            return null;
        }
        return CommentDtoOut.builder()
                .id(comment.getId())
                .commentor(UserMapper.INSTANCE.toUserDto(comment.getCommentor()))
                .event(comment.getEvent().getId())
                .comment(comment.getComment())
                .created(comment.getCreated())
                .rating(comment.getRating())
                .build();
    }

    default void patchComment(CommentDtoIn commentDtoIn, @MappingTarget Comment comment) {
        if (commentDtoIn == null) {
            return;
        }
        if (commentDtoIn.getComment() != null) {
            comment.setComment(commentDtoIn.getComment());
        }
        if (commentDtoIn.getRating() != null) {
            comment.setRating(commentDtoIn.getRating());
        }
    }
}
