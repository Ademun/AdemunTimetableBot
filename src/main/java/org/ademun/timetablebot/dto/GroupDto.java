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
  private Long groupId;
  private String name;
  private Long channelId;

  public GroupDto(Long groupId, String name, Long channelId) {
    this.groupId = groupId;
    this.name = name;
    this.channelId = channelId;
  }

  public GroupDto(String name, Long channelId) {
    this.name = name;
    this.channelId = channelId;
  }
}
