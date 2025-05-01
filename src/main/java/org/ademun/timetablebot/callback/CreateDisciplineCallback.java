package org.ademun.timetablebot.callback;

import lombok.NonNull;
import org.ademun.timetablebot.context.ChatContext;
import org.ademun.timetablebot.dto.DisciplineDto;
import org.ademun.timetablebot.service.ChatContextService;
import org.ademun.timetablebot.service.DisciplineService;
import org.ademun.timetablebot.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class CreateDisciplineCallback implements Callback {
  private final ChatContextService chatContextService;
  private final GroupService groupService;
  private final DisciplineService disciplineService;

  @Autowired
  public CreateDisciplineCallback(ChatContextService chatContextService, GroupService groupService,
      DisciplineService disciplineService) {
    this.chatContextService = chatContextService;
    this.groupService = groupService;
    this.disciplineService = disciplineService;
  }

  @Override
  public @NonNull SendMessage execute(Update update) {
    Long chatId = update.getMessage()
                        .getChatId();
    ChatContext context = chatContextService.getChatContext(chatId)
                                            .orElse(null);
    if (context == null) {
      return SendMessage.builder()
                        .chatId(chatId)
                        .text(
                            "Кажется вы ещё не создали группу в этом чате. Используйте команду " + "/start")
                        .build();
    }
    String message = update.getMessage()
                           .getText();
    Long groupId = groupService.getGroupByChannelId(chatId)
                               .orElseThrow()
                               .getGroupId();
    groupService.addDiscipline(groupId, new DisciplineDto(message));
    context.setChatState(ChatContext.State.IDLE);
    return SendMessage.builder()
                      .chatId(chatId)
                      .text("Дисциплина создана")
                      .build();
  }

  @Override
  public ChatContext.State getAccordingContext() {
    return ChatContext.State.CREATE_DISCIPLINE;
  }
}
