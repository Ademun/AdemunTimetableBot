package org.ademun.timetablebot.handler.callback;

import java.util.Map;
import lombok.NonNull;
import org.ademun.timetablebot.state.ChatStateManager;
import org.ademun.timetablebot.state.ChatStateManager.ChatState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class AddNewDisciplineCallback implements Callback {

  private final ChatStateManager chatStateManager;

  @Autowired
  public AddNewDisciplineCallback(ChatStateManager chatStateManager) {
    this.chatStateManager = chatStateManager;
  }

  @Override
  public @NonNull SendMessage execute(Update update, Map<String, String> callbackData) {
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    chatStateManager.setChatState(chatId, ChatState.AWAITING_DISCIPLINE_NAME);
    return SendMessage.builder().chatId(chatId).text("Введите имя дисциплины").build();
  }

  @Override
  public Action getAccordingAction() {
    return Action.ADD_NEW_DISCIPLINE;
  }
}
