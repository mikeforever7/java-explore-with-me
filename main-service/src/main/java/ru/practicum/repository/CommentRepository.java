package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.model.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findByIdAndAuthorIdAndEventId(Long commentId, Long userId, Long eventId);

    List<Comment> findAllByEventId(Long id);
}
