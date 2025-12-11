package ru.practicum.dto.comment;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.dto.user.UserShortDto;


import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private Long id;

    private String text;

    private Long eventId;

    private UserShortDto author;

    private LocalDateTime created;

}
