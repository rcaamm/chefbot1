package com.chefbot.chefbot;

import com.chefbot.chefbot.service.GeminiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class ChefBotController extends TelegramLongPollingBot {

    private final String botUsername;
    private final String botToken;
    private final GeminiService geminiService;

    public ChefBotController(
            @Value("${telegram.bot.username}") String botUsername,
            @Value("${telegram.bot.token}") String botToken,
            GeminiService geminiService) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.geminiService = geminiService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            String responseText;

            if (messageText.startsWith("/receta")) {
                responseText = "¡Perfecto! Dime qué ingredientes tienes o qué tipo de receta deseas, y la prepararé. Por ejemplo: Tacos al pastor.";
            }
            else {
                responseText = geminiService.generateRecipe(messageText);
            }

            sendMessage(chatId, responseText);
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}