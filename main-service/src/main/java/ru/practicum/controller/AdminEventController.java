package ru.practicum.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.UpdateEventAdminRequest;
import ru.practicum.enums.EventState;
import ru.practicum.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {
    private final EventService eventService;

    @PatchMapping("/{eventId}")
    public EventFullDto patchEvent(@PathVariable Long eventId, @RequestBody UpdateEventAdminRequest eventForUpdate) {
        return eventService.patchEvent(eventId, eventForUpdate);
    }

    @GetMapping
    public List<EventFullDto> searchFullEventForAdmin(
            @PositiveOrZero @RequestParam(required = false, name = "from", defaultValue = "0") int from,
            @Positive @RequestParam(required = false, name = "size", defaultValue = "10") int size,
            @RequestParam(required = false) List<Long> initiators,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd) {
        return eventService.searchFullEventForAdmin(from, size, initiators, states, categories, rangeStart, rangeEnd);
    }

}
