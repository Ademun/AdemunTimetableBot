package org.ademun.timetablebot.handler.callback;

import java.util.Map;
import lombok.NonNull;
import org.ademun.timetablebot.dto.Group;
import org.ademun.timetablebot.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class RemoveDisciplineCallback implements Callback {

  private final GroupService groupService;

  @Autowired
  public RemoveDisciplineCallback(GroupService groupService) {
    this.groupService = groupService;
  }

  @Override
  public @NonNull SendMessage execute(Update update, Map<String, String> callbackData) {
    Long chatId = update.getCallbackQuery().getMessage().getChatId();
    Long disciplineId = Long.parseLong(callbackData.get("id"));
    Group group = groupService.getByChannelId(chatId).orElseThrow();
    groupService.removeDiscipline(group.getGroupId(), disciplineId);
    return SendMessage.builder().chatId(chatId)
        .text("Дисциплина удалена из списка вашей группы").build();
  }

  @Override
  public Action getAccordingAction() {
    return Action.REMOVE_DISCIPLINE;
  }
}
