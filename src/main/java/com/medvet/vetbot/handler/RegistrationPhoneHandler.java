package com.medvet.vetbot.handler;

import com.medvet.vetbot.bot.Keyboards;
import com.medvet.vetbot.bot.UserSession;
import com.medvet.vetbot.domain.BotState;
import com.medvet.vetbot.domain.Role;
import com.medvet.vetbot.domain.User;
import com.medvet.vetbot.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RegistrationPhoneHandler implements StateHandler {

    private final UserRepository userRepository;
    private final MenuHandler menuHandler;

    public RegistrationPhoneHandler(UserRepository userRepository, MenuHandler menuHandler) {
        this.userRepository = userRepository;
        this.menuHandler = menuHandler;
    }

    @Override
    public BotState state() {
        return BotState.REG_WAITING_PHONE;
    }

    @Override
    public void handleText(UserSession session, String text) {
        String phone = normalizePhone(text);

        User user = session.getUser();
        user.setPhone(phone);

        Role role = resolveRole(phone, user);
        user.setRole(role);
        user.setState(BotState.MENU);
        session.saveUser();

        session.sendMessage("Добро пожаловать! Теперь вам доступны запись на приём, история и информация о клинике.", Keyboards.menuButton());
        menuHandler.showMenu(session);

    }

    private String normalizePhone(String phone) {
        String digits = phone.replaceAll("[^0-9]", "");
        return "+" + digits;
    }

    private Role resolveRole(String phone, User currentUser) {
        Optional<User> existing = userRepository.findByPhone(phone);
        if (existing.isPresent() && existing.get().getRole() == Role.EMPLOYEE && !existing.get().getId().equals(currentUser.getId())) {
            User employeeRecord = existing.get();
            employeeRecord.setTelegramId(currentUser.getTelegramId());
            employeeRecord.setFullName(currentUser.getFullName());
            employeeRecord.setState(BotState.MENU);
            userRepository.save(employeeRecord);
            return Role.EMPLOYEE;
        }
        return Role.CLIENT;
    }
}