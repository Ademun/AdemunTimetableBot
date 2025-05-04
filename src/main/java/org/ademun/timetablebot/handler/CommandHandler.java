package org.ademun.timetablebot.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.ademun.timetablebot.dto.Group;
import org.ademun.timetablebot.handler.command.Command;
import org.ademun.timetablebot.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
@Getter
public class CommandHandler implements UpdateHandler {

  private final List<Command> commands;
  private final GroupService groupService;

  @Autowired
  public CommandHandler(Collection<Command> commands, GroupService groupService) {

    this.commands = new ArrayList<>(commands);
    this.groupService = groupService;
  }

  @Override
  public void onUpdate(TelegramClient client, Update update) {
    if (update.hasMessage() && update.getMessage().isCommand() && update.getMessage().hasText()) {
      SendMessage message = handle(update);
      try {
        client.execute(message);
      } catch (TelegramApiException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private SendMessage handle(Update update) {
    Long chatId = update.getMessage().getChatId();
    String commandName = update.getMessage().getText();
    log.info("Received command: {}", commandName);
    Optional<Group> group = groupService.getByChannelId(chatId);
    if (group.isEmpty() && !Objects.equals(commandName, "/start")) {
      return SendMessage.builder().chatId(chatId)
          .text("Вы ещё не создали группу в этом чате. Создайте её через /start").build();
    }
    Command command = commands.stream().filter(cmnd -> cmnd.getName().equals(commandName))
        .findFirst().orElse(null);
    if (command == null) {
      return null;
    }
    return command.execute(update);
  }
}
