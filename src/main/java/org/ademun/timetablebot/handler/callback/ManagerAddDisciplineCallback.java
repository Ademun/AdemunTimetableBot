package org.ademun.timetablebot.handler.callback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import org.ademun.timetablebot.dto.Discipline;
import org.ademun.timetablebot.dto.Group;
import org.ademun.timetablebot.service.DisciplineService;
import org.ademun.timetablebot.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
public class ManagerAddDisciplineCallback implements Callback {

  private final GroupService groupService;

  private final DisciplineService disciplineService;

  @Autowired
  public ManagerAddDisciplineCallback(GroupService groupService,
      DisciplineService disciplineService) {
    this.groupService = groupService;
    this.disciplineService = disciplineService;
  }

  @Override
  public @NonNull SendMessage execute(Update update, Map<String, String> callbackData) {
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    Group group = groupService.getByChannelId(chatId).orElseThrow();
    List<Discipline> groupDisciplines = groupService.getDisciplines(group.getGroupId());
    List<Discipline> disciplines = disciplineService.getAll();
    disciplines.removeAll(groupDisciplines);
    return SendMessage.builder().chatId(chatId)
        .text("Выберите существующую дисциплину или добавьте новую")
        .replyMarkup(getInlineKeyboardMarkup(disciplines)).build();
  }

  private InlineKeyboardMarkup getInlineKeyboardMarkup(List<Discipline> disciplines) {
    List<InlineKeyboardRow> rows = new ArrayList<>();
    rows.add(new InlineKeyboardRow(
        InlineKeyboardButton.builder().text("Добавить новую")
            .callbackData("action=" + Action.ADD_NEW_DISCIPLINE.name()).build()));
    disciplines.forEach(discipline -> rows.add(
        new InlineKeyboardRow(
            InlineKeyboardButton.builder().text(discipline.getName()).callbackData(
                    "action=" + Action.ADD_EXISTING_DISCIPLINE.name() + "&id="
                        + discipline.getDisciplineId())
                .build())));
    return new InlineKeyboardMarkup(rows);
  }

  @Override
  public Action getAccordingAction() {
    return Action.MANAGER_ADD_DISCIPLINE;
  }
}
