package ru.practicum.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    private List<Long> events;

    @NotNull
    private Boolean pinned = false;

    @NotBlank(message = "не может быть пустым")
    @Size(max = 50, message = "Заголовок должен быть до 50 символов")
    private String title;
}
