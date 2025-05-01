package org.ademun.timetablebot.callback;

import lombok.NonNull;
import org.ademun.timetablebot.context.ChatContext;
import org.ademun.timetablebot.service.ChatContextService;
import org.ademun.timetablebot.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class RemoveDisciplineCallback implements Callback {
  private final ChatContextService chatContextService;
  private final GroupService groupService;

  @Autowired
  public RemoveDisciplineCallback(ChatContextService chatContextService,
      GroupService groupService) {
    this.chatContextService = chatContextService;
    this.groupService = groupService;
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
    Long disciplineId = Long.valueOf(update.getCallbackQuery()
                                           .getData());
    Long groupId = groupService.getGroupByChannelId(chatId)
                               .orElseThrow()
                               .getGroupId();
    groupService.removeDiscipline(groupId, disciplineId);
    context.setChatState(ChatContext.State.IDLE);
    return SendMessage.builder()
                      .chatId(chatId)
                      .text("Дисциплина удалена")
                      .build();
  }

  @Override
  public ChatContext.State getAccordingContext() {
    return ChatContext.State.DELETE_DISCIPLINE;
  }
}
