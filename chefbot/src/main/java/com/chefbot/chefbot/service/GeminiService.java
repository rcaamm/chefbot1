package com.chefbot.chefbot.service;

import com.google.genai.Client;
import com.google.genai.errors.ApiException;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GeminiService {

    private final String MODEL_NAME = "gemini-1.5-flash";
    private final Client geminiClient;

    // Instrucción de contexto para el modelo
    private static final String SYSTEM_INSTRUCTION =
            "Eres un ChefBot experto en recetas. Genera una receta detallada, con ingredientes, instrucciones y tiempo de preparación, basada en la siguiente solicitud del usuario. Usa formato Markdown para que sea fácil de leer.";

    public GeminiService(@Value("${chefbot.gemini.key}") String geminiApiKey) {
        this.geminiClient = Client.builder()
                .apiKey(geminiApiKey)
                .build();
    }

    public String generateRecipe(String prompt) {
        try {
            // SOLUCIÓN AL ERROR DE COMPILACIÓN:
            // Concatenamos el contexto (SYSTEM_INSTRUCTION) y la solicitud del usuario.
            String fullPrompt = SYSTEM_INSTRUCTION + "\n\nSolicitud de receta: " + prompt;

            Content userContent = Content.builder()
                    .role("user")
                    .parts(Part.builder().text(fullPrompt).build())
                    .build();

            List<Content> contents = List.of(userContent);

            // Configuraciones de generación (se elimina la línea .systemInstruction)
            GenerateContentConfig config = GenerateContentConfig.builder()
                    .temperature(0.7f)
                    .maxOutputTokens(2048)
                    .build();

            GenerateContentResponse response = geminiClient.models.generateContent(
                    MODEL_NAME,
                    contents,
                    config
            );

            return response.text();

        } catch (Exception e) {
            System.err.println("Error al generar receta: " + e.getMessage());
            e.printStackTrace();

            // Mensaje de error útil para enviar a Telegram
            return "❌ Error al generar la receta. La clave de API es el problema: " + e.getMessage();
        }
    }
}