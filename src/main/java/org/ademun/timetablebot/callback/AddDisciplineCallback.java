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

import java.util.List;

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
                                            .orElse(null);
    if (context == null) {
      return SendMessage.builder()
                        .chatId(chatId)
                        .text(
                            "Кажется вы ещё не создали группу в этом чате. Используйте команду /start")
                        .build();
    }
    String callbackData = update.getCallbackQuery()
                                .getData();
    if (callbackData.equals(ManageDisciplinesCallback.CallbackData.CREATE_DISCIPLINE.name())) {
      context.setChatState(ChatContext.State.CREATE_DISCIPLINE);
      return SendMessage.builder()
                        .chatId(chatId)
                        .text("Введите название дисциплины")
                        .build();
    }
    Long disciplineId = Long.valueOf(callbackData);
    Long groupId = groupService.getGroupByChannelId(chatId)
                               .orElseThrow()
                               .getGroupId();
    List<DisciplineDto> disciplines = groupService.getDisciplines(groupId);
    DisciplineDto discipline = disciplineService.getDisciplineById(disciplineId)
                                                .orElseThrow();
    if (disciplines.contains(discipline)) {
      return SendMessage.builder()
                        .chatId(chatId)
                        .text("Эта дисциплина уже есть в группе")
                        .build();
    }
    groupService.addDiscipline(groupId, discipline);
    context.setChatState(ChatContext.State.IDLE);
    return SendMessage.builder()
                      .chatId(chatId)
                      .text("Дисциплина добавлена")
                      .build();
  }

  @Override
  public ChatContext.State getAccordingContext() {
    return ChatContext.State.ADD_DISCIPLINE;
  }
}
