package org.ademun.timetablebot.handler.callback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import org.ademun.timetablebot.dto.Group;
import org.ademun.timetablebot.dto.Professor;
import org.ademun.timetablebot.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

@Component
public class ManagerRemoveProfessorCallback implements Callback {

  private final GroupService groupService;

  @Autowired
  public ManagerRemoveProfessorCallback(GroupService groupService) {
    this.groupService = groupService;
  }

  @Override
  public @NonNull SendMessage execute(Update update, Map<String, String> callbackData) {
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    Group group = groupService.getByChannelId(chatId).orElseThrow();
    List<Professor> professors = groupService.getProfessors(group.getGroupId());

    return SendMessage.builder().chatId(chatId)
        .text("Выберите преподавателя, которого хотите удалить")
        .replyMarkup(getInlineKeyboardMarkup(professors)).build();
  }

  private InlineKeyboardMarkup getInlineKeyboardMarkup(List<Professor> professors) {
    List<InlineKeyboardRow> rows = new ArrayList<>();
    professors.forEach(professor -> rows.add(
        new InlineKeyboardRow(InlineKeyboardButton.builder().text(professor.getShortName())
            .callbackData(
                "action=" + Action.REMOVE_PROFESSOR.name() + "&id=" + professor.getProfessorId())
            .build())));
    return new InlineKeyboardMarkup(rows);
  }

  @Override
  public Action getAccordingAction() {
    return Action.MANAGER_REMOVE_PROFESSOR;
  }
}
