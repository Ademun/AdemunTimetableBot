package org.ademun.timetablebot.command;

import lombok.NonNull;
import org.ademun.timetablebot.context.ChatContext;
import org.ademun.timetablebot.dto.DisciplineDto;
import org.ademun.timetablebot.dto.GroupDto;
import org.ademun.timetablebot.service.ChatStateService;
import org.ademun.timetablebot.service.DisciplineService;
import org.ademun.timetablebot.service.GroupService;
import org.jetbrains.annotations.NotNull;
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
public class AddDisciplineCommand implements Command {
  private final ChatStateService chatStateService;
  private final GroupService groupService;
  private final DisciplineService disciplineService;

  @Autowired
  public AddDisciplineCommand(ChatStateService chatStateService, GroupService groupService,
      DisciplineService disciplineService) {
    this.chatStateService = chatStateService;
    this.groupService = groupService;
    this.disciplineService = disciplineService;
  }

  @Override
  public @NonNull SendMessage execute(Update update) {
    Long chatId = update.getMessage().getChatId();
    if (chatStateService.getChatState(chatId).isEmpty()) {
      return SendMessage.builder().chatId(chatId)
          .text("Кажется вы ещё не создали группу в этом чате. Используйте команду /start").build();
    }
    chatStateService.getChatState(chatId)
        .ifPresent(chatState -> chatState.setChatState(ChatContext.State.GROUP_ADD_DISCIPLINE));
    GroupDto groupDto = groupService.getGroupByChannelId(chatId);
    List<DisciplineDto> disciplines = disciplineService.getAllDisciplines();
    return SendMessage.builder().chatId(chatId)
        .text("Добавьте новую дисциплину или выберите существующую")
        .replyMarkup(disciplinesToInlineKeyboard(disciplines)).build();
  }

  private InlineKeyboardMarkup disciplinesToInlineKeyboard(List<DisciplineDto> disciplines) {
    disciplines = sortDisciplinesAlphabetically(disciplines);
    List<InlineKeyboardRow> rows = new ArrayList<>();
    rows.add(new InlineKeyboardRow(
        InlineKeyboardButton.builder().text("Создать новую").callbackData("create_new").build()));
    for (DisciplineDto discipline : disciplines) {
      InlineKeyboardRow row = new InlineKeyboardRow(
          InlineKeyboardButton.builder().text(discipline.getName())
              .callbackData(String.valueOf(discipline.getDiscipline_id())).build());
      rows.add(row);
    }
    return new InlineKeyboardMarkup(rows);
  }

  @NotNull
  private List<DisciplineDto> sortDisciplinesAlphabetically(List<DisciplineDto> disciplines) {
    Collator collator = Collator.getInstance(Locale.of("ru", "RU"));
    disciplines =
        disciplines.stream().sorted((d1, d2) -> collator.compare(d1.getName(), d2.getName()))
            .toList();
    return disciplines;
  }

  @Override
  public String getName() {
    return "/discipline";
  }

  @Override
  public String getDescription() {
    return "Добавить дисциплину";
  }
}
