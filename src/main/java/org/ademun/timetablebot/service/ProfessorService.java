package org.ademun.timetablebot.service;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ademun.timetablebot.dto.Professor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class ProfessorService {

  private final RestClient client = RestClient.builder()
      .baseUrl("http://localhost:8080/api/professors/")
      .requestInterceptor(((request, body, execution) -> {
        log.info("Request sent on {}", request.getURI());
        return execution.execute(request, body);
      })).build();

  @Retryable
  public List<Professor> getAll() {
    try {
      return client.get().retrieve().body(new ParameterizedTypeReference<>() {
      });
    } catch (HttpClientErrorException e) {
      throw new RuntimeException(e);
    }
  }

  @Retryable
  public Optional<Professor> getById(Long id) {
    try {
      return Optional.ofNullable(client.get().uri("{id}", id).retrieve().body(Professor.class));
    } catch (HttpClientErrorException e) {
      log.error(e.getResponseBodyAsString());
    }
    return Optional.empty();
  }

  @Retryable
  public Optional<Professor> getByFullName(String name) {
    try {
      return Optional.ofNullable(
          client.get().uri(uriBuilder -> uriBuilder.queryParam("name", name).build()).retrieve()
              .body(Professor.class));
    } catch (HttpClientErrorException e) {
      log.error(e.getResponseBodyAsString());
    }
    return Optional.empty();
  }

  @Retryable
  public Professor create(Professor professor) {
    return client.post().contentType(MediaType.APPLICATION_JSON).body(professor).retrieve()
        .toEntity(Professor.class).getBody();
  }

  @Retryable
  public void delete(Long id) {
    client.delete().uri("{id}", id);
  }
}
