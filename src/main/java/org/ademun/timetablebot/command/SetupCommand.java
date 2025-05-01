package org.ademun.timetablebot.command;

import lombok.NonNull;
import org.ademun.timetablebot.context.ChatContext;
import org.ademun.timetablebot.dto.GroupDto;
import org.ademun.timetablebot.service.ChatContextService;
import org.ademun.timetablebot.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
public class SetupCommand implements Command {
  private final ChatContextService chatContextService;
  private final GroupService groupService;

  @Autowired
  public SetupCommand(ChatContextService chatContextService, GroupService groupService) {
    this.chatContextService = chatContextService;
    this.groupService = groupService;
  }

  @Override
  public @NonNull SendMessage execute(Update update) {
    Long chatId = update.getMessage()
                        .getChatId();
    Optional<GroupDto> group = groupService.getGroupByChannelId(chatId);
    if (group.isPresent()) {
      return SendMessage.builder()
                        .chatId(chatId)
                        .text("Вы уже создали группу в этом чате")
                        .build();
    }
    createContext(chatId);
    return SendMessage.builder()
                      .chatId(chatId)
                      .text("Введите код группы (Например ИН-24-8)")
                      .build();
  }

  private void createContext(Long chatId) {
    ChatContext context = new ChatContext();
    context.setChatState(ChatContext.State.CHAT_SETUP);
    chatContextService.putChatContext(chatId, context);
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
