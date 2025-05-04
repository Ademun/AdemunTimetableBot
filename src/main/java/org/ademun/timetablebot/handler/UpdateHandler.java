package org.ademun.timetablebot.handler;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public interface UpdateHandler {

  void onUpdate(TelegramClient client, Update update);
}
