package org.ademun.timetablebot.service;

import lombok.extern.slf4j.Slf4j;
import org.ademun.timetablebot.dto.DisciplineDto;
import org.ademun.timetablebot.dto.GroupDto;
import org.ademun.timetablebot.dto.ProfessorDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class GroupService {
  private final RestClient client = RestClient.builder()
                                              .baseUrl("https://localhost:8080/api/groups")
                                              .requestInterceptor(((request, body, execution) -> {
                                                log.info("Request sent on {}", request.getURI());
                                                return execution.execute(request, body);
                                              }))
                                              .build();

  public List<GroupDto> getAllGroups() {
    try {
      return client.get()
                   .retrieve()
                   .body(new ParameterizedTypeReference<>() {
                   });
    } catch (HttpClientErrorException e) {
      throw new RuntimeException(e);
    }
  }

  public Optional<GroupDto> getGroupById(Long id) {
    try {
      return Optional.ofNullable(client.get()
                                       .uri("{id}", id)
                                       .retrieve()
                                       .body(GroupDto.class));
    } catch (HttpClientErrorException e) {
      log.error(e.getResponseBodyAsString());
    }
    return Optional.empty();
  }

  public Optional<GroupDto> getGroupByChannelId(Long channelId) {
    List<GroupDto> groups = getAllGroups();
    return groups.stream()
                 .filter(groupDto -> groupDto.getChannel_id()
                                             .equals(channelId))
                 .findFirst();
  }

  public GroupDto createGroup(GroupDto groupDto) {
    return client.post()
                 .contentType(MediaType.APPLICATION_JSON)
                 .body(groupDto)
                 .retrieve()
                 .toEntity(GroupDto.class)
                 .getBody();
  }

  public void deleteGroupById(Long id) {
    client.delete()
          .uri("{id}", id);
  }

  public List<DisciplineDto> getDisciplines(Long id) {
    return client.get()
                 .uri("{id}/disciplines/", id)
                 .retrieve()
                 .body(new ParameterizedTypeReference<>() {
                 });
  }

  public void addDiscipline(Long id, DisciplineDto disciplineDto) {
    client.post()
          .uri("{id}/disciplines/", id)
          .body(disciplineDto);
  }

  public List<ProfessorDto> getProfessors(Long id) {
    return client.get()
                 .uri("{id}/professors/", id)
                 .retrieve()
                 .body(new ParameterizedTypeReference<>() {
                 });
  }

  public void addProfessor(Long id, ProfessorDto professorDto) {
    client.post()
          .uri("{id}/professors/", id)
          .body(professorDto);
  }
}
