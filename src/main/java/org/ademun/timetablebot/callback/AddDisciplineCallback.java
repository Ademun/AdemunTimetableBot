package org.ademun.timetablebot.callback;

import lombok.NonNull;
import org.ademun.timetablebot.context.ChatContext;
import org.ademun.timetablebot.context.ChatContext.State;
import org.ademun.timetablebot.dto.DisciplineDto;
import org.ademun.timetablebot.service.ChatContextService;
import org.ademun.timetablebot.service.DisciplineService;
import org.ademun.timetablebot.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class AddDisciplineCallback implements Callback {
  private final ChatContextService chatContextService;
  private final GroupService groupService;
  private final DisciplineService disciplineService;

  @Autowired
  public AddDisciplineCallback(ChatContextService chatContextService, GroupService groupService,
      DisciplineService disciplineService) {
    this.chatContextService = chatContextService;
    this.groupService = groupService;
    this.disciplineService = disciplineService;
  }

  @Override
  public @NonNull SendMessage execute(Update update) {
    Long chatId = update.getCallbackQuery()
                        .getMessage()
                        .getChatId();
    ChatContext context = chatContextService.getChatContext(chatId)
                                            .orElseThrow();
    context.setChatState(State.IDLE);
    String callbackData = update.getCallbackQuery()
                                .getData();

    if (callbackData.equals("create_new")) {
      return SendMessage.builder()
                        .chatId(chatId)
                        .text("Введите название дисциплины")
                        .build();
    }
    DisciplineDto disciplineDto = disciplineService.getDisciplineById(Long.parseLong(callbackData));
    groupService.addDiscipline(groupService.getGroupByChannelId(chatId)
                                           .getGroup_id(), disciplineDto);
    return SendMessage.builder()
                      .chatId(chatId)
                      .text("Дисциплина успешно добавлена")
                      .build();
  }

  @Override
  public State getAccordingContext() {
    return State.GROUP_ADD_DISCIPLINE;
  }
}
