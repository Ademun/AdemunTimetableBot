package org.ademun.timetablebot.context;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatContext {
  private State chatState = State.IDLE;


  public enum State {
    IDLE, CHAT_SETUP, GROUP_ADD_DISCIPLINE
  }
}
