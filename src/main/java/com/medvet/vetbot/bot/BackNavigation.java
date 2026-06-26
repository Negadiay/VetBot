package com.medvet.vetbot.bot;

import com.medvet.vetbot.domain.BotState;

import java.util.Map;

public final class BackNavigation {

    public static final String BACK_CALLBACK = "nav:back";

    private static final Map<BotState, BotState> PREVIOUS = Map.of(
            BotState.BOOKING_CHOOSING_SERVICE, BotState.MENU,
            BotState.BOOKING_CHOOSING_DATE, BotState.BOOKING_CHOOSING_SERVICE,
            BotState.BOOKING_CHOOSING_TIME, BotState.BOOKING_CHOOSING_DATE,
            BotState.BOOKING_WAITING_PET_NAME, BotState.BOOKING_CHOOSING_TIME,
            BotState.BOOKING_CHOOSING_PET_TYPE, BotState.BOOKING_WAITING_PET_NAME
    );

    private BackNavigation() {
    }

    public static BotState previousOf(BotState state) {
        return PREVIOUS.get(state);
    }

}