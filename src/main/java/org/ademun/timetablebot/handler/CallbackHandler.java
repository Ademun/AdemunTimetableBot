package org.ademun.timetablebot.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.ademun.timetablebot.handler.callback.Action;
import org.ademun.timetablebot.handler.callback.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
public class CallbackHandler implements UpdateHandler {

  private final List<Callback> callbacks;

  @Autowired
  public CallbackHandler(Collection<Callback> callbacks) {
    this.callbacks = new ArrayList<>(callbacks);
  }

  @Override
  public void onUpdate(TelegramClient client, Update update) {
    if (update.hasCallbackQuery()) {
      String queryId = update.getCallbackQuery().getId();
      AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery(queryId);
      SendMessage message = handle(update);
      try {
        client.execute(message);
        client.execute(answerCallbackQuery);
      } catch (TelegramApiException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private SendMessage handle(Update update) {
    Map<String, String> data = Arrays.stream(update.getCallbackQuery().getData().split("&"))
        .map(param -> param.split("=")).collect(Collectors.toMap(p -> p[0], p -> p[1]));
    log.info("Received callback with data: {}", data);
    Action action = Action.valueOf(data.get("action"));
    Callback callback = callbacks.stream().filter(clbk -> clbk.getAccordingAction().equals(action))
        .findFirst().orElse(null);
    if (callback == null) {
      return null;
    }
    return callback.execute(update, data);
  }
}
