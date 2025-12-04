package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.service.StatService;


import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatController {
    private final StatService statService;

    @PostMapping(path = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto addHit(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        log.info("Пробуем добавить просмотр события hit= {}", endpointHitDto);
        return statService.addHit(endpointHitDto);
    }

    @GetMapping(path = "/stats")
    public List<ViewStatsDto> searchStats(
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("Получен запрос: start={}, end={}, uris={}, unique={}", start, end, uris, unique);

        return statService.searchStats(start, end, uris, unique);
    }

}
