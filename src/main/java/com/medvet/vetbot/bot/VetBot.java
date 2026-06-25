package com.medvet.vetbot.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Component
public class VetBot implements SpringLongPollingBot, LongPollingUpdateConsumer {

    private final TelegramClient client;
    private final String token;
    private final StateDispatcher dispatcher;

    public VetBot(@Value("${bot.token}") String token, StateDispatcher dispatcher) {
        this.token = token;
        this.client = new OkHttpTelegramClient(token);
        this.dispatcher = dispatcher;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(List<Update> updates) {
        for (Update update : updates) {
            dispatcher.dispatch(update, client);
        }
    }
}