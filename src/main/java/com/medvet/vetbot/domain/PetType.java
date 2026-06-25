package com.medvet.vetbot.domain;

import lombok.Getter;

@Getter
public enum PetType {
    CAT("Кошки"),
    DOG("Собаки"),
    RODENT("Грызуны"),
    BIRD("Птицы"),
    OTHER("Другое");

    private final String label;

    PetType(String label) {
        this.label = label;
    }

}


