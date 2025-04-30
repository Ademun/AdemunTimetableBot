package org.ademun.timetablebot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ademun.timetablebot.dto.DisciplineDto;
import org.ademun.timetablebot.dto.GroupDto;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;

import static org.ademun.timetablebot.helpers.HttpHelper.executeRequest;

@Service
public class DisciplineService {
  private final ObjectMapper mapper = new ObjectMapper();

  public List<DisciplineDto> getAllDisciplines() {
    HttpRequest request =
        HttpRequest.newBuilder(URI.create("http://localhost:8080/api/disciplines/")).GET().build();
    List<DisciplineDto> disciplineDtoList;
    try {
      disciplineDtoList = mapper.readValue(executeRequest(request), new TypeReference<>() {
      });
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return disciplineDtoList;
  }

  public DisciplineDto getDisciplineById(Long id) {
    HttpRequest request =
        HttpRequest.newBuilder(URI.create("http://localhost:8080/api/disciplines/" + id)).GET()
            .build();
    DisciplineDto disciplineDto;
    try {
      disciplineDto = mapper.readValue(executeRequest(request), new TypeReference<>() {
      });
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return disciplineDto;
  }

  public DisciplineDto createDiscipline(DisciplineDto disciplineDto) {
    String disciplineJson;
    try {
      disciplineJson = mapper.writeValueAsString(disciplineDto);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    HttpRequest request =
        HttpRequest.newBuilder(URI.create("http://localhost:8080/api/disciplines/"))
            .POST(HttpRequest.BodyPublishers.ofString(disciplineJson)).header("Content-Type", "application/json").build();
    DisciplineDto disciplineDtoResponse;
    try {
      disciplineDtoResponse = mapper.readValue(executeRequest(request), new TypeReference<>() {
      });
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return disciplineDtoResponse;
  }

  public void deleteDisciplineById(Long id) {
    HttpRequest request =
        HttpRequest.newBuilder(URI.create("http://localhost:8080/api/disciplines/" + id)).DELETE()
            .build();
    executeRequest(request);
  }

  public List<GroupDto> getGroups(Long id) {
    HttpRequest request = HttpRequest.newBuilder(
        URI.create("http://localhost:8080/api/disciplines/" + id + "/groups/")).GET().build();
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
