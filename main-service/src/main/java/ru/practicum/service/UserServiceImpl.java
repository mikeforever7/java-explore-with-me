package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatClient;
import ru.practicum.dto.user.NewUserRequest;
import ru.practicum.dto.user.UserDto;
import ru.practicum.exception.AlreadyExistsException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final StatClient statClient;

    @Override
    public List<UserDto> findUsers(List<Long> ids, int from, int size) {
        List<User> users;
        if (ids != null && !ids.isEmpty()) {
            users = userRepository.findAllByIdIn(ids);
        } else {
            Pageable pageable = PageRequest.of(from / size, size);
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
//        String dateString = "2022-09-06 11:00:23";
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        LocalDateTime timestamp = LocalDateTime.parse(dateString, formatter);
//        EndpointHitDto endpointHitDto = new EndpointHitDto();
//        EndpointHitDto endpointHitDto2 = new EndpointHitDto();
//        endpointHitDto.setTimestamp(timestamp);
//        endpointHitDto2.setTimestamp(LocalDateTime.now());
//        endpointHitDto.setUri("Первая ДТО");
//        endpointHitDto2.setUri("Вторая ДТО");
//        endpointHitDto.setIp("asdf");
//        endpointHitDto2.setIp("asdf");
//        endpointHitDto.setApp("aaaaa");
//        endpointHitDto2.setApp("aaaaa");
//        statClient.addHit(endpointHitDto);
//        statClient.addHit(endpointHitDto2);

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
