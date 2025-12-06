package ru.practicum.service;

import com.querydsl.core.BooleanBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatClient;
import ru.practicum.dto.event.*;
import ru.practicum.enums.AdminStateAction;
import ru.practicum.enums.EventSortBy;
import ru.practicum.enums.EventState;
import ru.practicum.enums.UserStateAction;
import ru.practicum.exception.EventDateTooSoonException;
import ru.practicum.exception.EventStateException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.QEvent;
import ru.practicum.model.User;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final StatClient statClient;

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("События с id=" + eventId + " созданного юзером с id=" + userId + "не найдено"));
        return EventMapper.mapToFullDto(event);
    }

    @Override
    public List<EventShortDto> getEvents(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Event> page = eventRepository.findByInitiatorId(userId, pageable);
        List<Event> events = page.getContent();
        return EventMapper.mapToShortDtoList(events);
    }

    @Override
    @Transactional
    public EventFullDto addNewEvent(Long userId, NewEventDto newEventDto) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория с id=" + newEventDto.getCategory() + " не найдена"));
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата события должна быть не раньше, чем через 2 часа от текущего времени");
        }
        Event event = EventMapper.mapToEvent(newEventDto, initiator, category);
        return EventMapper.mapToFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto patchEvent(Long eventId, UpdateEventAdminRequest updateRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id=" + eventId + "не найдено"));
        LocalDateTime newEventDate = updateRequest.getEventDate();

        if (newEventDate != null) {
            if (newEventDate.isBefore(LocalDateTime.now())) {
                throw new ValidationException("Дата события должна быть не раньше текущего времени");
            }
            event.setEventDate(updateRequest.getEventDate());
        }
        AdminStateAction action = updateRequest.getStateAction();
        if (action != null) {
            switch (action) {
                case PUBLISH_EVENT:
                    if (event.getEventState() != EventState.PENDING) {
                        throw new EventStateException("Опубликовать можно только событие в состоянии ожидания");
                    }
                    if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                        throw new EventDateTooSoonException("Дата события должна быть не раньше, чем через 1 час от текущего времени");
                    }
                    event.setEventState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    if (event.getEventState() == EventState.PUBLISHED) {
                        throw new EventStateException("Можно отклонить только неопубликованное событие");
                    }
                    event.setEventState(EventState.CANCELED);
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестное действие: " + action);
            }
        }
        if (StringUtils.hasText(updateRequest.getAnnotation())) {
            if (updateRequest.getAnnotation().length() < 20 || updateRequest.getAnnotation().length() > 2000) {
                throw new ValidationException("Аннотация должна быть от 20 до 2000 символов");
            }
            event.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с id=" + updateRequest.getCategory() + " не найдена"));
            event.setCategory(category);
        }
        if (StringUtils.hasText(updateRequest.getDescription())) {
            if (updateRequest.getDescription().length() < 20 || updateRequest.getDescription().length() > 7000) {
                throw new ValidationException("Описание должно быть от 20 до 7000 символов");
            }
            event.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getLocation() != null) {
            event.setLocationLat(updateRequest.getLocation().getLat());
            event.setLocationLon(updateRequest.getLocation().getLon());
        }
        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }
        if (updateRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }
        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }
        if (updateRequest.getTitle() != null) {
            if (updateRequest.getTitle().length() < 3 || updateRequest.getTitle().length() > 120) {
                throw new ValidationException("Заголовок должен быть от 3 до 120 символов");
            }
            event.setTitle(updateRequest.getTitle());
        }
        return EventMapper.mapToFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto patchEventByUser(Long userId, Long eventId, UpdateEventUserRequest updateRequest) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("События с id=" + eventId + " созданного юзером с id=" + userId + "не найдено"));
        if (event.getEventState() != EventState.PENDING && event.getEventState() != EventState.CANCELED) {
            throw new EventStateException("Нельзя изменить уже опубликованное событие");
        }
        if (updateRequest.getEventDate() != null) {
            event.setEventDate(updateRequest.getEventDate());
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Дата события должна быть не раньше, чем через 2 часа от текущего времени");
        }
        if (updateRequest.getAnnotation() != null) {
            if (updateRequest.getAnnotation().length() < 20 || updateRequest.getAnnotation().length() > 2000) {
                throw new ValidationException("Аннотация должна быть от 20 до 2000 символов");
            }
            event.setAnnotation(updateRequest.getAnnotation());
        }
        if (updateRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория с id=" + updateRequest.getCategory() + " не найдена"));
            event.setCategory(category);
        }
        if (StringUtils.hasText(updateRequest.getDescription())) {
            if (updateRequest.getDescription().length() < 20 || updateRequest.getDescription().length() > 7000) {
                throw new ValidationException("Аннотация должна быть от 20 до 2000 символов");
            }
            event.setDescription(updateRequest.getDescription());
        }
        if (updateRequest.getLocation() != null) {
            Float lat = updateRequest.getLocation().getLat();
            Float lon = updateRequest.getLocation().getLon();
            if (lat <= -90 || lat >= 90 || lon <= -180 || lon >= 180) {
                throw new ValidationException("Координаты выходят за пределы допустимых значений");
            }
            event.setLocationLat(lat);
            event.setLocationLon(lon);
        }
        if (updateRequest.getPaid() != null) {
            event.setPaid(updateRequest.getPaid());
        }
        if (updateRequest.getParticipantLimit() != null) {
            if (updateRequest.getParticipantLimit() < 0) {
                throw new ValidationException("Лимит участников не может быть отрицательным числом");
            }
            event.setParticipantLimit(updateRequest.getParticipantLimit());
        }
        if (updateRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateRequest.getRequestModeration());
        }
        if (updateRequest.getTitle() != null) {
            if (updateRequest.getTitle().length() < 3 || updateRequest.getTitle().length() > 120) {
                throw new ValidationException("Заголовок должен быть от 3 до 120 символов");
            }
            event.setTitle(updateRequest.getTitle());
        }
        UserStateAction action = updateRequest.getStateAction();
        if (action != null) {
            switch (action) {
                case CANCEL_REVIEW:
                    event.setEventState(EventState.CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    event.setEventState(EventState.PENDING);
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестное действие: " + action);
            }
        }
        return EventMapper.mapToFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto findEventById(Long id, HttpServletRequest request) {

        Event event = eventRepository.findByIdAndEventState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException("События с id=" + id + "не найдено"));
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setTimestamp(LocalDateTime.now());
        String uri = request.getRequestURI();
        endpointHitDto.setUri(request.getRequestURI());
        endpointHitDto.setIp(request.getRemoteAddr());
        endpointHitDto.setApp("ewm-main-service");
        LocalDateTime start = LocalDateTime.now().minusYears(100);
        LocalDateTime end = LocalDateTime.now().plusYears(100);

        int amountUniqueBefore = statClient.getStatistics(start, end, List.of(uri), true).size();

        statClient.addHit(endpointHitDto);

        int amountUniqueAfter = statClient.getStatistics(start, end, List.of(uri), true).size();

        if (amountUniqueAfter > amountUniqueBefore) {
            event.setViews(event.getViews() + 1);
        }
        eventRepository.save(event);
        return EventMapper.mapToFullDto(event);
    }

    @Override
    public List<EventShortDto> searchAvailableItemByText(
            Integer from, Integer size, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
            LocalDateTime rangeEnd, Boolean onlyAvailable, EventSortBy sort, String text, HttpServletRequest request) {
        BooleanBuilder predicate = new BooleanBuilder();
        predicate.and(QEvent.event.eventState.eq(EventState.PUBLISHED));
        if (StringUtils.hasText(text)) {
            predicate.and(QEvent.event.annotation.containsIgnoreCase(text)
                    .or(QEvent.event.description.containsIgnoreCase(text)));
        }
        if (categories != null && !categories.isEmpty()) {
            predicate.and(QEvent.event.category.id.in(categories));
        }
        if (paid != null) {
            predicate.and(QEvent.event.paid.eq(paid));
        }
        predicate.and(QEvent.event.eventDate.after(Objects.requireNonNullElseGet(rangeStart, LocalDateTime::now)));
        if (rangeEnd != null) {
            predicate.and(QEvent.event.eventDate.before(rangeEnd));
        }
        if (onlyAvailable) {
            predicate.and(QEvent.event.confirmedRequests.lt(QEvent.event.participantLimit));
        }
        Sort sorting = Sort.unsorted();
        if (sort == EventSortBy.EVENT_DATE) {
            sorting = Sort.by(Sort.Direction.ASC, "eventDate");
        } else if (sort == EventSortBy.VIEWS) {
            sorting = Sort.by(Sort.Direction.DESC, "views");
        }
        Pageable pageable = PageRequest.of(from / size, size, sorting);
        List<Event> events = eventRepository.findAll(predicate, pageable).getContent();
        EndpointHitDto endpointHitDto = new EndpointHitDto();
        endpointHitDto.setTimestamp(LocalDateTime.now());

        endpointHitDto.setUri(request.getRequestURI());
        endpointHitDto.setIp(request.getRemoteAddr());
        endpointHitDto.setApp("ewm-main-service");
        statClient.addHit(endpointHitDto);
        return EventMapper.mapToShortDtoList(events);
    }

    public List<EventFullDto> searchFullEventForAdmin(
            Integer from, Integer size, List<Long> initiators, List<EventState> states,
            List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        BooleanBuilder predicate = new BooleanBuilder();
        if (initiators != null && !initiators.isEmpty()) {
            predicate.and(QEvent.event.initiator.id.in(initiators));
        }
        if (states != null) {
            predicate.and(QEvent.event.eventState.in(states));
        }
        if (categories != null && !categories.isEmpty()) {
            predicate.and(QEvent.event.category.id.in(categories));
        }
        if (rangeStart != null) {
            predicate.and(QEvent.event.eventDate.after(rangeStart));
        }
        if (rangeEnd != null) {
            predicate.and(QEvent.event.eventDate.before(rangeEnd));
        }

        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findAll(predicate, pageable).getContent();

        return EventMapper.mapToFullDtoList(events);
    }
}