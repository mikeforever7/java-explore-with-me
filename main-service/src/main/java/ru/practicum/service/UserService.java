package ru.practicum.service;

import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto saveUser(NewUserRequest userDto);

    void deleteUser(Long id);

    List<UserDto> findUsers(List<Long> ids, int from, int size);
}
