package org.ademun.timetablebot;

import org.ademun.timetablebot.command.Command;
import org.ademun.timetablebot.config.BotConfig;
import org.ademun.timetablebot.context.ChatContext;
import org.ademun.timetablebot.dto.GroupDto;
import org.ademun.timetablebot.service.CallbackService;
import org.ademun.timetablebot.service.ChatContextService;
import org.ademun.timetablebot.service.CommandService;
import org.ademun.timetablebot.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class Bot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
  private final BotConfig config;
  private final TelegramClient telegramClient;
  private final ChatContextService chatContextService;
  private final CommandService commandService;
  private final CallbackService callbackService;
  private final GroupService groupService;

  @Autowired
  public Bot(BotConfig config, ChatContextService chatContextService, CommandService commandService,
      CallbackService callbackService, GroupService groupService) {
    this.config = config;
    this.telegramClient = new OkHttpTelegramClient(config.getToken());
    this.chatContextService = chatContextService;
    this.commandService = commandService;
    this.callbackService = callbackService;
    this.groupService = groupService;
    setupCommands();
    retrieveGroups();
    System.out.println("Bot: " + config.getName() + " has started");
  }

  private void setupCommands() {
    List<BotCommand> botCommands = new ArrayList<>();
    for (Command command : commandService.getCommands()) {
      botCommands.add(new BotCommand(command.getName(), command.getDescription()));
    }
    try {
      telegramClient.execute(new SetMyCommands(botCommands));
    } catch (TelegramApiException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void consume(Update update) {
    if (update.hasMessage() && update.getMessage().hasText()) {
      if (update.getMessage().hasText() && update.getMessage().getText().startsWith("/")) {
        handleCommand(update);
      } else if (chatContextService.getChatContext(update.getMessage().getChatId()).orElseThrow()
          .getChatState() != ChatContext.State.IDLE) {
        handleCallback(update);
      }
    } else if (update.hasCallbackQuery()) {
      AnswerCallbackQuery answer = new AnswerCallbackQuery(update.getCallbackQuery().getId());
      handleCallback(update);
      try {
        telegramClient.execute(answer);
      } catch (TelegramApiException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void handleCommand(Update update) {
    try {
      telegramClient.execute(commandService.handle(update));
    } catch (TelegramApiException e) {
      throw new RuntimeException(e);
    }
  }

  private void handleCallback(Update update) {
    try {
      telegramClient.execute(callbackService.handle(update));
    } catch (TelegramApiException e) {
      throw new RuntimeException(e);
    }
  }

  private void retrieveGroups() {
    List<GroupDto> groups = groupService.getAllGroups();
    for (GroupDto group : groups) {
      chatContextService.putChatContext(group.getChannelId(), new ChatContext());
    }
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
