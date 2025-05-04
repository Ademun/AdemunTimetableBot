package org.ademun.timetablebot.service;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ademun.timetablebot.dto.Discipline;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class DisciplineService {

  private final RestClient client = RestClient.builder()
      .baseUrl("http://localhost:8080/api/disciplines/")
      .requestInterceptor(((request, body, execution) -> {
        log.info("Request sent on {}", request.getURI());
        return execution.execute(request, body);
      })).build();

  @Retryable
  public List<Discipline> getAll() {
    try {
      return client.get().retrieve().body(new ParameterizedTypeReference<>() {
      });
    } catch (HttpClientErrorException e) {
      throw new RuntimeException(e);
    }
  }

  @Retryable
  public Optional<Discipline> getById(Long id) {
    try {
      return Optional.ofNullable(client.get().uri("{id}", id).retrieve().body(Discipline.class));
    } catch (HttpClientErrorException e) {
      log.error(e.getResponseBodyAsString());
    }
    return Optional.empty();
  }

  @Retryable
  public Optional<Discipline> getByName(String name) {
    try {
      return Optional.ofNullable(
          client.get().uri(uriBuilder -> uriBuilder.queryParam("name", name).build()).retrieve()
              .body(Discipline.class));
    } catch (HttpClientErrorException e) {
      log.error(e.getResponseBodyAsString());
    }
    return Optional.empty();
  }

  @Retryable
  public Discipline create(Discipline discipline) {
    return client.post().contentType(MediaType.APPLICATION_JSON).body(discipline).retrieve()
        .toEntity(Discipline.class).getBody();
  }

  @Retryable
  public void delete(Long id) {
    client.delete().uri("{id}", id);
  }
}
