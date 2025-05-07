package org.ademun.timetablebot.handler.input;

import java.util.Optional;
import lombok.NonNull;
import org.ademun.timetablebot.dto.Group;
import org.ademun.timetablebot.dto.Professor;
import org.ademun.timetablebot.service.GroupService;
import org.ademun.timetablebot.service.ProfessorService;
import org.ademun.timetablebot.state.ChatStateManager.ChatState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class ProfessorNameInput implements Input {

  private final GroupService groupService;
  private final ProfessorService professorService;

  @Autowired
  public ProfessorNameInput(GroupService groupService, ProfessorService professorService) {
    this.groupService = groupService;
    this.professorService = professorService;
  }

  @Override
  public @NonNull SendMessage execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    String professorName = update.getMessage().getText();
    if (!validateProfessorName(professorName)) {
      return SendMessage.builder().chatId(chatId)
          .text("Введите имя преподавателя в корректном формате (Фамилия Имя Отчество)").build();
    }
    String[] split = professorName.split(" ");
    Group group = groupService.getByChannelId(chatId).orElseThrow();
    Optional<Professor> existingProfessor = professorService.getByFullName(professorName);

    if (existingProfessor.isPresent()) {
      groupService.addProfessor(group.getGroupId(), existingProfessor.get());
      return SendMessage.builder().chatId(chatId).text(
          "Преподаватель " + existingProfessor.get().getShortName()
              + " уже существует. Он добавлен в список группы").build();
    }

    Professor professor = new Professor(null, split[0], split[1], split[2], null);
    Professor createdProfessor = professorService.create(professor);
    groupService.addProfessor(group.getGroupId(), createdProfessor);
    return SendMessage.builder().chatId(chatId)
        .text("Преподаватель " + createdProfessor.getShortName() + " создан").build();
  }

  private boolean validateProfessorName(String name) {
    return name.split(" ").length == 3 && !name.contains(".");
  }

  @Override
  public ChatState getAccordingState() {
    return ChatState.AWAITING_PROFESSOR_NAME;
  }
}
