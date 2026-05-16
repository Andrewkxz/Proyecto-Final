package co.edu.uniquindio.proptech;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import co.edu.uniquindio.proptech.BinarySearchTree.Node;
import co.edu.uniquindio.proptech.LinkedSimpleList.LinkedSimpleList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Controlador principal de la aplicación Spring Boot.
 * Actúa como puente entre las vistas HTML (Thymeleaf) y la clase Inmobiliaria (Lógica y Estructuras de Datos).
 */
@Controller
public class PropTechController {

    // Instancia estática del cerebro del sistema (Mantiene los datos vivos en memoria)
    private static Inmobiliaria plataforma = new Inmobiliaria();
    private static boolean datosInicializados = false;

    // =====================================================================================
    // 1. CONFIGURACIÓN INICIAL Y DASHBOARD
    // =====================================================================================

    public PropTechController() {
        // Cargamos datos de prueba solo la primera vez que arranca el servidor
        if (!datosInicializados) {
            cargarDatosPrueba();
            datosInicializados = true;
        }
    }

    /**
     * Renderiza la vista principal del sistema (Dashboard) pasándole las métricas iniciales.
     */
    @GetMapping("/")
    public String dashboard(Model model) {
        // 1. Cargar Asesores para los formularios
        LinkedSimpleList<Asesor> listaAsesores = new LinkedSimpleList<>();
        for (int i = 0; i < plataforma.getAsesores().getSize(); i++){
            listaAsesores.addLast(plataforma.getAsesores().getData(i));
        }
        model.addAttribute("listaAsesoresActivos", listaAsesores);

        // 2. Cargar Tareas Administrativas (Cola)
        String sigTarea = plataforma.verSiguienteTarea();
        model.addAttribute("siguienteTarea", sigTarea != null ? sigTarea : "No hay tareas pendientes en la cola.");

        // 3. Cargar Historial (Pila)
        String ultimaAccion = plataforma.obtenerUltimaAccion();
        model.addAttribute("ultimaAccion", ultimaAccion != null ? ultimaAccion : "No hay acciones recientes.");
        model.addAttribute("hayHistorial", ultimaAccion != null);

        // 4. Métricas Generales
        model.addAttribute("totalInmuebles", plataforma.getArbolInmueblesPorPrecio().getSize());
        model.addAttribute("totalClientes", plataforma.getClientes().getSize());
        
        // 5. Generar el JSON del Árbol Binario para el script visual de GoJS
        String treeJson = "[]";
        if (!plataforma.getArbolInmueblesPorPrecio().isEmpty()) {
            treeJson = generateGraphData(plataforma.getArbolInmueblesPorPrecio().root);
        }
        model.addAttribute("treeJsonData", treeJson);

        return "dashboard";
    }

    // =====================================================================================
    // 2. MÓDULO CRUD: INMUEBLES
    // =====================================================================================

    /**
     * Recibe los datos del formulario, enlaza al asesor y registra el Inmueble en el ABB y Tablas Hash.
     */
    @PostMapping("/registrar-inmueble")
    public String registrarInmueble(
        @RequestParam String codigo, @RequestParam String direccion,
        @RequestParam String ciudad, @RequestParam String barrioZona,
        @RequestParam String finalidad, @RequestParam double precio,
        @RequestParam double area, @RequestParam int habitaciones,
        @RequestParam int banos, @RequestParam String estado,
        @RequestParam String idAsesor,
        RedirectAttributes redirectAttrs) {

        Asesor asesorAsignado = plataforma.buscarAsesorPorId(idAsesor);
        if(asesorAsignado == null){
            redirectAttrs.addFlashAttribute("mensajeError", "Error: el asesor seleccionado no existe.");
            return "redirect:/";
        }
        
        Apartamento nuevoInmueble = new Apartamento(codigo, direccion, ciudad, barrioZona, 
                                                    finalidad, precio, area, habitaciones, 
                                                    banos, estado, true, asesorAsignado, true, 0.0);
        
        plataforma.registrarInmueble(nuevoInmueble);
        asesorAsignado.getInmueblesAsignados().addLast(nuevoInmueble);

        redirectAttrs.addFlashAttribute("mensajeExito", "Inmueble " + codigo + " registrado. ¡Revisa el Árbol!");
        return "redirect:/";
    }

    /**
     * Elimina físicamente un Inmueble de todas las estructuras de datos.
     */
    @PostMapping("/eliminar-inmueble")
    public String eliminarInmueble(@RequestParam String codigo, RedirectAttributes redirectAttrs) {
        boolean exito = plataforma.eliminarInmueble(codigo);
        if (exito) {
            redirectAttrs.addFlashAttribute("mensajeExito", "Inmueble " + codigo + " eliminado de todas las estructuras.");
        } else {
            redirectAttrs.addFlashAttribute("mensajeError", "No se encontró el inmueble " + codigo);
        }
        return "redirect:/";
    }

    // =====================================================================================
    // 3. MÓDULO CRUD: CLIENTES Y ASESORES
    // =====================================================================================

    /**
     * Registra un nuevo Cliente en el Hash y el ABB por presupuesto.
     */
    @PostMapping("/registrar-cliente")
    public String registrarCliente(
        @RequestParam String identificacion, @RequestParam String nombre,
        @RequestParam String correo, @RequestParam String telefono,
        @RequestParam String tipoCliente, @RequestParam double presupuesto,
        @RequestParam String tipoInmuebleDeseado, @RequestParam int minHabitaciones,
        RedirectAttributes redirectAttrs) {

        Cliente nuevoCliente = new Cliente(identificacion, nombre, correo, telefono, 
                                           tipoCliente, presupuesto, tipoInmuebleDeseado, minHabitaciones);
        plataforma.registrarCliente(nuevoCliente);

        redirectAttrs.addFlashAttribute("mensajeExito", "Cliente " + nombre + " registrado exitosamente.");
        return "redirect:/";
    }

    /**
     * Elimina físicamente un cliente de las estructuras del sistema.
     */
    @PostMapping("/eliminar-cliente")
    public String eliminarCliente(@RequestParam String idCliente, RedirectAttributes redirectAttrs) {
        boolean exito = plataforma.eliminarCliente(idCliente);
        if (exito) {
            redirectAttrs.addFlashAttribute("mensajeExito", "Cliente " + idCliente + " eliminado exitosamente.");
        } else {
            redirectAttrs.addFlashAttribute("mensajeError", "No se encontró el cliente " + idCliente);
        }
        return "redirect:/";
    }

    /**
     * Registra un Asesor Inmobiliario en la Tabla Hash para búsquedas en O(1).
     */
    @PostMapping("/registrar-asesor")
    public String registrarAsesor(
            @RequestParam String identificacion, @RequestParam String nombre,
            @RequestParam String contacto, @RequestParam String especialidad,
            RedirectAttributes redirectAttrs) {

        Asesor nuevoAsesor = new Asesor(identificacion, nombre, contacto, especialidad);
        plataforma.registrarAsesor(nuevoAsesor);

        redirectAttrs.addFlashAttribute("mensajeExito", "Asesor " + nombre + " registrado correctamente en la Tabla Hash.");
        return "redirect:/";
    }

    // =====================================================================================
    // 4. MÓDULO DE FLUJOS: VISITAS Y OPERACIONES
    // =====================================================================================

    /**
     * Encola una visita en la PriorityQueue según su nivel de urgencia.
     */
    @PostMapping("/agendar-visita")
    public String agendarVisita(
            @RequestParam String idVisita, @RequestParam String idCliente, 
            @RequestParam String codigoInmueble, @RequestParam String fecha, 
            @RequestParam String hora, @RequestParam int nivelUrgencia, 
            RedirectAttributes redirectAttrs) {

        Cliente cliente = plataforma.buscarClientePorId(idCliente);
        Inmueble inmueble = plataforma.buscarInmueblePorCodigo(codigoInmueble);

        if (cliente == null || inmueble == null) {
            redirectAttrs.addFlashAttribute("mensajeError", "Error: Cliente o Inmueble no encontrados.");
            return "redirect:/";
        }

        Asesor asesorAsignado = inmueble.getAsesorResponsable();
        java.time.LocalDate fechaVisita = java.time.LocalDate.parse(fecha);

        Visita nuevaVisita = new Visita(idVisita, cliente, inmueble, fechaVisita, hora, asesorAsignado, nivelUrgencia);
        plataforma.agendarVisita(nuevaVisita);

        cliente.getInmueblesVisitados().addLast(inmueble);
        if(asesorAsignado != null){
            asesorAsignado.getVisitasAgendadas().offer(nuevaVisita); // Usa el método de tu Lista o Cola
        }

        redirectAttrs.addFlashAttribute("mensajeExito", "Visita agendada (Prioridad VIP: " + nivelUrgencia + ")");
        return "redirect:/";
    }

    /**
     * Desencola la próxima visita a atender ignorando las canceladas.
     */
    @PostMapping("/atender-visita")
    public String atenderVisita(RedirectAttributes redirectAttrs) {
        Visita visitaAtendida = plataforma.atenderSiguienteVisita();

        if (visitaAtendida != null) {
            redirectAttrs.addFlashAttribute("mensajeExito", "✅ Atendiendo visita VIP (Nivel " + visitaAtendida.getNivelUrgencia() + "): " + visitaAtendida.getCliente().getNombre());
        } else {
            redirectAttrs.addFlashAttribute("mensajeInfo", "La cola de visitas está vacía.");
        }
        return "redirect:/";
    }

    @PostMapping("/cancelar-visita")
    public String cancelarVisita(@RequestParam String idVisita, RedirectAttributes redirectAttrs) {
        boolean exito = plataforma.cancelarVisita(idVisita);
        if (exito) {
            redirectAttrs.addFlashAttribute("mensajeExito", "Visita " + idVisita + " cancelada exitosamente.");
        } else {
            redirectAttrs.addFlashAttribute("mensajeError", "No se pudo cancelar. Verifique ID o estado.");
        }
        return "redirect:/";
    }

    @PostMapping("/reprogramar-visita")
    public String reprogramarVisita(@RequestParam String idVisita, @RequestParam String nuevaFecha, @RequestParam String nuevaHora, RedirectAttributes redirectAttrs) {
        java.time.LocalDate fechaParsed = java.time.LocalDate.parse(nuevaFecha);
        boolean exito = plataforma.reprogramarVisita(idVisita, fechaParsed, nuevaHora);
        if (exito) {
            redirectAttrs.addFlashAttribute("mensajeExito", "Visita " + idVisita + " reprogramada.");
        } else {
            redirectAttrs.addFlashAttribute("mensajeError", "No se pudo reprogramar la visita.");
        }
        return "redirect:/";
    }

    /**
     * Cierra un negocio (venta/arriendo) afectando la disponibilidad del inmueble.
     */
    @PostMapping("/registrar-operacion")
    public String registrarOperacion(
            @RequestParam String idOperacion, @RequestParam String idCliente, 
            @RequestParam String codigoInmueble, @RequestParam String tipoOperacion,
            @RequestParam double valorAcordado, @RequestParam double porcentajeComision,
            RedirectAttributes redirectAttrs) {

        Cliente cliente = plataforma.buscarClientePorId(idCliente);
        Inmueble inmueble = plataforma.buscarInmueblePorCodigo(codigoInmueble);
        
        if (cliente == null || inmueble == null) {
            redirectAttrs.addFlashAttribute("mensajeError", "Error: Cliente o Inmueble no encontrados.");
            return "redirect:/";
        }
        if (!inmueble.isDisponibilidad() && (tipoOperacion.equals("Venta") || tipoOperacion.equals("Arriendo"))) {
            redirectAttrs.addFlashAttribute("mensajeError", "Error: El inmueble ya no está disponible.");
            return "redirect:/";
        }

        Asesor asesor = inmueble.getAsesorResponsable();
        Operacion nuevaOp = new Operacion(idOperacion, inmueble, cliente, asesor, 
                                          java.time.LocalDate.now(), tipoOperacion, 
                                          valorAcordado, porcentajeComision);

        plataforma.registrarOperacion(nuevaOp);
        cliente.getInmueblesNegociados().addLast(inmueble);
        if(asesor != null) asesor.getCierresRealizados().addLast(nuevaOp);

        redirectAttrs.addFlashAttribute("mensajeExito", "¡Operación (" + tipoOperacion + ") registrada con éxito!");
        return "redirect:/";
    }

    // =====================================================================================
    // 5. MÓDULO INTERACCIÓN Y PERFILES (CLIENTES)
    // =====================================================================================

    @PostMapping("/marcar-favorito")
    public String marcarFavorito(@RequestParam String idCliente, @RequestParam String codigoInmueble, RedirectAttributes redirectAttrs) {
        plataforma.marcarFavorito(idCliente, codigoInmueble);
        redirectAttrs.addFlashAttribute("mensajeExito", "Inmueble " + codigoInmueble + " añadido a favoritos.");
        return "redirect:/";
    }

    @PostMapping("/ver-perfil-cliente")
    public String verPerfilCliente(@RequestParam String idCliente, RedirectAttributes redirectAttrs) {
        Cliente cliente = plataforma.buscarClientePorId(idCliente);
        if (cliente == null) {
            redirectAttrs.addFlashAttribute("mensajeError", "Cliente no encontrado.");
            return "redirect:/";
        }

        List<String> favs = new ArrayList<>();
        for (int i = 0; i < cliente.getFavoritos().getSize(); i++) {
            favs.add("⭐ " + cliente.getFavoritos().getData(i).getCodigo());
        }

        redirectAttrs.addFlashAttribute("nombreClientePerfil", cliente.getNombre());
        redirectAttrs.addFlashAttribute("listaFavoritos", favs);
        redirectAttrs.addFlashAttribute("mostrarModalPerfil", true);
        return "redirect:/";
    }

    // =====================================================================================
    // 6. UTILIDADES: COLAS, PILAS Y AGRUPACIONES (REQUISITOS ESTRUCTURAS)
    // =====================================================================================

    @PostMapping("/agregar-tarea")
    public String agregarTarea(@RequestParam String tarea, RedirectAttributes redirectAttrs) {
        plataforma.registrarTareaAdministrativa(tarea);
        redirectAttrs.addFlashAttribute("mensajeExito", "Tarea administrativa encolada: " + tarea);
        return "redirect:/";
    }

    @PostMapping("/atender-tarea")
    public String atenderTarea(RedirectAttributes redirectAttrs) {
        String tareaResuelta = plataforma.atenderSiguienteTarea(); 
        if (tareaResuelta != null) {
            redirectAttrs.addFlashAttribute("mensajeExito", "✅ Tarea resuelta y sacada de la cola: " + tareaResuelta);
        } else {
            redirectAttrs.addFlashAttribute("mensajeInfo", "La cola de tareas está vacía.");
        }
        return "redirect:/";
    }

    /**
     * Aplica la técnica de 'Undo' utilizando el método pop de la Pila de Historial.
     */
    @PostMapping("/deshacer-accion")
    public String deshacerAccion(RedirectAttributes redirectAttrs) {
        String accionDeshecha = plataforma.extraerUltimoCambio();
        if (accionDeshecha != null) {
            redirectAttrs.addFlashAttribute("mensajeExito", "Se ha deshecho del historial: " + accionDeshecha);
        } else {
            redirectAttrs.addFlashAttribute("mensajeInfo", "La pila de historial está vacía.");
        }
        return "redirect:/";
    }

    /**
     * Aprovecha la agrupación en Tabla Hash (Requisito 5.5).
     */
    @PostMapping("/consultar-ciudad")
    public String consultarCiudad(@RequestParam String ciudad, RedirectAttributes redirectAttrs) {
        LinkedSimpleList<Inmueble> inmuebles = plataforma.obtenerInmueblesPorCiudad(ciudad);
        if (inmuebles.isEmpty()) {
            redirectAttrs.addFlashAttribute("mensajeInfo", "No hay inmuebles registrados en la ciudad: " + ciudad);
        } else {
            List<String> listaAgrupada = new ArrayList<>();
            for(int i = 0; i < inmuebles.getSize(); i++){
                Inmueble inm = inmuebles.getData(i);
                listaAgrupada.add("🏢 " + inm.getCodigo() + " - " + inm.getBarrioZona() + " ($" + String.format("%,.0f", inm.getPrecio()) + ")");
            }
            redirectAttrs.addFlashAttribute("listaResultadosBusqueda", listaAgrupada);
            redirectAttrs.addFlashAttribute("mostrarModalBusqueda", true);
        }
        return "redirect:/";
    }

    // =====================================================================================
    // 7. INTELIGENCIA DE NEGOCIOS Y REPORTES (ÁRBOLES Y GRAFOS)
    // =====================================================================================

    /**
     * Ejecuta una búsqueda óptima podando ramas del Árbol Binario según el rango de precio.
     */
    @PostMapping("/buscar-inmuebles")
    public String buscarInmuebles(
            @RequestParam(defaultValue = "0") double precioMin, @RequestParam(defaultValue = "1000000000") double precioMax,
            @RequestParam String zona, @RequestParam(defaultValue = "0") int minHabitaciones, RedirectAttributes redirectAttrs) {

        LinkedSimpleList<Inmueble> resultadosPropios = plataforma.buscarInmuebleConFiltros(precioMin, precioMax, zona, minHabitaciones);
        List<String> listaResultados = new ArrayList<>();
        for (int i = 0; i < resultadosPropios.getSize(); i++) {
            Inmueble inm = resultadosPropios.getData(i);
            listaResultados.add("🏠 " + inm.getCodigo() + " en " + inm.getBarrioZona() + " | Precio: $" + String.format("%,.0f", inm.getPrecio()));
        }

        if (listaResultados.isEmpty()) {
            redirectAttrs.addFlashAttribute("mensajeInfo", "No se encontraron inmuebles que coincidan.");
        } else {
            redirectAttrs.addFlashAttribute("listaResultadosBusqueda", listaResultados);
            redirectAttrs.addFlashAttribute("mostrarModalBusqueda", true);
        }
        return "redirect:/";
    }

    /**
     * Cruza la información del Grafo de interacciones y el Árbol de presupuesto.
     */
    @PostMapping("/generar-recomendaciones")
    public String generarRecomendaciones(@RequestParam String idCliente, RedirectAttributes redirectAttrs) {
        Cliente cliente = plataforma.buscarClientePorId(idCliente);
        if (cliente == null) {
            redirectAttrs.addFlashAttribute("mensajeError", "Error: Cliente no encontrado.");
            return "redirect:/";
        }

        LinkedSimpleList<Inmueble> recos = plataforma.generarRecomendaciones(idCliente);
        if (recos.getSize() == 0) {
            redirectAttrs.addFlashAttribute("mensajeInfo", "No hay recomendaciones para " + cliente.getNombre());
        } else {
            List<String> listaSugerencias = new ArrayList<>();
            for (int i = 0; i < recos.getSize(); i++) {
                Inmueble inm = recos.getData(i);
                listaSugerencias.add("📌 " + inm.getCodigo() + " - " + inm.getBarrioZona() + " | Precio: $" + String.format("%,.0f", inm.getPrecio()));
            }
            redirectAttrs.addFlashAttribute("clienteReco", cliente.getNombre());
            redirectAttrs.addFlashAttribute("listaSugerencias", listaSugerencias);
            redirectAttrs.addFlashAttribute("mostrarModalRecos", true);
        }
        return "redirect:/";
    }

    /**
     * Módulo de Reportes usando recorridos y ordenamiento.
     */
    @PostMapping("/generar-reportes")
    public String generarReportes(RedirectAttributes redirectAttrs) {
        LinkedSimpleList<Asesor> rankingAsesores = plataforma.generarRankingAsesores();
        List<String> listaAsesores = new ArrayList<>();
        for (int i = 0; i < rankingAsesores.getSize(); i++) {
            Asesor a = rankingAsesores.getData(i);
            listaAsesores.add("🏆 " + a.getNombre() + " | Ventas/Cierres: " + a.getCargaTrabajoActiva());
        }

        LinkedSimpleList<String> actividadZonas = plataforma.obtenerRankingZonas();
        List<String> listaZonas = new ArrayList<>();
        for (int i = 0; i < actividadZonas.getSize(); i++) {
            listaZonas.add(actividadZonas.getData(i));
        }

        redirectAttrs.addFlashAttribute("listaRankingAsesores", listaAsesores);
        redirectAttrs.addFlashAttribute("listaActividadZonas", listaZonas);
        redirectAttrs.addFlashAttribute("mostrarModalReportes", true);
        return "redirect:/";
    }

    /**
     * Panel Avanzado Gerencial.
     */
    @PostMapping("/panel-gerencial")
    public String panelGerencial(@RequestParam(defaultValue = "Norte") String zonaSimulacion, RedirectAttributes redirectAttrs) {
        LinkedSimpleList<Cliente> vips = plataforma.detectarClientesAltaPrioridad();
        List<String> listaVips = new ArrayList<>();
        for(int i = 0; i < vips.getSize(); i++) {
            Cliente c = vips.getData(i);
            listaVips.add("🌟 " + c.getNombre() + " (Presupuesto: $" + String.format("%,.0f", c.getPresupuesto()) + ")");
        }

        LinkedSimpleList<String> ranking = plataforma.obtenerRankingZonas();
        List<String> listaRanking = new ArrayList<>();
        for(int i = 0; i < ranking.getSize(); i++) {
            listaRanking.add(ranking.getData(i));
        }

        String resultadoSimulacion = plataforma.simularCrecimientoDemanda(zonaSimulacion);

        redirectAttrs.addFlashAttribute("listaVips", listaVips.isEmpty() ? List.of("No hay clientes VIP detectados.") : listaVips);
        redirectAttrs.addFlashAttribute("listaRankingZonas", listaRanking.isEmpty() ? List.of("No hay actividad.") : listaRanking);
        redirectAttrs.addFlashAttribute("resultadoSimulacion", resultadoSimulacion);
        redirectAttrs.addFlashAttribute("mostrarModalGerencial", true);
        return "redirect:/";
    }

    /**
     * Analizador de comportamiento para llenar la Bicola de Alertas.
     */
    @PostMapping("/analizar-comportamiento")
    public String analizarComportamiento(RedirectAttributes redirectAttrs) {
        plataforma.detectarComportamientosInusuales();
        LinkedSimpleList<String> alertasPropias = plataforma.extraerAlertas();
        
        List<String> alertasParaHTML = new ArrayList<>();
        for (int i = 0; i < alertasPropias.getSize(); i++) {
            alertasParaHTML.add(alertasPropias.getData(i));
        }

        if (alertasParaHTML.isEmpty()) {
            redirectAttrs.addFlashAttribute("mensajeInfo", "Análisis completado: No se detectaron anomalías.");
        } else {
            redirectAttrs.addFlashAttribute("mensajeError", "¡Atención! Se detectaron comportamientos inusuales.");
            redirectAttrs.addFlashAttribute("listaAlertas", alertasParaHTML);
        }
        return "redirect:/";
    }

    // =====================================================================================
    // 8. MÉTODOS AUXILIARES Y JSON
    // =====================================================================================

    /**
     * Convierte el Árbol Binario en un JSON recursivo compatible con la librería gráfica GoJS.
     */
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
        String currentKey = node.getData().getCodigo(); 
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

    /**
     * Mock de datos iniciales para facilitar la calificación y pruebas.
     */
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
        asesor1.getInmueblesAsignados().addLast(apt1);
        asesor1.getInmueblesAsignados().addLast(apt2);
        asesor2.getInmueblesAsignados().addLast(loc1);

        Cliente cliente1 = new Cliente("C-001", "Andrés", "andres@gmail.com", "333", "Comprador", 300000000.0, "Apartamento", 2);
        Cliente cliente2 = new Cliente("C-002", "Nathaly", "nat@gmail.com", "444", "Inversionista", 600000000.0, "LocalComercial", 0);
        plataforma.registrarCliente(cliente1);
        plataforma.registrarCliente(cliente2);

        cliente1.getHistorialConsultas().addLast(apt1);
        plataforma.conectarClientesConInmuebles(cliente1.getId(), apt1.getCodigo());

        for(int i = 0; i < 12; i++){
            Visita v = new Visita("VIS-TEST-" + i, cliente2, loc1, LocalDate.now(), "10:00 AM", asesor2, 1);
            loc1.registrarVisita(v);
        }
    }
}