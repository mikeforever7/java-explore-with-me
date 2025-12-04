package ru.practicum.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.event.*;
import ru.practicum.enums.EventSortBy;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto addNewEvent(Long userId, NewEventDto newEventDto);
    EventFullDto getEvent(Long userId, Long eventId);
    EventFullDto findEventById(Long id, HttpServletRequest request);
    List<EventShortDto> getEvents(Long userId, Integer from, Integer size);
    List<EventShortDto> searchAvailableItemByText(
                                                  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                  @RequestParam List<Integer> categories,
                                                  @RequestParam Boolean paid,
                                                  @RequestParam LocalDateTime rangeStart,
                                                  @RequestParam LocalDateTime rangeEnd,
                                                  @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                  @RequestParam EventSortBy sort,
                                                  @RequestParam String text);
    EventFullDto patchEvent(Long eventId, UpdateEventAdminRequest eventForUpdate);
    EventFullDto patchEventByUser(Long userId, Long eventId, UpdateEventUserRequest eventForUpdate);
}
