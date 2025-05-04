package org.ademun.timetablebot.handler.command;

import java.util.Optional;
import lombok.NonNull;
import org.ademun.timetablebot.dto.Group;
import org.ademun.timetablebot.state.ChatStateManager;
import org.ademun.timetablebot.state.ChatStateManager.ChatState;
import org.ademun.timetablebot.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class SetupGroupCommand implements Command {

  private final ChatStateManager chatStateManager;
  private final GroupService groupService;

  @Autowired
  public SetupGroupCommand(ChatStateManager chatStateManager, GroupService groupService) {
    this.chatStateManager = chatStateManager;
    this.groupService = groupService;
  }

  @Override
  public @NonNull SendMessage execute(Update update) {
    Long chatId = update.getMessage()
        .getChatId();
    Optional<Group> group = groupService.getByChannelId(chatId);
    if (group.isPresent()) {
      return SendMessage.builder()
          .chatId(chatId)
          .text("Вы уже создали группу в этом чате")
          .build();
    }
    chatStateManager.setChatState(chatId, ChatState.AWAITING_GROUP_NAME);
    return SendMessage.builder()
        .chatId(chatId)
        .text("Введите код группы (Например ИН-24-8)")
        .build();
  }

  @Override
  public String getName() {
    return "/start";
  }

  @Override
  public String getDescription() {
    return "Начальная настройка группы";
  }
}
