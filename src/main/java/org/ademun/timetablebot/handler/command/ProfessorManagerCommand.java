package org.ademun.timetablebot.handler.command;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import org.ademun.timetablebot.handler.callback.Action;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
public class ProfessorManagerCommand implements Command {

  @Override
  public @NonNull SendMessage execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    return SendMessage.builder().chatId(chatId).text("Выберите действие")
        .replyMarkup(getInlineKeyboardMarkup()).build();
  }

  private InlineKeyboardMarkup getInlineKeyboardMarkup() {
    List<InlineKeyboardRow> rows = new ArrayList<>();
    rows.add(new InlineKeyboardRow(
        InlineKeyboardButton.builder().text("Добавить")
            .callbackData("action=" + Action.MANAGER_ADD_PROFESSOR.name())
            .build()));
    rows.add(new InlineKeyboardRow(
        InlineKeyboardButton.builder().text("Удалить")
            .callbackData("action=" + Action.MANAGER_REMOVE_PROFESSOR.name())
            .build()));
    return new InlineKeyboardMarkup(rows);
  }

  @Override
  public String getName() {
    return "/professors";
  }

  @Override
  public String getDescription() {
    return "Управление преподавателями";
  }
}
