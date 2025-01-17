package ru.practicum.shareit.item.comment;

public class CommentMapper  {

    public static CommentRespondDto toRespondDto (Comment comment) {
        return CommentRespondDto.builder()
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .id(comment.getId())
                .text(comment.getText())
                .build();
    }
}
