package org.ademun.timetablebot.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.ademun.timetablebot.handler.input.Input;
import org.ademun.timetablebot.state.ChatStateManager;
import org.ademun.timetablebot.state.ChatStateManager.ChatState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
public class InputHandler implements UpdateHandler {

  private final List<Input> inputs;
  private final ChatStateManager chatStateManager;

  @Autowired
  public InputHandler(Collection<Input> inputs, ChatStateManager chatStateManager) {
    this.inputs = new ArrayList<>(inputs);
    this.chatStateManager = chatStateManager;
  }

  @Override
  public void onUpdate(TelegramClient client, Update update) {
    if (update.hasMessage() && !update.getMessage().isCommand() && update.getMessage().hasText()) {
      SendMessage message = handle(update);
      try {
        client.execute(message);
      } catch (TelegramApiException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private SendMessage handle(Update update) {
    Long chatId = update.getMessage().getChatId();
    String message = update.getMessage().getText();
    log.info("Received message: {}", message);
    ChatState state = chatStateManager.getChatState(chatId);
    Input input = inputs.stream().filter(inpt -> inpt.getAccordingState() == state)
        .findFirst().orElse(null);
    if (input == null) {
      return null;
    }
    chatStateManager.clearChatState(chatId);
    return input.execute(update);
  }
}
