package co.edu.uniquindio.proptech;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
public class GeminiService {

    // Sigue llamándose igual internamente para no romper los @Autowired de tu controlador
    @Value("${groq.api.key}")
    private String apiKey;

    @PostConstruct
    public void init() {
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("ADVERTENCIA: API key de Groq no configurada en application.properties");
        } else {
            System.out.println("API key de Groq cargada correctamente para el servicio de IA");
        }
    }

    public String generarRespuesta(String mensaje) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            // Endpoint oficial de Groq
            String url = "https://api.groq.com/openai/v1/chat/completions";

            // Modelo potente, inteligente y gratis de Groq ideal para asistentes virtuales
            String modelo = "llama-3.3-70b-versatile"; 

            String contextoProptech = "Eres AURA, un asistente virtual inteligente para una plataforma inmobiliaria (PropTech) llamada AURA Hub. "
                    + "Tu objetivo es ayudar a clientes a encontrar propiedades, agendar visitas y resolver dudas sobre bienes raíces. "
                    + "Sé amable, profesional y empático. Responde en español de forma fluida. Mantén respuestas concisas (máximo 3-4 líneas).";

            // Construcción del payload JSON para la API de Groq
            Map<String, Object> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", contextoProptech);

            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", mensaje);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", modelo);
            requestBody.put("messages", List.of(systemMessage, userMessage));

            // Configuración de cabeceras estándar (Bearer Token)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            System.out.println("Enviando consulta al motor Groq (" + modelo + ")...");
            String response = restTemplate.postForObject(url, entity, String.class);
            System.out.println("Respuesta cruda de Groq recibida con éxito");

            // Mapeo del JSON de respuesta (choices[0].message.content)
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            if (root.has("error")) {
                JsonNode error = root.path("error");
                String errorMsg = error.path("message").asText("Error desconocido");
                System.err.println("Error de Groq: " + errorMsg);
                return "Error de IA corporativa: " + errorMsg;
            }

            JsonNode choices = root.path("choices");
            if (choices.isArray() && choices.size() > 0) {
                String respuestaTexto = choices.get(0).path("message").path("content").asText();
                if (respuestaTexto != null && !respuestaTexto.trim().isEmpty()) {
                    return respuestaTexto;
                }
            }

            return "Lo siento, no pude procesar tu solicitud en este momento. Por favor, contacta con nuestro equipo de asesores.";
        } catch (RestClientException e) {
            System.err.println("Error REST al conectar con Groq: " + e.getMessage());
            e.printStackTrace();
            return "Error de comunicación con el servidor de la IA. Por favor intenta de nuevo.";
        } catch (Exception e) {
            System.err.println("Error general en el motor de IA: " + e.getMessage());
            e.printStackTrace();
            return "Ocurrió un error inesperado al procesar la respuesta de la IA.";
        }
    }
}