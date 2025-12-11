package ru.practicum.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.event.*;
import ru.practicum.enums.EventSortBy;
import ru.practicum.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto addNewEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEvent(Long userId, Long eventId);

    EventFullDto findEventById(Long id, HttpServletRequest request);

    List<EventShortDto> getEvents(Long userId, Integer from, Integer size);

    List<EventShortDto> searchAvailableItemByText(
            Integer from, Integer size, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
            LocalDateTime rangeEnd, Boolean onlyAvailable, EventSortBy sort, String text, HttpServletRequest request);

    List<EventFullDto> searchFullEventForAdmin(
            Integer from, Integer size, List<Long> initiators, List<EventState> states,
            List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd);

    EventFullDto patchEvent(Long eventId, UpdateEventAdminRequest eventForUpdate);

    EventFullDto patchEventByUser(Long userId, Long eventId, UpdateEventUserRequest eventForUpdate);

    CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateComment(Long userId, Long eventId, NewCommentDto newCommentDto, Long commentId);

    void deleteComment(Long userId, Long eventId, Long commentId);

    List<CommentDto> getAllEventComments(Long id);
}
