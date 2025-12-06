package ru.practicum;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
public class StatClient {
    private final String baseUrl = "http://stat-server:9090";
    //    private final String baseUrl = "http://localhost:9090";
    private final RestClient restClient;

    public StatClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public void addHit(EndpointHitDto endpointHitDto) {
        restClient.post()
                .uri(baseUrl + "/hit")
                .body(endpointHitDto)
                .retrieve()
                .toBodilessEntity();
    }

    public List<ViewStatsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        URI uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/stats")
                .queryParam("start", start.format(dtf))
                .queryParam("end", end.format(dtf))
                .queryParam("uris", uris.toArray())
                .queryParam("unique", unique)
                .build()
                .toUri();

        return restClient.get()
                .uri(uri)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ViewStatsDto>>() {
                });
    }
}

