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
public class GroupDto {
  private Long group_id;
  private String name;
  private Long channel_id;

  public GroupDto(Long group_id, String name, Long channel_id) {
    this.group_id = group_id;
    this.name = name;
    this.channel_id = channel_id;
  }

  public GroupDto(String name, Long channel_id) {
    this.name = name;
    this.channel_id = channel_id;
  }
}
