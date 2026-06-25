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
    @Getter
    private final UserRepository userRepository;

    public UserSession(User user, TelegramClient client, UserRepository userRepository) {
        this.user = user;
        this.client = client;
        this.userRepository = userRepository;
    }

    private void send(SendMessage message) {
        try {
            client.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String text) {
        send(SendMessage.builder()
                .chatId(user.getTelegramId())
                .text(text)
                .build()
        );
    }

    public void sendMessage(String text, ReplyKeyboard keyboard) {
        send(SendMessage.builder()
                .chatId(user.getTelegramId())
                .text(text)
                .replyMarkup(keyboard)
                .build());
    }

    public void setState(BotState state) {
        user.setState(state);
        saveUser();
    }

    public void saveUser() {
        userRepository.save(user);
    }

}
