package org.ademun.timetablebot.callback;

import lombok.NonNull;
import org.ademun.timetablebot.context.ChatContext;
import org.ademun.timetablebot.dto.GroupDto;
import org.ademun.timetablebot.service.ChatStateService;
import org.ademun.timetablebot.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SetupChatFirstStepCallback implements Callback {
  private final ChatStateService chatStateService;
  private final GroupService groupService;

  @Autowired
  public SetupChatFirstStepCallback(ChatStateService chatStateService, GroupService groupService) {
    this.chatStateService = chatStateService;
    this.groupService = groupService;
  }

  @Override
  public @NonNull SendMessage execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    if (chatStateService.getChatState(chatId).isEmpty()) {
      chatStateService.getChatState(chatId)
          .ifPresent(chatState -> chatState.setChatState(ChatContext.State.IDLE));
      return SendMessage.builder().chatId(chatId)
          .text("Кажется вы ещё не создали группу в этом чате. Используйте команду /start").build();
    }
    String groupName = update.getMessage().getText();
    if (!validateGroupName(groupName)) {
      return SendMessage.builder().chatId(chatId)
          .text("Введите код группы в корректном формате (Например ИН-24-8)").build();
    }
    GroupDto groupDto = new GroupDto(groupName, chatId);
    chatStateService.getChatState(chatId)
        .ifPresent(chatState -> chatState.setChatState(ChatContext.State.IDLE));
    GroupDto response = groupService.createGroup(groupDto);
    System.out.println(response);
    return SendMessage.builder().chatId(chatId).text("Группа " + groupName + " создана!").build();
  }

  private boolean validateGroupName(String groupName) {
    Pattern pattern = Pattern.compile("[А-я]{2,3}-[0-9]{2}-[0-9]{1,2}");
    Matcher matcher = pattern.matcher(groupName);
    return matcher.matches();
  }

  @Override
  public ChatContext.State getAccordingContext() {
    return ChatContext.State.CHAT_SETUP;
  }
}
