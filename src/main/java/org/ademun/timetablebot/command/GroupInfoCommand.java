package org.ademun.timetablebot.command;

import lombok.NonNull;
import org.ademun.timetablebot.context.ChatContext;
import org.ademun.timetablebot.dto.DisciplineDto;
import org.ademun.timetablebot.dto.GroupDto;
import org.ademun.timetablebot.dto.ProfessorDto;
import org.ademun.timetablebot.service.ChatContextService;
import org.ademun.timetablebot.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class GroupInfoCommand implements Command {
  private final ChatContextService chatContextService;
  private final GroupService groupService;

  @Autowired
  public GroupInfoCommand(ChatContextService chatContextService, GroupService groupService) {
    this.chatContextService = chatContextService;
    this.groupService = groupService;
  }

  @Override
  public @NonNull SendMessage execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    ChatContext context = chatContextService.getChatContext(chatId).orElse(null);

    if (context == null) {
      return SendMessage.builder().chatId(chatId)
          .text("Кажется вы ещё не создали группу в этом чате. Используйте команду /start").build();
    }
    context.setChatState(ChatContext.State.IDLE);

    GroupDto groupDto = groupService.getGroupByChannelId(chatId).orElseThrow();
    List<DisciplineDto> disciplines = groupService.getDisciplines(groupDto.getGroupId());
    List<ProfessorDto> professors = groupService.getProfessors(groupDto.getGroupId());

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
