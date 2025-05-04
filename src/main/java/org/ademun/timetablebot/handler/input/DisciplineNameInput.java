package org.ademun.timetablebot.handler.input;

import java.util.Optional;
import lombok.NonNull;
import org.ademun.timetablebot.dto.Discipline;
import org.ademun.timetablebot.dto.Group;
import org.ademun.timetablebot.service.DisciplineService;
import org.ademun.timetablebot.service.GroupService;
import org.ademun.timetablebot.state.ChatStateManager.ChatState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class DisciplineNameInput implements Input {

  private final GroupService groupService;
  private final DisciplineService disciplineService;

  @Autowired
  public DisciplineNameInput(GroupService groupService, DisciplineService disciplineService) {
    this.groupService = groupService;
    this.disciplineService = disciplineService;
  }

  @Override
  public @NonNull SendMessage execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    String disciplineName = update.getMessage().getText();
    Group group = groupService.getByChannelId(chatId).orElseThrow();
    Optional<Discipline> existingDiscipline = disciplineService.getByName(disciplineName);

    if (existingDiscipline.isPresent()) {
      groupService.addDiscipline(group.getGroupId(), existingDiscipline.get());
      return SendMessage.builder().chatId(chatId).text(
          "Дисциплина " + existingDiscipline.get().getName()
              + " уже существует. Она добавлена в список группы").build();
    }

    Discipline discipline = new Discipline(null, disciplineName, null);
    Discipline createdDiscipline = disciplineService.create(discipline);
    groupService.addDiscipline(group.getGroupId(), createdDiscipline);
    return SendMessage.builder().chatId(chatId)
        .text("Дисциплина " + createdDiscipline.getName() + " создана").build();
  }

  @Override
  public ChatState getAccordingState() {
    return ChatState.AWAITING_DISCIPLINE_NAME;
  }
}
