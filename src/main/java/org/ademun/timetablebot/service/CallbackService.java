package org.ademun.timetablebot.service;

import org.ademun.timetablebot.callback.Callback;
import org.ademun.timetablebot.context.ChatContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
public class CallbackService {
  private final List<Callback> callbacks;
  private final ChatContextService chatContextService;

  @Autowired
  public CallbackService(List<Callback> callbacks, ChatContextService chatContextService) {
    this.callbacks = callbacks;
    this.chatContextService = chatContextService;
  }

  public SendMessage handle(Update update) {
    ChatContext.State state =
        chatContextService.getChatContext(update.getMessage().getChatId()).orElseThrow().getChatState();
    Callback callback =
        callbacks.stream().filter(clbck -> clbck.getAccordingContext() == state).findFirst()
            .orElse(null);
    System.out.println(callback);
    if (callback == null) {
      return null;
    }
    return callback.execute(update);
  }
}
