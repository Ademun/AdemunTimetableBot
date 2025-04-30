package org.ademun.timetablebot.service;

import lombok.Getter;
import org.ademun.timetablebot.context.ChatContext;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Getter
public class ChatContextService {
  private final ConcurrentMap<Long, ChatContext> chats = new ConcurrentHashMap<>();


  public Optional<ChatContext> getChatContext(long chatId) {
    return Optional.ofNullable(chats.get(chatId));
  }

  public void putChatContext(long chatId, ChatContext chatState) {
    chats.put(chatId, chatState);
  }

  public Optional<ChatContext> removeChatContext(long chatId) {
    return Optional.ofNullable(chats.remove(chatId));
  }
}
