package com.medvet.vetbot.bot;

import com.medvet.vetbot.handler.MenuHandler;
import com.medvet.vetbot.handler.StateHandler;
import com.medvet.vetbot.domain.BotState;
import com.medvet.vetbot.domain.Role;
import com.medvet.vetbot.domain.User;
import com.medvet.vetbot.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class StateDispatcher {

    private final UserRepository userRepository;
    private final Map<BotState, StateHandler> handlers;
    private final MenuHandler menuHandler;

    public StateDispatcher(UserRepository userRepository, List<StateHandler> handlerList, MenuHandler menuHandler) {
        this.userRepository = userRepository;
        this.menuHandler = menuHandler;
        this.handlers = handlerList.stream().collect(Collectors.toMap(StateHandler::state, Function.identity()));
    }

    public void dispatch(Update update, TelegramClient client) {
        Long telegramId = extractTelegramId(update);
        if (telegramId == null) {
            return;
        }

        User user = userRepository.findByTelegramId(telegramId)
                .orElseGet(() -> createNewUser(telegramId));

        // TODO: потом убрать
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            if (text.equals("/employee") || text.equals("/client")) {
                handleTestCommand(text, user, client);
                return;
            }
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            if (text.equals(Keyboards.MENU_BUTTON_TEXT)) {
                UserSession session = new UserSession(user, client, userRepository);
                user.setState(BotState.MENU);
                userRepository.save(user);
                menuHandler.showMenu(session);
                return;
            }
        }

        StateHandler handler = handlers.get(user.getState());
        if (handler == null) {
            return;
        }

        UserSession session = new UserSession(user, client, userRepository);

        if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            handler.handleCallback(session, data);
        }
        else if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            handler.handleText(session, text);
        }
        else if (update.hasMessage() && update.getMessage().hasContact()) {
            String phone = update.getMessage().getContact().getPhoneNumber();
            handler.handleText(session, phone);
        }
    }

    private Long extractTelegramId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        }
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        }
        return null;
    }

    private User createNewUser(Long telegramId) {
        User user = new User();
        user.setTelegramId(telegramId);
        user.setRole(Role.CLIENT);
        user.setState(BotState.NEW);
        return userRepository.save(user);
    }

    // TODO: потом убрать
    private void handleTestCommand(String command, User user, TelegramClient client) {
        Role role = command.equals("/employee") ? Role.EMPLOYEE : Role.CLIENT;
        user.setRole(role);
        user.setState(BotState.MENU);
        user.setFullName(user.getFullName() != null ? user.getFullName() : "Тест Тестов Тестович");
        user.setPhone(user.getPhone() != null ? user.getPhone() : "+375000000000");
        userRepository.save(user);

        UserSession session = new UserSession(user, client, userRepository);
        session.sendMessage("Тестовый вход выполнен. Роль: " + role + ". Состояние: MENU.");
    }
}