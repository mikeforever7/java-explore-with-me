package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static Comment mapToComment(NewCommentDto newCommentDto, Event event, User author) {
        Comment comment = new Comment();
        comment.setText(newCommentDto.getText());
        comment.setEvent(event);
        comment.setAuthor(author);
        return comment;
    }

    public static CommentDto mapToDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getEvent().getId(),
                UserMapper.mapToUserShortDto(comment.getAuthor()), comment.getCreated());
    }

    public static List<CommentDto> mapToDtoList(List<Comment> comments) {
        return comments.stream().map(CommentMapper::mapToDto).toList();
    }
}
