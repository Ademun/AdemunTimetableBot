package org.ademun.timetablebot.handler.input;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.NonNull;
import org.ademun.timetablebot.dto.Group;
import org.ademun.timetablebot.state.ChatStateManager.ChatState;
import org.ademun.timetablebot.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class GroupNameInput implements Input {

  private final GroupService groupService;

  @Autowired
  public GroupNameInput(GroupService groupService) {
    this.groupService = groupService;
  }

  @Override
  public @NonNull SendMessage execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    String groupName = update.getMessage().getText();
    if (!validateGroupName(groupName)) {
      return SendMessage.builder().chatId(chatId)
          .text("Введите код группы в корректном формате (Например ИН-24-8)").build();
    }
    Group group = new Group(null, groupName, chatId);
    groupService.create(group);
    return SendMessage.builder().chatId(chatId).text("Группа " + groupName + " создана!").build();
  }

  private boolean validateGroupName(String groupName) {
    Pattern pattern = Pattern.compile("[А-я]{2,3}-[0-9]{2}-[0-9]{1,2}");
    Matcher matcher = pattern.matcher(groupName);
    return matcher.matches();
  }

  @Override
  public ChatState getAccordingState() {
    return ChatState.AWAITING_GROUP_NAME;
  }
}
