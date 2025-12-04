package ru.practicum.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.enums.EventState;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@ToString
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000, nullable = false)
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private Integer confirmedRequests = 0;

    @Column
    @CreationTimestamp
    private LocalDateTime createdOn;

    @Column(length = 7000, nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;

    @Column(nullable = false)
    private Float locationLat;

    @Column(nullable = false)
    private Float locationLon;

    private boolean paid;

    private int participantLimit;

    private LocalDateTime publishedOn;

    private boolean requestModeration = true;

    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private EventState eventState = EventState.PENDING;

    @Column(length = 120, nullable = false)
    private String title;

    private Integer views = 0;
}
