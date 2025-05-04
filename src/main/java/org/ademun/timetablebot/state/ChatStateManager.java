package org.ademun.timetablebot.state;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class ChatStateManager {

  private final Map<Long, ChatState> states = new ConcurrentHashMap<>();

  public ChatState getChatState(long chatId) {
    return states.getOrDefault(chatId, ChatState.DEFAULT);
  }

  public void setChatState(long chatId, ChatState state) {
    states.put(chatId, state);
  }

  public void clearChatState(long chatId) {
    states.remove(chatId);
  }

  public enum ChatState {
    DEFAULT, AWAITING_GROUP_NAME, AWAITING_DISCIPLINE_NAME
  }
}
