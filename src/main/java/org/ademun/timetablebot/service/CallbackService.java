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
  private final ChatStateService chatStateService;

  @Autowired
  public CallbackService(List<Callback> callbacks, ChatStateService chatStateService) {
    this.callbacks = callbacks;
    this.chatStateService = chatStateService;
  }

  public SendMessage handle(Update update) {
    ChatContext.State state =
        chatStateService.getChatState(update.getMessage().getChatId()).orElseThrow().getChatState();
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
