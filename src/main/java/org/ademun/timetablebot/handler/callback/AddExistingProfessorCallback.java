package org.ademun.timetablebot.handler.callback;

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

@Component
public class AddExistingProfessorCallback implements Callback {

  private final GroupService groupService;
  private final ProfessorService professorService;

  @Autowired
  public AddExistingProfessorCallback(GroupService groupService,
      ProfessorService professorService) {
    this.groupService = groupService;
    this.professorService = professorService;
  }

  @Override
  public @NonNull SendMessage execute(Update update, Map<String, String> callbackData) {
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    Long professorId = Long.parseLong(callbackData.get("id"));

    Group group = groupService.getByChannelId(chatId).orElseThrow();
    Professor professor = professorService.getById(professorId).orElseThrow();
    groupService.addProfessor(group.getGroupId(), professor);
    return SendMessage.builder().chatId(chatId)
        .text("Преподаватель " + professor.getShortName() + " добавлен")
        .build();
  }

  @Override
  public Action getAccordingAction() {
    return Action.ADD_EXISTING_PROFESSOR;
  }
}
