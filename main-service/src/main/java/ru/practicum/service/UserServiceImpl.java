package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> findUsers(List<Long> ids, int from, int size) {
        List<User> users;
        if (ids != null && !ids.isEmpty()) {
            users = userRepository.findAllByIdIn(ids);
        } else {
            Pageable pageable = PageRequest.of(from / size, size, Sort.by("id"));
            Page<User> page = userRepository.findAll(pageable);
            users = page.getContent();
        }
        return UserMapper.mapToDtoList(users);
    }

    @Override
    @Transactional
    public UserDto saveUser(NewUserRequest userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new AlreadyExistsException("Пользователь с Email " + userDto.getEmail() + " существует");
        }
        return UserMapper.mapToUserDto(userRepository.save(UserMapper.mapToUser(userDto)));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователя с id=" + id + " не существует");
        }
        userRepository.deleteById(id);
    }

}
