package org.ademun.timetablebot.handler.callback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import org.ademun.timetablebot.dto.Discipline;
import org.ademun.timetablebot.dto.Group;
import org.ademun.timetablebot.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
public class ManagerRemoveDisciplineCallback implements Callback {

  private final GroupService groupService;

  @Autowired
  public ManagerRemoveDisciplineCallback(GroupService groupService) {
    this.groupService = groupService;
  }

  @Override
  public @NonNull SendMessage execute(Update update, Map<String, String> callbackData) {
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    Group group = groupService.getByChannelId(chatId).orElseThrow();
    List<Discipline> disciplines = groupService.getDisciplines(group.getGroupId());

    return SendMessage.builder().chatId(chatId).text("Выберите дисциплину, которую хотите удалить")
        .replyMarkup(getInlineKeyboardMarkup(disciplines)).build();
  }

  private InlineKeyboardMarkup getInlineKeyboardMarkup(List<Discipline> disciplines) {
    List<InlineKeyboardRow> rows = new ArrayList<>();
    disciplines.forEach(discipline -> rows.add(
        new InlineKeyboardRow(InlineKeyboardButton.builder().text(discipline.getName())
            .callbackData(
                "action=" + Action.REMOVE_DISCIPLINE.name() + "&id=" + discipline.getDisciplineId())
            .build())));
    return new InlineKeyboardMarkup(rows);
  }

  @Override
  public Action getAccordingAction() {
    return Action.MANAGER_REMOVE_DISCIPLINE;
  }
}
