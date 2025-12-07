package ru.practicum.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.Location;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {
    public static Event mapToEvent(NewEventDto newEventDto, User initiator, Category category) {
        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(category);
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(newEventDto.getEventDate());
        event.setInitiator(initiator);
        event.setLocationLat(newEventDto.getLocation().getLat());
        event.setLocationLon(newEventDto.getLocation().getLon());
        event.setPaid(newEventDto.isPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.isRequestModeration());
        event.setTitle(newEventDto.getTitle());
        return event;
    }

    public static EventFullDto mapToFullDto(Event event) {
        Location location = new Location(event.getLocationLat(), event.getLocationLon());
        return new EventFullDto(event.getId(),
                event.getAnnotation(),
                event.getConfirmedRequests(),
                event.getCreatedOn(),
                CategoryMapper.mapToCategoryDto(event.getCategory()),
                event.getDescription(),
                event.getEventDate(),
                UserMapper.mapToUserShortDto(event.getInitiator()),
                location,
                event.isPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.isRequestModeration(),
                event.getEventState(),
                event.getTitle(),
                event.getViews());
    }

    public static EventShortDto mapToShortDto(Event event) {
        return new EventShortDto(event.getId(),
                event.getAnnotation(),
                event.getConfirmedRequests(),
                CategoryMapper.mapToCategoryDto(event.getCategory()),
                event.getEventDate(),
                UserMapper.mapToUserShortDto(event.getInitiator()),
                event.isPaid(),
                event.getTitle(),
                event.getViews());
    }

    public static List<EventShortDto> mapToShortDtoList(List<Event> events) {
        return events.stream().map(EventMapper::mapToShortDto).toList();
    }

    public static List<EventFullDto> mapToFullDtoList(List<Event> events) {
        return events.stream().map(EventMapper::mapToFullDto).toList();
    }
}
