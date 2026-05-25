package co.edu.uniquindio.proptech.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.edu.uniquindio.proptech.EstructuraDatos.LinkedSimpleList.LinkedSimpleList;
import co.edu.uniquindio.proptech.controllers.PropTechController;
import co.edu.uniquindio.proptech.dto.ContextoClienteIA;
import co.edu.uniquindio.proptech.dto.MensajeChat;
import co.edu.uniquindio.proptech.model.Inmueble;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;

@Service
public class GeminiService {
    Inmobiliaria plataforma = PropTechController.getPlataforma();
    // Sigue llamándose igual internamente para no romper los @Autowired de tu
    // controlador
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

    @SuppressWarnings("unchecked")
    public String generarRespuesta(String mensaje, HttpSession session) {
        ContextoClienteIA contexto = obtenerContexto(session);

        actualizarContexto(
                mensaje,
                contexto);
        try {

            session.setAttribute(
                    "contextoIA",
                    contexto);
            String contextoBusqueda = buscarSegunContexto(contexto);

            RestTemplate restTemplate = new RestTemplate();

            String url = "https://api.groq.com/openai/v1/chat/completions";

            String modelo = "llama-3.3-70b-versatile";

            String contextoProptech = "Eres AURA, un asistente virtual inteligente para una plataforma inmobiliaria llamada AURA. "
                    + "Reglas: "
                    + " - Debes recordar durante toda la conversación la información que el usuario te proporcione. "
                    + " - No vuelvas a preguntar datos que ya fueron suministrados. "
                    + " - Ayuda a encontrar inmuebles, resolver dudas y agendar visitas."
                    + " - Responde siempre en español."
                    + " - Sé amable y profesional."
                    + " - Responde en menos de 50 palabras."
                    + " - Máximo 3 líneas."
                    + " - No des explicaciones largas."
                    + " - No repitas información."
                    + " - Si muestras inmuebles usa viñetas:"
                    + "    - muestra máximo 3 opciones"
                    + "     - El usuario busca inmueble."
                    + "     - NO inventes propiedades."
                    + "Redacta una respuesta corta."
                    + " - Nunca escribas párrafos largos."
                    + " - Nunca inventes inmuebles."
                    + " -Utiliza únicamente los inmuebles suministrados por el sistema."
                    + " - Si existen varios resultados, resume la información en formato de lista corta.";

            // ==========================
            // RECUPERAR HISTORIAL
            // ==========================

            List<MensajeChat> historial = (List<MensajeChat>) session.getAttribute("historial");

            if (historial == null) {
                historial = new ArrayList<>();

                historial.add(
                        new MensajeChat(
                                "system",
                                contextoProptech));

                session.setAttribute(
                        "historial",
                        historial);
            }

            // ==========================
            // GUARDAR MENSAJE USUARIO
            // ==========================

            historial.add(
                    new MensajeChat(
                            "user",
                            mensaje));

            // ==========================
            // ARMAR PETICIÓN
            // ==========================

            Map<String, Object> requestBody = new HashMap<>();

            requestBody.put("model", modelo);

            if (!contextoBusqueda.isBlank()) {
                return """
                        Encontré estas opciones disponibles:

                        %s

                        ¿Deseas más información o agendar una visita?
                        """.formatted(contextoBusqueda);
            }
            requestBody.put(
                    "messages",
                    historial);

            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(
                    MediaType.APPLICATION_JSON);

            headers.set(
                    "Authorization",
                    "Bearer " + apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(
                    requestBody,
                    headers);

            String response = restTemplate.postForObject(
                    url,
                    entity,
                    String.class);

            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(response);

            JsonNode choices = root.path("choices");

            if (choices.isArray()
                    && choices.size() > 0) {

                String respuestaTexto = choices.get(0)
                        .path("message")
                        .path("content")
                        .asText();

                // ==========================
                // GUARDAR RESPUESTA IA
                // ==========================

                historial.add(
                        new MensajeChat(
                                "assistant",
                                respuestaTexto));

                return respuestaTexto;
            }

            return "No pude generar una respuesta.";

        } catch (Exception e) {

            e.printStackTrace();

            return "Error al comunicarse con la IA.";
        }
    }

    private void actualizarContexto(
            String mensaje,
            ContextoClienteIA contexto) {

        String texto = mensaje.toLowerCase();

        if (texto.contains("armenia")) {
            contexto.setCiudad("ARMENIA");
        }

        if (texto.contains("calarcá")) {
            contexto.setCiudad("CALARCÁ");
        }

        if (texto.contains("casa")) {
            contexto.setTipoInmueble("CASA");
        }

        if (texto.contains("apartamento")) {
            contexto.setTipoInmueble("APARTAMENTO");
        }

        if (texto.contains("oficina")) {
            contexto.setTipoInmueble("OFICINA");
        }

        if (texto.contains("bodega")) {
            contexto.setTipoInmueble("BODEGA");
        }

        if (texto.contains("venta")) {
            contexto.setFinalidad("Venta");
        }

        if (texto.contains("arriendo")) {
            contexto.setFinalidad("Arriendo");
        }

        if (texto.contains("norte")) {
            contexto.setZona("NORTE");
        }

        if (texto.contains("sur")) {
            contexto.setZona("SUR");
        }

        if (texto.contains("centro")) {
            contexto.setZona("CENTRO");
        }

        extraerPresupuesto(texto, contexto);

        extraerHabitaciones(texto, contexto);
    }

    private void extraerPresupuesto(
            String texto,
            ContextoClienteIA contexto) {

        Pattern p = Pattern.compile("(\\d{6,12})");

        Matcher m = p.matcher(texto);

        if (m.find()) {

            try {

                double presupuesto = Double.parseDouble(m.group(1));

                contexto.setPresupuesto(presupuesto);

            } catch (Exception e) {
            }
        }
    }

    private void extraerHabitaciones(
            String texto,
            ContextoClienteIA contexto) {

        Pattern p = Pattern.compile("(\\d+)\\s*habit");

        Matcher m = p.matcher(texto);

        if (m.find()) {

            contexto.setHabitaciones(
                    Integer.parseInt(
                            m.group(1)));
        }
    }

    private String buscarSegunContexto(
            ContextoClienteIA contexto) {

        if (contexto.getCiudad() == null) {
            return "";
        }

        LinkedSimpleList<Inmueble> inmueblesCiudad = plataforma.obtenerInmueblesPorCiudad(
                contexto.getCiudad());

        return construirResumenFiltrado(
                inmueblesCiudad,
                contexto);
    }

    private String construirResumenFiltrado(
            LinkedSimpleList<Inmueble> lista,
            ContextoClienteIA contexto) {

        StringBuilder sb = new StringBuilder();

        int contador = 0;

        for (Inmueble inm : lista) {

            if (contexto.getZona() != null) {

                if (!inm.getBarrioZona().equalsIgnoreCase(
                        contexto.getZona())) {

                    continue;
                }
            }

            if (contexto.getTipoInmueble() != null) {

                if (!inm.getClass()
                        .getSimpleName()
                        .equalsIgnoreCase(
                                contexto.getTipoInmueble())) {

                    continue;
                }
            }

            if (contexto.getFinalidad() != null) {

                if (!inm.getFinalidad()
                        .equalsIgnoreCase(
                                contexto.getFinalidad())) {

                    continue;
                }
            }

            sb.append("• ")
                    .append(inm.getCodigo())
                    .append(" | ")
                    .append(inm.getClass().getSimpleName())
                    .append(" | ")
                    .append(inm.getBarrioZona())
                    .append(" | $")
                    .append(String.format("%,.0f", inm.getPrecio()))
                    .append(" | ")
                    .append(inm.getHabitaciones())
                    .append(" hab");

            contador++;

            if (contador == 3) {
                break;
            }
        }

        return sb.toString();
    }

    private ContextoClienteIA obtenerContexto(HttpSession session) {

        ContextoClienteIA contexto = (ContextoClienteIA) session.getAttribute("contextoIA");

        if (contexto == null) {
            contexto = new ContextoClienteIA();
            session.setAttribute("contextoIA", contexto);
        }

        return contexto;
    }

    public void limpiarConversacion(
            HttpSession session) {

        session.removeAttribute(
                "historial");
    }
}