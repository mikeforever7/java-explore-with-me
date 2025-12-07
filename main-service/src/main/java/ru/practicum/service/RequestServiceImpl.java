package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.dto.request.ParticipationRequestDto;
import ru.practicum.enums.EventState;
import ru.practicum.enums.RequestStatus;
import ru.practicum.exception.*;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.Event;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ParticipationRequestDto addNewRequest(Long userId, Long eventId) {
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new AlreadyExistsException(
                    "Запрос на участие в событии с id=" + eventId + " от юзера с id=" + userId + " уже существует");
        }
        log.info("Метод добавления запроса к eventId={} от userId={} ", eventId, userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("События с id=" + eventId + " не найдено"));
        User requester = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        if (event.getInitiator().getId().equals(userId)) {
            throw new RequestAccessException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        if (!event.getEventState().equals(EventState.PUBLISHED)) {
            throw new EventNotPublishedException("Можно участвовать только в опубликованном событии");
        }
        if (event.getConfirmedRequests() >= event.getParticipantLimit() && event.getParticipantLimit() != 0) {
            throw new ParticipationLimitReachedException("Превышен лимит участников");
        }
        Request newRequest = new Request();
        newRequest.setRequester(requester);
        newRequest.setEvent(event);
        if (!event.isRequestModeration() || event.getParticipantLimit() == 0) {
            newRequest.setStatus(RequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        } else {
            newRequest.setStatus(RequestStatus.PENDING);
        }
        return RequestMapper.mapToDto(requestRepository.save(newRequest));
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Запрос id=" + requestId + " у юзера id=" + userId + " не найден"));
        if (request.getStatus() == RequestStatus.CONFIRMED) {
            Event event = eventRepository.findById(request.getEvent().getId())
                    .orElseThrow(() -> new NotFoundException("Событие для запроса id=" + requestId + " не найдено"));
            event.setConfirmedRequests(event.getConfirmedRequests() - 1);
        }
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.mapToDto(requestRepository.save(request));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult changeStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest updateRequest) {
        log.info("Обновление статуса для событий: eventId={}, userId={}, updateRequest={}", eventId, userId, updateRequest);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("События с id=" + eventId + " созданного юзером с id=" + userId + " не найдено"));
        int participantLimit = event.getParticipantLimit();
        if (participantLimit > 0 || event.isRequestModeration()) {

            int confirmedRequestsAmount = event.getConfirmedRequests();
            List<Long> ids = updateRequest.getRequestIds();
            List<Request> requests = requestRepository.findByIdIn(ids);

            RequestStatus newStatus = updateRequest.getStatus();
            List<Request> confirmedRequests = new ArrayList<>();
            List<Request> rejectedRequests = new ArrayList<>();

            for (Request request : requests) {
                if (request.getStatus() != RequestStatus.PENDING) {
                    throw new EventStateException("Изменять статус можно только у заявок, находящихся в состоянии ожидания");
                }
                switch (newStatus) {
                    case REJECTED -> {
                        request.setStatus(RequestStatus.REJECTED);
                        rejectedRequests.add(request);
                    }
                    case CONFIRMED -> {
                        if (confirmedRequestsAmount >= participantLimit) {
                            List<Request> pendingRequests = requestRepository.findByEventIdAndStatus(eventId, RequestStatus.PENDING);
                            for (Request pendingRequest : pendingRequests) {
                                pendingRequest.setStatus(RequestStatus.REJECTED);
                            }
                            requestRepository.saveAll(pendingRequests);
                            throw new ParticipationLimitReachedException("Превышен лимит участников");
                        }
                        request.setStatus(newStatus);
                        confirmedRequests.add(request);
                        confirmedRequestsAmount++;
                    }
                }
            }
            requestRepository.saveAll(requests);
            event.setConfirmedRequests(confirmedRequestsAmount);
            eventRepository.save(event);

            return new EventRequestStatusUpdateResult(
                    RequestMapper.mapToDtoList(confirmedRequests), RequestMapper.mapToDtoList(rejectedRequests));
        }
        return new EventRequestStatusUpdateResult(List.of(), List.of());
    }

    @Override
    public List<ParticipationRequestDto> getUserEventRequests(Long userId, Long eventId) {
        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            throw new NotFoundException("События с id=" + eventId + " созданного юзером с id=" + userId + "не найдено");
        }
        List<Request> requests = requestRepository.findByEventId(eventId);
        return RequestMapper.mapToDtoList(requests);
    }

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Юзера с id=" + userId + " не найдено");
        }
        List<Request> requests = requestRepository.findByRequesterId(userId);
        return RequestMapper.mapToDtoList(requests);
    }
}
