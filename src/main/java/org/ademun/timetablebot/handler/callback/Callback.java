package org.ademun.timetablebot.handler.callback;

import java.util.Map;
import lombok.NonNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Callback {

  @NonNull
  SendMessage execute(Update update, Map<String, String> callbackData);

  Action getAccordingAction();
}
