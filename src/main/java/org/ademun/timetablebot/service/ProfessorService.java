package org.ademun.timetablebot.service;

import lombok.extern.slf4j.Slf4j;
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
public class ProfessorService {
  private final RestClient client = RestClient.builder()
                                              .baseUrl("http://localhost:8080/api/professors/")
                                              .requestInterceptor(((request, body, execution) -> {
                                                log.info("Request sent on {}", request.getURI());
                                                return execution.execute(request, body);
                                              }))
                                              .build();

  public List<ProfessorDto> getAllProfessors() {
    try {
      return client.get()
                   .retrieve()
                   .body(new ParameterizedTypeReference<>() {
                   });
    } catch (HttpClientErrorException e) {
      throw new RuntimeException(e);
    }
  }

  public Optional<ProfessorDto> getProfessorById(Long id) {
    try {
      return Optional.ofNullable(client.get()
                                       .uri("{id}", id)
                                       .retrieve()
                                       .body(ProfessorDto.class));
    } catch (HttpClientErrorException e) {
      log.error(e.getResponseBodyAsString());
    }
    return Optional.empty();
  }

  public ProfessorDto createProfessor(ProfessorDto professorDto) {
    return client.post()
                 .contentType(MediaType.APPLICATION_JSON)
                 .body(professorDto)
                 .retrieve()
                 .toEntity(ProfessorDto.class)
                 .getBody();
  }

  public void deleteProfessorById(Long id) {
    client.delete()
          .uri("{id}", id);
  }
}
