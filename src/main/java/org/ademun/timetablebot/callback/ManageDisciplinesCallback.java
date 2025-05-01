package org.ademun.timetablebot.callback;

import lombok.NonNull;
import org.ademun.timetablebot.command.CallDisciplineManagerCommand;
import org.ademun.timetablebot.context.ChatContext;
import org.ademun.timetablebot.dto.DisciplineDto;
import org.ademun.timetablebot.service.ChatContextService;
import org.ademun.timetablebot.service.DisciplineService;
import org.ademun.timetablebot.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class ManageDisciplinesCallback implements Callback {
  private final ChatContextService chatContextService;
  private final GroupService groupService;
  private final DisciplineService disciplineService;

  @Autowired
  public ManageDisciplinesCallback(ChatContextService chatContextService, GroupService groupService,
      DisciplineService disciplineService) {
    this.chatContextService = chatContextService;
    this.groupService = groupService;
    this.disciplineService = disciplineService;
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
                            "Кажется вы ещё не создали группу в этом чате. Используйте команду " + "/start")
                        .build();
    }
    Long groupId = groupService.getGroupByChannelId(chatId)
                               .orElseThrow()
                               .getGroupId();
    if (update.getCallbackQuery()
              .getData()
              .equals(CallDisciplineManagerCommand.CallbackData.ADD_DISCIPLINE.name())) {
      context.setChatState(ChatContext.State.ADD_DISCIPLINE);
      List<DisciplineDto> existingDisciplines = disciplineService.getAllDisciplines();
      return SendMessage.builder()
                        .chatId(chatId)
                        .text("Выберите существующую дисциплину или добавьте новую")
                        .replyMarkup(getInlineKeyboardMarkupForAddDiscipline(existingDisciplines))
                        .build();
    } else {
      List<DisciplineDto> groupDisciplines = groupService.getDisciplines(groupId);
      context.setChatState(ChatContext.State.DELETE_DISCIPLINE);
      return SendMessage.builder()
                        .chatId(chatId)
                        .text("Выберите дисциплину")
                        .replyMarkup(getInlineKeyboardMarkupForDeleteDiscipline(groupDisciplines))
                        .build();
    }
  }

  private InlineKeyboardMarkup getInlineKeyboardMarkupForAddDiscipline(
      List<DisciplineDto> disciplines) {
    disciplines = sortDisciplinesAlphabetically(disciplines);
    List<InlineKeyboardRow> rows = new ArrayList<>();
    rows.add(new InlineKeyboardRow(InlineKeyboardButton.builder()
                                                       .text("Создать новую")
                                                       .callbackData(
                                                           CallbackData.CREATE_DISCIPLINE.name())
                                                       .build()));
    for (DisciplineDto discipline : disciplines) {
      InlineKeyboardRow row = new InlineKeyboardRow(InlineKeyboardButton.builder()
                                                                        .text(discipline.getName())
                                                                        .callbackData(
                                                                            String.valueOf(
                                                                                discipline.getDisciplineId()))
                                                                        .build());
      rows.add(row);
    }
    return new InlineKeyboardMarkup(rows);
  }

  private InlineKeyboardMarkup getInlineKeyboardMarkupForDeleteDiscipline(
      List<DisciplineDto> disciplines) {
    disciplines = sortDisciplinesAlphabetically(disciplines);
    List<InlineKeyboardRow> rows = new ArrayList<>();
    for (DisciplineDto discipline : disciplines) {
      InlineKeyboardRow row = new InlineKeyboardRow(InlineKeyboardButton.builder()
                                                                        .text(discipline.getName())
                                                                        .callbackData(
                                                                            String.valueOf(
                                                                                discipline.getDisciplineId()))
                                                                        .build());
      rows.add(row);
    }
    return new InlineKeyboardMarkup(rows);
  }

  private List<DisciplineDto> sortDisciplinesAlphabetically(List<DisciplineDto> disciplines) {
    Collator collator = Collator.getInstance(Locale.of("ru", "RU"));
    disciplines = disciplines.stream()
                             .sorted((d1, d2) -> collator.compare(d1.getName(), d2.getName()))
                             .toList();
    return disciplines;
  }

  @Override
  public ChatContext.State getAccordingContext() {
    return ChatContext.State.MANAGE_DISCIPLINES;
  }

  public enum CallbackData {
    CREATE_DISCIPLINE
  }
}
