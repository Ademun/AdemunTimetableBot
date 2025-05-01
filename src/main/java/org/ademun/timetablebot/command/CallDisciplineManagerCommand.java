package org.ademun.timetablebot.command;

import lombok.NonNull;
import org.ademun.timetablebot.context.ChatContext;
import org.ademun.timetablebot.service.ChatContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class CallDisciplineManagerCommand implements Command {
  private final ChatContextService chatContextService;

  @Autowired
  public CallDisciplineManagerCommand(ChatContextService chatContextService) {
    this.chatContextService = chatContextService;
  }

  @Override
  public @NonNull SendMessage execute(Update update) {
    Long chatId = update.getMessage()
                        .getChatId();
    ChatContext context = chatContextService.getChatContext(chatId)
                                            .orElse(null);
    if (context == null) {
      return SendMessage.builder()
                        .chatId(chatId)
                        .text(
                            "Кажется вы ещё не создали группу в этом чате. Используйте команду " + "/start")
                        .build();
    }
    context.setChatState(ChatContext.State.MANAGE_DISCIPLINES);
    return SendMessage.builder()
                      .chatId(chatId)
                      .text("Выберите действие")
                      .replyMarkup(getInlineKeyboardMarkup())
                      .build();
  }

  private InlineKeyboardMarkup getInlineKeyboardMarkup() {
    List<InlineKeyboardRow> rows = new ArrayList<>();
    rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                                                       .text("Добавить дисциплину")
                                                       .callbackData(
                                                           CallbackData.ADD_DISCIPLINE.name())
                                                       .build()));
    rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                                                       .text("Удалить дисциплину")
                                                       .callbackData(
                                                           CallbackData.DELETE_DISCIPLINE.name())
                                                       .build()));
    return new InlineKeyboardMarkup(rows);
  }

  @Override
  public String getName() {
    return "/disciplines";
  }

  @Override
  public String getDescription() {
    return "Управление дисциплинами";
  }

  public enum CallbackData {
    ADD_DISCIPLINE, DELETE_DISCIPLINE,
  }
}
