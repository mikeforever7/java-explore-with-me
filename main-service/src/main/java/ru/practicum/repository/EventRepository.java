package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.enums.EventState;
import ru.practicum.model.Event;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    Optional<Event> findByIdAndEventState(Long id, EventState state);

    Page<Event> findByInitiatorId(Long userId, Pageable pageable);

    boolean existsByIdAndInitiatorId(Long eventId, Long userId);

    boolean existsByCategoryId(Long catId);

    List<Event> findByIdIn(List<Long> ids);
}
