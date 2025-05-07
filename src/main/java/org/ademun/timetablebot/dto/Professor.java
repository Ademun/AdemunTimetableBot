package org.ademun.timetablebot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Professor {

  private Long professorId;
  private String firstName;
  private String lastName;
  private String patronymic;
  private String url;

  public String getFullName() {
    return firstName + " " + lastName + " " + patronymic;
  }

  public String getShortName() {
    return String.format("%s %s.%s.", firstName, lastName.charAt(0), patronymic.charAt(0));
  }
}
