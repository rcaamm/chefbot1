package com.chefbot.chefbot.service;

import com.google.genai.Client;
import com.google.genai.errors.ApiException;
import com.google.genai.types.Content;
import com.google.genai.types.GenerateContentParameters;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.Part;
// Importación final y correcta: Role está directamente en com.google.genai.types
import com.google.genai.types.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GeminiService {

    private final String MODEL_NAME = "gemini-2.5-flash";
    private final Client geminiClient;

    private final String systemInstructionText = "Eres un ChefBot experto en recetas. Genera una receta detallada, con ingredientes, instrucciones y tiempo de preparación, basada en la siguiente solicitud del usuario. Usa formato Markdown para que sea fácil de leer.";


    public GeminiService(@Value("${chefbot.gemini.key}") String geminiApiKey) {
        this.geminiClient = Client.builder()
                .apiKey(geminiApiKey)
                .build();
    }

    /**
     * Método para generar una receta a partir de un prompt.
     */
    public String generateRecipe(String prompt) {
        try {
            // 1. Definir la Instrucción del Sistema como un objeto Content con rol SYSTEM
            // Esta es la manera compatible de pasar las instrucciones en esta versión de la librería.
            Content systemContent = Content.builder()
                    .role(Role.SYSTEM)
                    .parts(Part.builder().text(systemInstructionText).build())
                    .build();

            // 2. Definir el Mensaje del Usuario como un objeto Content con rol USER
            Content userContent = Content.builder()
                    .role(Role.USER)
                    .parts(Part.builder().text(prompt).build())
                    .build();

            // 3. Crear la lista de contenidos que se envía al modelo (Instrucción + Prompt)
            List<Content> contents = List.of(systemContent, userContent);

            // 4. Llamada al modelo con la firma correcta (modelName, contents, parameters)
            GenerateContentResponse response = geminiClient.models
                    .generateContent(
                            MODEL_NAME,
                            contents,
                            // Se pasa un objeto GenerateContentParameters vacío para cumplir la firma
                            GenerateContentParameters.builder().build()
                    );

            return response.text();

        } catch (ApiException e) {
            System.err.println("Error de API al generar contenido: " + e.getMessage());
            e.printStackTrace();
            return "❌ Lo siento, la API de Gemini devolvió un error. Revisa tu clave y el estado del servicio.";
        } catch (Exception e) {
            System.err.println("Error general al generar contenido: " + e.getMessage());
            e.printStackTrace();
            return "⚠️ Lo siento, ocurrió un error interno al consultar la receta.";
        }
    }
}