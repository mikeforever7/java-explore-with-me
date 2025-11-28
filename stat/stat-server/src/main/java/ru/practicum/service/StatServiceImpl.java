package ru.practicum.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.mapper.EndpointHitMapper;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;

    @Override
    public EndpointHitDto addHit(EndpointHitDto endpointHitDto) {

        return EndpointHitMapper.mapToEndpointHitDto(
                statRepository.save(EndpointHitMapper.mapToEndpointHit(endpointHitDto)));
    }

    @Override
    public List<ViewStatsDto> searchStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (end.isBefore(start)) {
            throw new ValidationException("Неверные даты для выборки");
        }
        if (uris == null || uris.isEmpty()) {
            if (unique) {
                log.info("Выводим статистику для уникальных {} {} без uris {}", start, end, uris);
                return statRepository.findUniqueStatsWithoutUris(start, end);
            } else {
                log.info("Выводим всю статистику {} {} без {}", start, end, uris);
                return statRepository.findStatsWithoutUris(start, end);
            }
        } else {
            if (unique) {
                log.info("Выводим статистику для уникальных {} {} для {}", start, end, uris);
                return statRepository.findUniqueStats(start, end, uris);
            } else {
                log.info("Выводим всю статистику {} {} для {}", start, end, uris);
                return statRepository.findStats(start, end, uris);
            }
        }
    }
}
