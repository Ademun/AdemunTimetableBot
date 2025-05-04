package org.ademun.timetablebot.handler.command;

import lombok.NonNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {

  @NonNull
  SendMessage execute(Update update);

  String getName();

  String getDescription();
}
