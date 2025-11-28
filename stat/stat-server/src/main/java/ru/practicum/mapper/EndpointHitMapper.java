package ru.practicum.mapper;

import ru.practicum.EndpointHitDto;
import ru.practicum.model.EndpointHit;

public class EndpointHitMapper {
    public static EndpointHit mapToEndpointHit(EndpointHitDto dto) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(dto.getApp());
        endpointHit.setUri(dto.getUri());
        endpointHit.setIp(dto.getIp());
        endpointHit.setTimestamp(dto.getTimestamp());
        return endpointHit;
    }

    public static EndpointHitDto mapToEndpointHitDto(EndpointHit eh) {
        return new EndpointHitDto(eh.getId(), eh.getApp(), eh.getUri(), eh.getIp(), eh.getTimestamp());
    }

}
