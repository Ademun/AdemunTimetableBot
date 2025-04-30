package org.ademun.timetablebot.service;

import lombok.Getter;
import org.ademun.timetablebot.command.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Getter
public class CommandService {
  private final List<Command> commands;

  @Autowired
  public CommandService(Collection<Command> commands) {
    this.commands = new ArrayList<>(commands);
  }

  public SendMessage handle(Update update) {
    String commandName = update.getMessage().getText();
    Command command =
        commands.stream().filter(cmnd -> cmnd.getName().equals(commandName)).findFirst()
            .orElse(null);
    if (command == null) {
      return null;
    }
    return command.execute(update);
  }
}
