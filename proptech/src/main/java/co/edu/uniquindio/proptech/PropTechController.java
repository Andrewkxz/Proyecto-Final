package co.edu.uniquindio.proptech;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import co.edu.uniquindio.proptech.BinarySearchTree.Node;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class PropTechController {

    // Instanciamos el cerebro del sistema
    private static Inmobiliaria plataforma = new Inmobiliaria();
    private static boolean datosInicializados = false;

    public PropTechController() {
        // Cargamos datos de prueba la primera vez que arranca el servidor
        if (!datosInicializados) {
            cargarDatosPrueba();
            datosInicializados = true;
        }
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        // Pasamos métricas generales a la vista
        model.addAttribute("totalInmuebles", plataforma.getArbolInmueblesPorPrecio().getSize());
        model.addAttribute("totalClientes", plataforma.getClientes().getSize());
        String ultimaAccion = plataforma.obtenerUltimaAccion();
        model.addAttribute("ultimaAccion", ultimaAccion != null ? ultimaAccion : "No hay acciones recientes.");
        model.addAttribute("hayHistorial", ultimaAccion != null);
        
        // Generamos el JSON del Árbol de Inmuebles para tu script de GoJS
        String treeJson = "[]";
        if (!plataforma.getArbolInmueblesPorPrecio().isEmpty()) {
            treeJson = generateGraphData(plataforma.getArbolInmueblesPorPrecio().root);
        }
        model.addAttribute("treeJsonData", treeJson);

        return "dashboard";
    }

    @PostMapping("/registrar-cliente")
    public String registrarCliente(
        @RequestParam String identificacion, @RequestParam String nombre,
        @RequestParam String correo, @RequestParam String telefono,
        @RequestParam String tipoCliente, @RequestParam double presupuesto,
        @RequestParam String tipoInmuebleDeseado, @RequestParam int minHabitaciones,
        RedirectAttributes redirectAttrs) {

        // Creamos el cliente con los datos del formulario
        Cliente nuevoCliente = new Cliente(identificacion, nombre, correo, telefono, 
                                           tipoCliente, presupuesto, tipoInmuebleDeseado, minHabitaciones);
        
        // Lo guardamos en las estructuras (Hash y Lista)
        plataforma.registrarCliente(nuevoCliente);

        // Mandamos un mensaje de éxito a la vista
        redirectAttrs.addFlashAttribute("mensajeExito", "Cliente " + nombre + " registrado exitosamente.");
        return "redirect:/";
    }

    @PostMapping("/registrar-inmueble")
    public String registrarInmueble(
        @RequestParam String codigo, @RequestParam String direccion,
        @RequestParam String ciudad, @RequestParam String barrioZona,
        @RequestParam String finalidad, @RequestParam double precio,
        @RequestParam double area, @RequestParam int habitaciones,
        @RequestParam int banos, @RequestParam String estado,
        RedirectAttributes redirectAttrs) {

        // Para simplificar, asignamos un asesor por defecto (el primero de la lista si existe, o uno temporal)
        // En una app real, aquí tendrías un <select> con los asesores disponibles
        Asesor asesorAsignado = new Asesor("A-TEMP", "Asesor Base", "000", "General"); 
        
        // Creamos el Inmueble (Para este ejemplo usaremos la clase Apartamento)
        Apartamento nuevoInmueble = new Apartamento(codigo, direccion, ciudad, barrioZona, 
                                                    finalidad, precio, area, habitaciones, 
                                                    banos, estado, true, asesorAsignado, true, 0.0);
        
        // Se inserta en la Lista Doble, en el Hash y MÁGICAMENTE en el Árbol Binario
        plataforma.registrarInmueble(nuevoInmueble);

        redirectAttrs.addFlashAttribute("mensajeExito", "Inmueble " + codigo + " registrado. ¡Revisa el Árbol!");
        return "redirect:/";
    }

    @PostMapping("/buscar-inmuebles")
    public String buscarInmuebles(
            @RequestParam(defaultValue = "0") double precioMin,
            @RequestParam(defaultValue = "1000000000") double precioMax,
            @RequestParam String zona,
            @RequestParam(defaultValue = "0") int minHabitaciones,
            RedirectAttributes redirectAttrs) {

        // Llamamos a tu método que usa el Árbol Binario + Filtros Secuenciales
        co.edu.uniquindio.proptech.LinkedSimpleList.LinkedSimpleList<Inmueble> resultadosPropios = 
            plataforma.buscarInmuebleConFiltros(precioMin, precioMax, zona, minHabitaciones);

        List<String> listaResultados = new ArrayList<>();
        for (int i = 0; i < resultadosPropios.getSize(); i++) {
            Inmueble inm = resultadosPropios.getData(i);
            listaResultados.add("🏠 " + inm.getCodigo() + " en " + inm.getBarrioZona() + 
                               " | " + inm.getHabitaciones() + " Hab. | Precio: $" + 
                               String.format("%,.0f", inm.getPrecio()));
        }

        if (listaResultados.isEmpty()) {
            redirectAttrs.addFlashAttribute("mensajeInfo", "No se encontraron inmuebles que coincidan con esos filtros.");
        } else {
            redirectAttrs.addFlashAttribute("listaResultadosBusqueda", listaResultados);
            redirectAttrs.addFlashAttribute("mostrarModalBusqueda", true);
        }

        return "redirect:/";
    }

    @PostMapping("/generar-recomendaciones")
    public String generarRecomendaciones(@RequestParam String idCliente, RedirectAttributes redirectAttrs) {
        
        Cliente cliente = plataforma.buscarClientePorId(idCliente);
        
        if (cliente == null) {
            redirectAttrs.addFlashAttribute("mensajeError", "Error: No se encontró ningún cliente con la identificación '" + idCliente + "'.");
            return "redirect:/";
        }

        // Llamamos al motor inteligente que cruza Grafo + Árbol
        co.edu.uniquindio.proptech.LinkedSimpleList.LinkedSimpleList<Inmueble> recos = plataforma.generarRecomendaciones(idCliente);

        if (recos.getSize() == 0) {
            redirectAttrs.addFlashAttribute("mensajeInfo", "No hay recomendaciones disponibles que se ajusten a " + cliente.getNombre() + " en este momento.");
        } else {
            // Convertimos los resultados a Strings formateados para la vista
            List<String> listaSugerencias = new ArrayList<>();
            for (int i = 0; i < recos.getSize(); i++) {
                Inmueble inm = recos.getData(i);
                listaSugerencias.add("📌 " + inm.getCodigo() + " - " + inm.getBarrioZona() + " | Precio: $" + String.format("%,.0f", inm.getPrecio()));
            }
            
            // Pasamos los datos a la vista
            redirectAttrs.addFlashAttribute("clienteReco", cliente.getNombre());
            redirectAttrs.addFlashAttribute("listaSugerencias", listaSugerencias);
            redirectAttrs.addFlashAttribute("mostrarModalRecos", true); // Bandera para abrir el modal automáticamente
        }

        return "redirect:/";
    }

    @PostMapping("/generar-reportes")
    public String generarReportes(RedirectAttributes redirectAttrs) {
        
        // 1. Obtener Ranking de Asesores (Usa tu Árbol Binario de Búsqueda internamente)
        co.edu.uniquindio.proptech.LinkedSimpleList.LinkedSimpleList<Asesor> rankingAsesores = plataforma.generarRankingAsesores();
        List<String> listaAsesores = new ArrayList<>();
        for (int i = 0; i < rankingAsesores.getSize(); i++) {
            Asesor a = rankingAsesores.getData(i);
            listaAsesores.add("🏆 " + a.getNombre() + " | Ventas/Cierres: " + a.getCargaTrabajoActiva());
        }

        // 2. Obtener Actividad por Zonas
        co.edu.uniquindio.proptech.LinkedSimpleList.LinkedSimpleList<String> actividadZonas = plataforma.obtenerResumenZonas();
        List<String> listaZonas = new ArrayList<>();
        for (int i = 0; i < actividadZonas.getSize(); i++) {
            listaZonas.add(actividadZonas.getData(i));
        }

        // 3. Pasar datos a la vista
        redirectAttrs.addFlashAttribute("listaRankingAsesores", listaAsesores);
        redirectAttrs.addFlashAttribute("listaActividadZonas", listaZonas);
        redirectAttrs.addFlashAttribute("mostrarModalReportes", true);

        return "redirect:/";
    }

    @PostMapping("/analizar-comportamiento")
    public String analizarComportamiento(RedirectAttributes redirectAttrs) {
        
        // 1. Ejecutamos tu motor de detección (esto llena la Bicola)
        plataforma.detectarComportamientosInusuales();
        
        // 2. Extraemos las alertas formateadas
        co.edu.uniquindio.proptech.LinkedSimpleList.LinkedSimpleList<String> alertasPropias = plataforma.extraerAlertas();
        
        // 3. Convertimos a java.util.List para Thymeleaf
        List<String> alertasParaHTML = new ArrayList<>();
        for (int i = 0; i < alertasPropias.getSize(); i++) {
            alertasParaHTML.add(alertasPropias.getData(i));
        }

        // 4. Enviamos a la vista
        if (alertasParaHTML.isEmpty()) {
            redirectAttrs.addFlashAttribute("mensajeInfo", "Análisis completado: No se detectaron anomalías en el sistema.");
        } else {
            redirectAttrs.addFlashAttribute("mensajeError", "¡Atención! Se detectaron comportamientos inusuales.");
            redirectAttrs.addFlashAttribute("listaAlertas", alertasParaHTML);
        }

        return "redirect:/";
    }

    @PostMapping("/deshacer-accion")
    public String deshacerAccion(RedirectAttributes redirectAttrs) {
        String accionDeshecha = plataforma.extraerUltimoCambio();
        
        if (accionDeshecha != null) {
            // Nota: Aquí extraemos el registro de la pila. Para un "deshacer" 100% real 
            // habría que ir al Árbol/Lista y borrar el objeto, pero para la demostración
            // de la Pila, sacarlo del historial y notificar es perfecto.
            redirectAttrs.addFlashAttribute("mensajeExito", "Se ha deshecho del historial: " + accionDeshecha);
        } else {
            redirectAttrs.addFlashAttribute("mensajeInfo", "La pila de historial está vacía.");
        }
        
        return "redirect:/";
    }

    // --- ADAPTACIÓN DE TU MÉTODO PARA LEER INMUEBLES ---
    private String generateGraphData(Node<Inmueble> root) {
        List<Map<String, Object>> nodeList = new ArrayList<>();
        populateJsonModel(root, nodeList, null, null);

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(nodeList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "[]";
        }
    }

    private void populateJsonModel(Node<Inmueble> node, List<Map<String, Object>> list, String parentKey, String direction) {
        if (node == null) return;

        Map<String, Object> nodeMap = new HashMap<>();
        
        // Usamos el código del inmueble como Key único
        String currentKey = node.getData().getCodigo(); 
        // Mostramos el Código y el Precio en el círculo del árbol
        String displayText = node.getData().getCodigo() + "\n$" + String.format("%,.0f", node.getData().getPrecio());
        
        nodeMap.put("key", currentKey);
        nodeMap.put("text", displayText);
        
        if (parentKey != null) {
            nodeMap.put("parent", parentKey);
            nodeMap.put("dir", direction);
        }
        
        list.add(nodeMap);
        
        populateJsonModel(node.getLeft(), list, currentKey, "L");
        populateJsonModel(node.getRight(), list, currentKey, "R");
    }

    private void cargarDatosPrueba() {
        Asesor asesor1 = new Asesor("A-101", "Juli", "111", "Norte");
        Asesor asesor2 = new Asesor("A-102", "Juan", "222", "Centro");

        plataforma.registrarAsesor(asesor1);
        plataforma.registrarAsesor(asesor2);


        
        Apartamento apt1 = new Apartamento("APT-001", "Calle 10N", "Armenia", "Norte", "Venta", 250000000.0, 65.0, 3, 2, "Nuevo", true, asesor1, true, 200000.0);
        Apartamento apt2 = new Apartamento("APT-002", "Av Centenario", "Armenia", "Norte", "Arriendo", 1500000.0, 50.0, 2, 1, "Usado", true, asesor1, false, 150000.0);
        LocalComercial loc1 = new LocalComercial("LOC-001", "Carrera 14", "Armenia", "Centro", "Venta", 500000000.0, 120.0, 1, 2, "Remodelado", true, asesor2, true, "Comercial Mixto");

        plataforma.registrarInmueble(apt1);
        plataforma.registrarInmueble(apt2);
        plataforma.registrarInmueble(loc1);

        Cliente cliente1 = new Cliente("C-001", "Andrés", "andres@gmail.com", "333", "Comprador", 300000000.0, "Apartamento", 2);
        Cliente cliente2 = new Cliente("C-002", "Nathaly", "nat@gmail.com", "444", "Inversionista", 600000000.0, "LocalComercial", 0);

        plataforma.registrarCliente(cliente1);
        plataforma.registrarCliente(cliente2);

        cliente1.getHistorialConsultas().addLast(apt1);
        plataforma.conectarClientesConInmuebles(cliente1.getId(), apt1.getCodigo());

        for(int i = 0; i < 12; i++){
            Visita v = new Visita(cliente2, loc1, LocalDate.now(), "10:00 AM", asesor2, 1);
            loc1.registrarVisita(v);
        }
    }
}