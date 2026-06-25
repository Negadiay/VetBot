package com.medvet.vetbot.bot;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class Paginator {

    private Paginator() {
    }

    public static <T> InlineKeyboardMarkup paginate(
            List<T> items,
            int page,
            int pageSize,
            int columns,
            String pagePrefix,
            Function<T, InlineKeyboardButton> buttonFactory,
            List<InlineKeyboardRow> extraRows) {

        int from = page * pageSize;
        int to = Math.min(from + pageSize, items.size());

        List<InlineKeyboardRow> rows = new ArrayList<>();
        InlineKeyboardRow currentRow = new InlineKeyboardRow();

        for (int i = from; i < to; i++) {
            currentRow.add(buttonFactory.apply(items.get(i)));
            if (currentRow.size() == columns) {
                rows.add(currentRow);
                currentRow = new InlineKeyboardRow();
            }
        }
        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
        }

        InlineKeyboardRow nav = new InlineKeyboardRow();
        if (page > 0) {
            nav.add(navButton("Назад", pagePrefix + (page - 1)));
        }
        if (to < items.size()) {
            nav.add(navButton("Далее", pagePrefix + (page + 1)));
        }
        if (!nav.isEmpty()) {
            rows.add(nav);
        }

        if (extraRows != null) {
            rows.addAll(extraRows);
        }

        return InlineKeyboardMarkup.builder().keyboard(rows).build();
    }

    private static InlineKeyboardButton navButton(String text, String callback) {
        return InlineKeyboardButton.builder().text(text).callbackData(callback).build();
    }
}