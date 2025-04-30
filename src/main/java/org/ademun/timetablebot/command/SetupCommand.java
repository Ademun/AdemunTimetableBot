package org.ademun.timetablebot.command;

import lombok.NonNull;
import org.ademun.timetablebot.context.ChatContext;
import org.ademun.timetablebot.service.ChatContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class SetupCommand implements Command {
  private final ChatContextService chatContextService;

  @Autowired
  public SetupCommand(ChatContextService chatContextService) {
    this.chatContextService = chatContextService;
  }

  @Override
  public @NonNull SendMessage execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    ChatContext context = chatContextService.getChatContext(chatId).orElse(null);
    if (context != null) {
      return SendMessage.builder().chatId(chatId).text("Вы уже создали группу в этом чате").build();
    }
    createContext(chatId);
    return SendMessage.builder().chatId(chatId).text("Введите код группы (Например ИН-24-8)")
        .build();
  }

  private void createContext(Long chatId) {
    ChatContext context = new ChatContext();
    context.setChatState(ChatContext.State.CHAT_SETUP);
    chatContextService.putChatContext(chatId, context);
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
