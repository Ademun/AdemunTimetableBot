package org.ademun.timetablebot.handler.callback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import org.ademun.timetablebot.dto.Group;
import org.ademun.timetablebot.dto.Professor;
import org.ademun.timetablebot.service.GroupService;
import org.ademun.timetablebot.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
public class ManagerAddProfessorCallback implements Callback {

  private final GroupService groupService;

  private final ProfessorService professorService;

  @Autowired
  public ManagerAddProfessorCallback(GroupService groupService,
      ProfessorService professorService) {
    this.groupService = groupService;
    this.professorService = professorService;
  }

  @Override
  public @NonNull SendMessage execute(Update update, Map<String, String> callbackData) {
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    Group group = groupService.getByChannelId(chatId).orElseThrow();
    List<Professor> groupProfessors = groupService.getProfessors(group.getGroupId());
    List<Professor> professors = professorService.getAll();
    professors.removeAll(groupProfessors);
    return SendMessage.builder().chatId(chatId)
        .text("Выберите существующего преподавателя или добавьте нового")
        .replyMarkup(getInlineKeyboardMarkup(professors)).build();
  }

  private InlineKeyboardMarkup getInlineKeyboardMarkup(List<Professor> professors) {
    List<InlineKeyboardRow> rows = new ArrayList<>();
    rows.add(new InlineKeyboardRow(
        InlineKeyboardButton.builder().text("Добавить нового")
            .callbackData("action=" + Action.ADD_NEW_PROFESSOR.name()).build()));
    professors.forEach(professor -> rows.add(
        new InlineKeyboardRow(
            InlineKeyboardButton.builder().text(professor.getShortName()).callbackData(
                    "action=" + Action.ADD_EXISTING_PROFESSOR.name() + "&id="
                        + professor.getProfessorId())
                .build())));
    return new InlineKeyboardMarkup(rows);
  }

  @Override
  public Action getAccordingAction() {
    return Action.MANAGER_ADD_PROFESSOR;
  }
}
