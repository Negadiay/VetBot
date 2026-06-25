package com.medvet.vetbot.config;

import com.medvet.vetbot.domain.Role;
import com.medvet.vetbot.domain.Service;
import com.medvet.vetbot.domain.User;
import com.medvet.vetbot.repository.ServiceRepository;
import com.medvet.vetbot.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

// TODO: потом убрать

@Component
public class DataSeeder implements CommandLineRunner {

    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;

    public DataSeeder(ServiceRepository serviceRepository, UserRepository userRepository) {
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        seedServices();
        seedEmployee();
    }

    private void seedServices() {
        if (serviceRepository.count() > 0) {
            return;
        }

        Service chipping = new Service();
        chipping.setName("Чипирование");
        chipping.setRoom("221");
        chipping.setPrice(new BigDecimal("45.00"));

        Service ultrasound = new Service();
        ultrasound.setName("УЗИ");
        ultrasound.setRoom("105");
        ultrasound.setPrice(new BigDecimal("60.00"));

        Service checkup = new Service();
        checkup.setName("Осмотр");
        checkup.setRoom("110");
        checkup.setPrice(new BigDecimal("30.00"));

        serviceRepository.saveAll(List.of(chipping, ultrasound, checkup));
    }

    private void seedEmployee() {
        String employeePhone = "+375295831084";

        if (userRepository.findByPhone(employeePhone).isPresent()) {
            return;
        }

        User employee = new User();
        employee.setTelegramId(-1L);
        employee.setFullName("Бипкин Анатолий Ефимович");
        employee.setPhone(employeePhone);
        employee.setRole(Role.EMPLOYEE);

        userRepository.save(employee);
    }
}