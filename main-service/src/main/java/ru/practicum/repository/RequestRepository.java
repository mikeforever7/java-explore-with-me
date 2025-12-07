package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.enums.RequestStatus;
import ru.practicum.model.Request;

import java.util.Optional;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    Optional<Request> findByIdAndRequesterId(Long requestId, Long userId);

    List<Request> findByIdIn(List<Long> ids);

    List<Request> findByEventIdAndStatus(Long eventId, RequestStatus status);

    List<Request> findByEventId(Long eventId);

    List<Request> findByRequesterId(Long userId);
}
