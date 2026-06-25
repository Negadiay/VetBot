package com.medvet.vetbot.bot;

import com.medvet.vetbot.domain.BotState;
import com.medvet.vetbot.domain.User;
import com.medvet.vetbot.repository.UserRepository;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public class UserSession {

    @Getter
    private final User user;
    private final TelegramClient client;
    private final UserRepository userRepository;
    private final StateDispatcher dispatcher;

    public UserSession(User user, TelegramClient client, UserRepository userRepository, StateDispatcher dispatcher) {
        this.user = user;
        this.client = client;
        this.userRepository = userRepository;
        this.dispatcher = dispatcher;
    }

    public void saveUser() {
        userRepository.save(user);
    }

    public void setState(BotState state) {
        user.setState(state);
        saveUser();
    }

    public void goTo(BotState newState) {
        dispatcher.enterState(this, newState);
    }

    public void sendMessage(String text) {
        send(SendMessage.builder()
                .chatId(user.getTelegramId())
                .text(text)
                .build());
    }

    public void sendMessage(String text, ReplyKeyboard keyboard) {
        send(SendMessage.builder()
                .chatId(user.getTelegramId())
                .text(text)
                .replyMarkup(keyboard)
                .build());
    }

    private void send(SendMessage message) {
        try {
            client.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}