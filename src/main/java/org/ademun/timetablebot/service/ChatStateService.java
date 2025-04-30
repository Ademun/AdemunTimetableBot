package org.ademun.timetablebot.service;

import lombok.Getter;
import org.ademun.timetablebot.context.ChatContext;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Getter
public class ChatStateService {
  private final ConcurrentMap<Long, ChatContext> chatStates = new ConcurrentHashMap<>();


  public Optional<ChatContext> getChatState(long chatId) {
    return Optional.ofNullable(chatStates.get(chatId));
  }

  public void putChatState(long chatId, ChatContext chatState) {
    chatStates.put(chatId, chatState);
  }

  public Optional<ChatContext> removeChatState(long chatId) {
    return Optional.ofNullable(chatStates.remove(chatId));
  }
}
