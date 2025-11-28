package ru.practicum.service;


import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    EndpointHitDto addHit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> searchStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
