package com.chefbot.chefbot.service; // ¡CAMBIO DE PAQUETE A 'service'!

import com.chefbot.chefbot.service.GeminiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

// Renombramos la clase de ChefBotController a TelegramBotService
@Component
public class TelegramBotService extends TelegramLongPollingBot {

    private final String botUsername;
    private final String botToken;
    private final GeminiService geminiService;

    // Inyección de dependencias y valores de application.properties
    public TelegramBotService(
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

            // Manejo del comando /receta o cualquier otro texto
            if (messageText.startsWith("/start")) {
                responseText = "¡Bienvenido a ChefBot! Soy tu asistente de recetas. Dime qué ingredientes tienes o qué tipo de receta deseas, y la prepararé. Por ejemplo: Tacos al pastor.";
            }
            else {
                // Si el mensaje no es un comando conocido, lo enviamos a Gemini
                responseText = geminiService.generateRecipe(messageText);
            }

            sendMessage(chatId, responseText);
        }
    }

    private void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        // Habilitamos Markdown para que la respuesta de Gemini se vea bien
        message.setParseMode("Markdown");

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