package org.ademun.timetablebot.service;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.ademun.timetablebot.dto.Discipline;
import org.ademun.timetablebot.dto.Group;
import org.ademun.timetablebot.dto.Professor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class GroupService {

  private final RestClient client = RestClient.builder()
      .baseUrl("http://localhost:8080/api/groups/")
      .requestInterceptor(((request, body, execution) -> {
        log.info("Request sent on {}", request.getURI());
        return execution.execute(request, body);
      })).build();

  @Retryable
  public List<Group> getAll() {
    try {
      return client.get().retrieve().body(new ParameterizedTypeReference<>() {
      });
    } catch (HttpClientErrorException e) {
      throw new RuntimeException(e);
    }
  }

  @Retryable
  public Optional<Group> getById(Long id) {
    try {
      return Optional.ofNullable(client.get().uri("{id}", id).retrieve().body(Group.class));
    } catch (HttpClientErrorException e) {
      log.error(e.getResponseBodyAsString());
    }
    return Optional.empty();
  }

  @Retryable
  public Optional<Group> getByChannelId(Long channelId) {
    try {
      return Optional.ofNullable(
          client.get().uri(uriBuilder -> uriBuilder.queryParam("channelId", channelId).build())
              .retrieve().body(Group.class));
    } catch (HttpClientErrorException e) {
      log.error(e.getResponseBodyAsString());
    }
    return Optional.empty();
  }

  @Retryable
  public void create(Group group) {
    client.post().contentType(MediaType.APPLICATION_JSON).body(group).retrieve().toBodilessEntity();
  }

  @Retryable
  public void delete(Long id) {
    client.delete().uri("{id}", id);
  }

  @Retryable
  public List<Discipline> getDisciplines(Long id) {
    return client.get().uri("{id}/disciplines/", id).retrieve()
        .body(new ParameterizedTypeReference<>() {
        });
  }

  @Retryable
  public void addDiscipline(Long groupId, Discipline discipline) {
    client.post().uri("{groupId}/disciplines/", groupId).body(discipline).retrieve()
        .toBodilessEntity();
  }

  @Retryable
  public void removeDiscipline(Long groupId, Long disciplineId) {
    System.out.println(disciplineId);
    client.delete().uri("{groupId}/disciplines/{disciplineId}", groupId, disciplineId).retrieve()
        .toBodilessEntity();
  }

  @Retryable
  public List<Professor> getProfessors(Long id) {
    return client.get().uri("{id}/professors/", id).retrieve()
        .body(new ParameterizedTypeReference<>() {
        });
  }

  @Retryable
  public void addProfessor(Long groupId, Professor professor) {
    client.post().uri("{groupId}/professors/", groupId).body(professor);
  }

  @Retryable
  public void removeProfessor(Long groupId, Long professorId) {
    System.out.println(professorId);
    client.delete().uri("{groupId}/professors/{professorId}", groupId, professorId).retrieve()
        .toBodilessEntity();
  }
}
