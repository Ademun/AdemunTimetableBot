package org.ademun.timetablebot.command;

import lombok.NonNull;
import org.ademun.timetablebot.dto.DisciplineDto;
import org.ademun.timetablebot.dto.GroupDto;
import org.ademun.timetablebot.dto.ProfessorDto;
import org.ademun.timetablebot.service.ChatStateService;
import org.ademun.timetablebot.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class GroupInfoCommand implements Command {
  private final ChatStateService chatStateService;
  private final GroupService groupService;

  @Autowired
  public GroupInfoCommand(ChatStateService chatStateService, GroupService groupService) {
    this.chatStateService = chatStateService;
    this.groupService = groupService;
  }

  @Override
  public @NonNull SendMessage execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    if (chatStateService.getChatState(chatId).isEmpty()) {
      return SendMessage.builder().chatId(chatId)
          .text("Кажется вы ещё не создали группу в этом чате. Используйте команду /start").build();
    }
    GroupDto groupDto = groupService.getGroupByChannelId(chatId);
    List<DisciplineDto> disciplines = groupService.getDisciplines(groupDto.getGroup_id());
    List<ProfessorDto> professors = groupService.getProfessors(groupDto.getGroup_id());
    StringBuilder text = new StringBuilder().append(groupDto.getName()).append("\n")
        .append(String.format("Предметы (%d):\n", disciplines.size()));
    for (DisciplineDto discipline : disciplines) {
      text.append(String.format("<a href=\"%s\">%s</a>", discipline.getUrl(), discipline.getName()))
          .append("\n");
    }
    text.append(String.format("Преподаватели (%d):\n", professors.size()));
    for (ProfessorDto professor : professors) {
      text.append(
          String.format("<a href=\"%s\">%s %s %s</a>", professor.getUrl(), professor.getFirstName(),
              professor.getLastName(), professor.getPatronymic())).append("\n");
    }
    return SendMessage.builder().chatId(chatId).text(text.toString()).parseMode(ParseMode.HTML)
        .build();
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
