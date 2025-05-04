package org.ademun.timetablebot.handler.command;

import java.util.List;
import lombok.NonNull;
import org.ademun.timetablebot.dto.Discipline;
import org.ademun.timetablebot.dto.Group;
import org.ademun.timetablebot.dto.Professor;
import org.ademun.timetablebot.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class GroupInfoCommand implements Command {

  private final GroupService groupService;

  @Autowired
  public GroupInfoCommand(GroupService groupService) {
    this.groupService = groupService;
  }

  @Override
  public @NonNull SendMessage execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    Group group = groupService.getByChannelId(chatId).orElseThrow();
    List<Discipline> disciplines = groupService.getDisciplines(group.getGroupId());
    List<Professor> professors = groupService.getProfessors(group.getGroupId());
    return SendMessage.builder().chatId(chatId)
        .text(generateGroupInfo(group, disciplines, professors)).build();
  }

  private String generateGroupInfo(Group group, List<Discipline> disciplines,
      List<Professor> professors) {
    StringBuilder groupInfo = new StringBuilder("Группа " + group.getName() + "\n");
    groupInfo.append(String.format("Дисциплины (%d):\n", disciplines.size()));
    disciplines.forEach(discipline -> groupInfo.append(discipline.getName()).append("\n"));
    groupInfo.append(String.format("Преподаватели (%d):\n", professors.size()));
    professors.forEach(professor -> groupInfo.append(professor.getFullName()).append("\n"));
    return groupInfo.toString();
  }

  @Override
  public String getName() {
    return "/info";
  }

  @Override
  public String getDescription() {
    return "Информация о группе";
  }
}
