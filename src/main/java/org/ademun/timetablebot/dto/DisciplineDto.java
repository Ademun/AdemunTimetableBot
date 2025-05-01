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
public class DisciplineDto {
  private Long disciplineId;
  private String name;
  private String url;

  public DisciplineDto(Long disciplineId, String name, String url) {
    this.disciplineId = disciplineId;
    this.name = name;
    this.url = url;
  }

  public DisciplineDto(String name, String url) {
    this.name = name;
    this.url = url;
  }

  public DisciplineDto(String name) {
    this.name = name;
  }
}
