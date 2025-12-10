package ru.practicum.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.event.*;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.service.EventService;
import ru.practicum.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
@Slf4j
public class PrivateEventController {
    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getEvent(userId, eventId);
    }

    @GetMapping
    public List<EventShortDto> getEvents(@PathVariable Long userId, @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return eventService.getEvents(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addNewEvent(@Valid @RequestBody NewEventDto newEventDto,
                                    @PathVariable Long userId) {
        log.info("Пришел запрос на добавление собития {} от юзера с id={}", newEventDto, userId);
        return eventService.addNewEvent(userId, newEventDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto patchEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                   @RequestBody UpdateEventUserRequest eventForUpdate) {
        return eventService.patchEventByUser(userId, eventId, eventForUpdate);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getUserEventRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        return requestService.getUserEventRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult patchRequest(@PathVariable Long userId, @PathVariable Long eventId,
                                                       @RequestBody(required = false) EventRequestStatusUpdateRequest updateRequest) {
        log.info("Реквест запрос {}", updateRequest);
        return requestService.changeStatus(userId, eventId, updateRequest);
    }

    @PostMapping("/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto postComment(@PathVariable Long userId, @PathVariable Long eventId, @RequestBody NewCommentDto newCommentDto) {
        return eventService.addComment(userId, eventId, newCommentDto);
    }

    @PatchMapping("/{eventId}/comments/{commentId}")
    public CommentDto patchComment(@PathVariable Long userId, @PathVariable Long eventId,
                                   @RequestBody NewCommentDto newCommentDto, @PathVariable Long commentId) {
        return eventService.updateComment(userId, eventId, newCommentDto, commentId);
    }

    @DeleteMapping("/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId, @PathVariable Long eventId, @PathVariable Long commentId) {
        eventService.deleteComment(userId, eventId, commentId);
    }
}
