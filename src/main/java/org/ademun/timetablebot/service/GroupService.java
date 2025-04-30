package org.ademun.timetablebot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ademun.timetablebot.dto.DisciplineDto;
import org.ademun.timetablebot.dto.GroupDto;
import org.ademun.timetablebot.dto.ProfessorDto;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;

import static org.ademun.timetablebot.helpers.HttpHelper.executeRequest;

@Service
public class GroupService {
  private final ObjectMapper mapper = new ObjectMapper();

  public List<GroupDto> getAllGroups() {
    HttpRequest request =
        HttpRequest.newBuilder(URI.create("http://localhost:8080/api/groups/")).GET().build();
    List<GroupDto> groupDtoList;
    try {
      groupDtoList = mapper.readValue(executeRequest(request), new TypeReference<>() {
      });
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return groupDtoList;
  }

  public GroupDto getGroupById(Long id) {
    HttpRequest request =
        HttpRequest.newBuilder(URI.create("http://localhost:8080/api/groups/" + id)).GET().build();
    GroupDto groupDto;
    try {
      groupDto = mapper.readValue(executeRequest(request), new TypeReference<>() {
      });
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return groupDto;
  }

  public GroupDto getGroupByChannelId(Long channelId) {
    HttpRequest request =
        HttpRequest.newBuilder(URI.create("http://localhost:8080/api/groups/")).GET().build();
    List<GroupDto> groupDtoList;
    try {
      groupDtoList = mapper.readValue(executeRequest(request), new TypeReference<>() {
      });
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    for (GroupDto groupDto : groupDtoList) {
      System.out.println(groupDto);
    }
    return groupDtoList.stream().filter(groupDto -> groupDto.getChannel_id().equals(channelId))
        .findFirst().orElse(null);
  }

  public GroupDto createGroup(GroupDto groupDto) {
    String groupJson;
    try {
      groupJson = mapper.writeValueAsString(groupDto);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/api/groups/"))
        .POST(HttpRequest.BodyPublishers.ofString(groupJson))
        .header("Content-Type", "application/json").build();
    GroupDto groupDtoResponse;
    try {
      groupDtoResponse = mapper.readValue(executeRequest(request), new TypeReference<>() {
      });
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return groupDtoResponse;
  }

  public void deleteGroupById(Long id) {
    HttpRequest request =
        HttpRequest.newBuilder(URI.create("http://localhost:8080/api/groups/" + id)).DELETE()
            .build();
    executeRequest(request);
  }

  public List<DisciplineDto> getDisciplines(Long id) {
    HttpRequest request =
        HttpRequest.newBuilder(URI.create("http://localhost:8080/api/groups/" + id + "/disciplines/"))
            .GET().build();
    List<DisciplineDto> disciplineDtoList;
    try {
      disciplineDtoList = mapper.readValue(executeRequest(request), new TypeReference<>() {
      });
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return disciplineDtoList;
  }

  public List<ProfessorDto> getProfessors(Long id) {
    HttpRequest request =
        HttpRequest.newBuilder(URI.create("http://localhost:8080/api/groups/" + id + "/professors/"))
            .GET().build();
    List<ProfessorDto> professorDtoList;
    try {
      professorDtoList = mapper.readValue(executeRequest(request), new TypeReference<>() {
      });
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return professorDtoList;
  }
}
