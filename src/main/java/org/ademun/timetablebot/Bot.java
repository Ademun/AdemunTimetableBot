package org.ademun.timetablebot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.ademun.timetablebot.config.BotConfig;
import org.ademun.timetablebot.handler.UpdateHandler;
import org.ademun.timetablebot.handler.command.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class Bot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

  private final BotConfig config;
  private final TelegramClient telegramClient;
  private final List<UpdateHandler> listeners;
  private final List<Command> commands;

  @Autowired
  public Bot(BotConfig config, Collection<UpdateHandler> listeners,
      Collection<Command> commands) {
    this.config = config;
    this.telegramClient = new OkHttpTelegramClient(config.getToken());
    this.listeners = new ArrayList<>(listeners);
    this.commands = new ArrayList<>(commands);
    setupCommands();
  }

  private void setupCommands() {
    List<BotCommand> botCommands = new ArrayList<>();
    commands.forEach(command ->
        botCommands.add(new BotCommand(command.getName(), command.getDescription())));
    try {
      telegramClient.execute(new SetMyCommands(botCommands));
    } catch (TelegramApiException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void consume(Update update) {
    listeners.forEach(listener -> listener.onUpdate(telegramClient, update));
  }

  @Override
  public String getBotToken() {
    return config.getToken();
  }

  @Override
  public LongPollingUpdateConsumer getUpdatesConsumer() {
    return this;
  }
}
