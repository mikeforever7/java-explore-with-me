package ru.practicum.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {

    @NotBlank(message = "не может быть пустым")
    @Size(min = 2, max = 250, message = "Имя пользователя должно быть от 2 до 250 символов")
    private String name;

    @Email
    @NotBlank(message = "не может быть пустым")
    @Size(min = 6, max = 254, message = "Имя пользователя должно быть от 6 до 254 символов")
    private String email;
}
