package org.ademun.timetablebot.handler.callback;

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

@Component
public class AddExistingDisciplineCallback implements Callback {

  private final GroupService groupService;
  private final DisciplineService disciplineService;

  @Autowired
  public AddExistingDisciplineCallback(GroupService groupService,
      DisciplineService disciplineService) {
    this.groupService = groupService;
    this.disciplineService = disciplineService;
  }

  @Override
  public @NonNull SendMessage execute(Update update, Map<String, String> callbackData) {
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    Long disciplineId = Long.parseLong(callbackData.get("id"));

    Group group = groupService.getByChannelId(chatId).orElseThrow();
    Discipline discipline = disciplineService.getById(disciplineId).orElseThrow();
    groupService.addDiscipline(group.getGroupId(), discipline);
    return SendMessage.builder().text("Дисциплина " + discipline.getName() + " добавлена").build();
  }

  @Override
  public Action getAccordingAction() {
    return Action.ADD_EXISTING_DISCIPLINE;
  }
}
