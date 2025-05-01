package org.ademun.timetablebot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfessorDto {
  private Long professorId;
  private String firstName;
  private String lastName;
  private String patronymic;
  private String url;

  public ProfessorDto(Long professorId, String firstName, String lastName, String patronymic,
      String url) {
    this.professorId = professorId;
    this.firstName = firstName;
    this.lastName = lastName;
    this.patronymic = patronymic;
    this.url = url;
  }

  public ProfessorDto(String firstName, String lastName, String patronymic, String url) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.patronymic = patronymic;
    this.url = url;
  }

  public ProfessorDto(String firstName, String lastName, String patronymic) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.patronymic = patronymic;
  }
}
