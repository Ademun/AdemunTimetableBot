package org.ademun.timetablebot.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;
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
public class Discipline {

  private Long disciplineId;
  private String name;
  private String url;

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Discipline that = (Discipline) o;
    return Objects.equals(name.toLowerCase().replace(" ", ""),
        that.name.toLowerCase().replace(" ", ""));
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(name.toLowerCase().replace(" ", ""));
  }
}
