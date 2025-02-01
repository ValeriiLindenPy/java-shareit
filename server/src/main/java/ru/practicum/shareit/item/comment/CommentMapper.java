package ru.practicum.shareit.item.comment;

public class CommentMapper {

    public static CommentResponseDto toRespondDto(Comment comment) {
        return CommentResponseDto.builder()
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .id(comment.getId())
                .text(comment.getText())
                .build();
    }
}
