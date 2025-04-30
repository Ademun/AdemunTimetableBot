package org.ademun.timetablebot.callback;

import lombok.NonNull;
import org.ademun.timetablebot.context.ChatContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Callback {
  @NonNull
  SendMessage execute(Update update);

  ChatContext.State getAccordingContext();
}
