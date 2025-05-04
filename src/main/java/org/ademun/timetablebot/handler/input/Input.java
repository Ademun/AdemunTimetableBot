package org.ademun.timetablebot.handler.input;

import lombok.NonNull;
import org.ademun.timetablebot.state.ChatStateManager.ChatState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Input {
  @NonNull
  SendMessage execute(Update update);

  ChatState getAccordingState();
}
