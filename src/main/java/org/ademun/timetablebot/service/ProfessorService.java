package org.ademun.timetablebot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ademun.timetablebot.dto.GroupDto;
import org.ademun.timetablebot.dto.ProfessorDto;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;

import static org.ademun.timetablebot.helpers.HttpHelper.executeRequest;

@Service
public class ProfessorService {
  private final ObjectMapper mapper = new ObjectMapper();

  public List<ProfessorDto> getAllProfessors() {
    HttpRequest request =
        HttpRequest.newBuilder(URI.create("http://localhost:8080/api/professors/")).GET().build();
    List<ProfessorDto> professorDtoList;
    try {
      professorDtoList = mapper.readValue(executeRequest(request), new TypeReference<>() {
      });
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return professorDtoList;
  }

  public ProfessorDto getProfessorById(Long id) {
    HttpRequest request =
        HttpRequest.newBuilder(URI.create("http://localhost:8080/api/professors/" + id)).GET()
            .build();
    ProfessorDto professorDto;
    try {
      professorDto = mapper.readValue(executeRequest(request), new TypeReference<>() {
      });
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return professorDto;
  }

  public ProfessorDto createProfessor(ProfessorDto professorDto) {
    String professorJson;
    try {
      professorJson = mapper.writeValueAsString(professorDto);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    HttpRequest request =
        HttpRequest.newBuilder(URI.create("http://localhost:8080/api/professors/"))
            .POST(HttpRequest.BodyPublishers.ofString(professorJson)).header("Content-Type", "application/json").build();
    ProfessorDto professorDtoResponse;
    try {
      professorDtoResponse = mapper.readValue(executeRequest(request), new TypeReference<>() {
      });
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return professorDtoResponse;
  }

  public void deleteProfessorById(Long id) {
    HttpRequest request =
        HttpRequest.newBuilder(URI.create("http://localhost:8080/api/professors/" + id)).DELETE()
            .build();
    executeRequest(request);
  }

  public List<GroupDto> getGroups(Long id) {
    HttpRequest request = HttpRequest.newBuilder(
        URI.create("http://localhost:8080/api/professors/" + id + "/groups/")).GET().build();
    List<GroupDto> groupDtoList;
    try {
      groupDtoList = mapper.readValue(executeRequest(request), new TypeReference<>() {
      });
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return groupDtoList;
  }
}
