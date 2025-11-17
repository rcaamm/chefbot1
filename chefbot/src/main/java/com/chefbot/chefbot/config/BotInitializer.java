package com.chefbot.chefbot.config;

import com.chefbot.chefbot.service.TelegramBotService; // ¡IMPORT ACTUALIZADO!
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class BotInitializer {

    private final TelegramBotService botService;

    // Spring inyectará automáticamente la instancia de TelegramBotService
    public BotInitializer(TelegramBotService botService) {
        this.botService = botService;
    }

    /**
     * Este método se ejecuta automáticamente cuando el contexto de Spring ha finalizado su carga.
     * Es el punto de inicio para la conexión real con la API de Telegram.
     */
    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        try {
            // Crea la instancia de la API de TelegramBots
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

            // Registra tu bot: ESTO ES LO QUE HACE QUE EMPIECE A ESCUCHAR.
            telegramBotsApi.registerBot(botService);
            System.out.println("✅ Telegram Bot registrado exitosamente: " + botService.getBotUsername());
        } catch (TelegramApiException e) {
            System.err.println("❌ ERROR FATAL al registrar el bot de Telegram. Verifica tu token en application.properties.");
            e.printStackTrace();
            throw e;
        }
    }
}