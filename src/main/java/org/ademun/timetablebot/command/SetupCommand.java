package org.ademun.timetablebot.command;

import lombok.NonNull;
import org.ademun.timetablebot.context.ChatContext;
import org.ademun.timetablebot.service.ChatStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class SetupCommand implements Command {
  private final ChatStateService chatStateService;

  @Autowired
  public SetupCommand(ChatStateService chatStateService) {
    this.chatStateService = chatStateService;
  }

  @Override
  public @NonNull SendMessage execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    if (chatStateService.getChatState(chatId).isPresent()) {
      return SendMessage.builder().chatId(chatId).text("Вы уже создали группу в этом чате").build();
    }
    createContext(chatId);
    return SendMessage.builder().chatId(chatId).text("Введите код группы (Например ИН-24-8)")
        .build();
  }

  private void createContext(Long chatId) {
    ChatContext context = new ChatContext();
    context.setChatState(ChatContext.State.CHAT_SETUP);
    chatStateService.putChatState(chatId, context);
  }

  @Override
  public String getName() {
    return "/start";
  }

  @Override
  public String getDescription() {
    return "Начальная настройка группы";
  }
}
