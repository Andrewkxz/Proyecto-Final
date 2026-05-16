package co.edu.uniquindio.proptech;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import co.edu.uniquindio.proptech.BinarySearchTree.Node;

import jakarta.servlet.http.HttpSession;

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
        if (!datosInicializados) {
            cargarDatosPrueba();
            datosInicializados = true;
        }
    }

    // =====================================================================================
    // 1. SISTEMA DE LOGIN Y SESIONES (Requerimiento de última hora)
    // =====================================================================================
    
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; 
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String username, @RequestParam String password, 
                                HttpSession session, RedirectAttributes redirectAttrs) {
        Usuario u = plataforma.buscarUsuarioPorUsername(username);
        
        if (u != null && u.getPassword().equals(password)) {
            session.setAttribute("usuarioLogueado", u);
            return "redirect:/";
        }
        redirectAttrs.addFlashAttribute("error", "Usuario o contraseña incorrectos.");
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate(); 
        return "redirect:/login";
    }

    // =====================================================================================
    // 2. CARGA DEL DASHBOARD PRINCIPAL
    // =====================================================================================

    @GetMapping("/")
    public String dashboard(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) {
            return "redirect:/login"; 
        }

        // Datos de Sesión
        model.addAttribute("usuarioActual", usuarioLogueado);
        model.addAttribute("rolActual", usuarioLogueado.getRol());
        model.addAttribute("idAsociadoActual", usuarioLogueado.getIdAsociado());

        // Métricas Globales
        model.addAttribute("totalInmuebles", plataforma.getArbolInmueblesPorPrecio().getSize());
        
        // Estado de la Pila (Deshacer)
        String ultimaAccion = plataforma.obtenerUltimaAccion();
        model.addAttribute("ultimaAccion", ultimaAccion != null ? ultimaAccion : "No hay acciones recientes.");
        model.addAttribute("hayHistorial", ultimaAccion != null);

        // Estado de las Colas
        String sigTarea = plataforma.verSiguienteTarea();
        model.addAttribute("siguienteTarea", sigTarea != null ? sigTarea : "No hay tareas.");
        
        String sigSolicitud = plataforma.verSiguienteSolicitud();
        model.addAttribute("siguienteSolicitud", sigSolicitud != null ? sigSolicitud : "No hay solicitudes.");

        // Pasamos asesores activos para el formulario de registrar inmueble
        List<Asesor> listaAsesores = new ArrayList<>();
        for (int i = 0; i < plataforma.getAsesores().getSize(); i++) {
            listaAsesores.add(plataforma.getAsesores().getData(i));
        }
        model.addAttribute("listaAsesoresActivos", listaAsesores);

        // JSON del Árbol (Solo si es Admin/Asesor)
        String treeJson = "[]";
        if (usuarioLogueado.getRol().equals("ADMIN") || usuarioLogueado.getRol().equals("ASESOR")) {
            if (!plataforma.getArbolInmueblesPorPrecio().isEmpty()) {
                treeJson = generateGraphData(plataforma.getArbolInmueblesPorPrecio().root);
            }
        }
        model.addAttribute("treeJsonData", treeJson);

        return "dashboard";
    }

    // =====================================================================================
    // 3. CRUD: INMUEBLES, CLIENTES Y ASESORES
    // =====================================================================================

    @PostMapping("/registrar-inmueble")
    public String registrarInmueble(
        @RequestParam String codigo, @RequestParam String direccion, @RequestParam String ciudad, 
        @RequestParam String barrioZona, @RequestParam String finalidad, @RequestParam double precio,
        @RequestParam double area, @RequestParam int habitaciones, @RequestParam int banos, 
        @RequestParam String estado, @RequestParam String idAsesor, RedirectAttributes redirectAttrs) {

        Asesor asesorAsignado = plataforma.buscarAsesorPorId(idAsesor);
        if(asesorAsignado == null) {
            redirectAttrs.addFlashAttribute("mensajeError", "Error: El asesor seleccionado no existe.");
            return "redirect:/";
        }
        
        Apartamento nuevoInmueble = new Apartamento(codigo, direccion, ciudad, barrioZona, finalidad, precio, area, habitaciones, banos, estado, true, asesorAsignado, true, 0.0);
        boolean exito = plataforma.registrarInmueble(nuevoInmueble);

        if(exito) redirectAttrs.addFlashAttribute("mensajeExito", "Inmueble " + codigo + " registrado. ¡Revisa el Árbol!");
        else redirectAttrs.addFlashAttribute("mensajeError", "Error: Ya existe un inmueble con ese código.");
        
        return "redirect:/";
    }

    @PostMapping("/eliminar-inmueble")
    public String eliminarInmueble(@RequestParam String codigo, RedirectAttributes redirectAttrs) {
        boolean exito = plataforma.eliminarInmueble(codigo);
        if(exito) redirectAttrs.addFlashAttribute("mensajeExito", "Inmueble " + codigo + " eliminado correctamente.");
        else redirectAttrs.addFlashAttribute("mensajeError", "Inmueble no encontrado.");
        return "redirect:/";
    }

    @PostMapping("/registrar-cliente")
    public String registrarCliente(
        @RequestParam String identificacion, @RequestParam String nombre, @RequestParam String correo, 
        @RequestParam String telefono, @RequestParam String tipoCliente, @RequestParam double presupuesto,
        @RequestParam String tipoInmuebleDeseado, @RequestParam int minHabitaciones, RedirectAttributes redirectAttrs) {

        Cliente nuevoCliente = new Cliente(identificacion, nombre, correo, telefono, tipoCliente, presupuesto, tipoInmuebleDeseado, minHabitaciones);
        boolean exito = plataforma.registrarCliente(nuevoCliente);

        if(exito) redirectAttrs.addFlashAttribute("mensajeExito", "Cliente " + nombre + " registrado exitosamente.");
        else redirectAttrs.addFlashAttribute("mensajeError", "Error: El cliente ya existe.");
        return "redirect:/";
    }

    @PostMapping("/eliminar-cliente")
    public String eliminarCliente(@RequestParam String idCliente, RedirectAttributes redirectAttrs) {
        boolean exito = plataforma.eliminarCliente(idCliente);
        if(exito) redirectAttrs.addFlashAttribute("mensajeExito", "Cliente " + idCliente + " eliminado correctamente.");
        else redirectAttrs.addFlashAttribute("mensajeError", "Cliente no encontrado.");
        return "redirect:/";
    }

    @PostMapping("/registrar-asesor")
    public String registrarAsesor(@RequestParam String identificacion, @RequestParam String nombre, @RequestParam String contacto, @RequestParam String especialidad, RedirectAttributes redirectAttrs) {
        Asesor a = new Asesor(identificacion, nombre, contacto, especialidad);
        boolean exito = plataforma.registrarAsesor(a);
        if(exito) redirectAttrs.addFlashAttribute("mensajeExito", "Asesor " + nombre + " registrado.");
        else redirectAttrs.addFlashAttribute("mensajeError", "Error: El asesor ya existe.");
        return "redirect:/";
    }

    // =====================================================================================
    // 4. OPERACIONES DE NEGOCIO Y VISITAS
    // =====================================================================================

    @PostMapping("/registrar-operacion")
    public String registrarOperacion(
        @RequestParam String idOperacion, @RequestParam String tipoOperacion, @RequestParam String idCliente,
        @RequestParam String codigoInmueble, @RequestParam double valorAcordado, @RequestParam double porcentajeComision,
        @RequestParam String idAsesor, // <-- VUELVE EL CAMPO MANUAL PARA EL ASESOR
        RedirectAttributes redirectAttrs) {
        
        Cliente c = plataforma.buscarClientePorId(idCliente);
        Inmueble i = plataforma.buscarInmueblePorCodigo(codigoInmueble);
        Asesor a = plataforma.buscarAsesorPorId(idAsesor); // <-- El que tú elijas en la vista

        if (c != null && i != null && a != null) {
            Operacion op = new Operacion(idOperacion, i, c, a, LocalDate.now(), tipoOperacion, valorAcordado, porcentajeComision);
            plataforma.registrarOperacion(op);
            c.registrarInmuebleNegociado(i);
            redirectAttrs.addFlashAttribute("mensajeExito", "Operación registrada y sumada al asesor: " + a.getNombre());
        } else {
            redirectAttrs.addFlashAttribute("mensajeError", "Error: Verifique que Cliente, Inmueble y Asesor existan.");
        }
        return "redirect:/";
    }

    @PostMapping("/agendar-visita")
    public String agendarVisita(
        @RequestParam String idVisita, @RequestParam String idCliente, @RequestParam String codigoInmueble,
        @RequestParam String fecha, @RequestParam String hora, @RequestParam int nivelUrgencia,
        HttpSession session, RedirectAttributes redirectAttrs) {
        
        Cliente c = plataforma.buscarClientePorId(idCliente);
        Inmueble i = plataforma.buscarInmueblePorCodigo(codigoInmueble);
        Usuario logueado = (Usuario) session.getAttribute("usuarioLogueado");
        Asesor a = plataforma.buscarAsesorPorId(logueado.getIdAsociado());

        if (c != null && i != null && a != null) {
            Visita v = new Visita(idVisita, c, i, LocalDate.parse(fecha), hora, a, nivelUrgencia);
            plataforma.agendarVisita(v);
            redirectAttrs.addFlashAttribute("mensajeExito", "Visita " + idVisita + " encolada.");
        } else {
            redirectAttrs.addFlashAttribute("mensajeError", "Error en datos. Solo asesores pueden agendar.");
        }
        return "redirect:/";
    }

    @PostMapping("/confirmar-visita")
    public String confirmarVisita(@RequestParam String idVisita, RedirectAttributes redirectAttrs) {
        boolean exito = plataforma.confirmarVisita(idVisita);
        if (exito) redirectAttrs.addFlashAttribute("mensajeExito", "Visita " + idVisita + " confirmada.");
        else redirectAttrs.addFlashAttribute("mensajeError", "No se pudo confirmar.");
        return "redirect:/";
    }

    @PostMapping("/cancelar-visita")
    public String cancelarVisita(@RequestParam String idVisita, RedirectAttributes redirectAttrs) {
        boolean exito = plataforma.cancelarVisita(idVisita);
        if (exito) redirectAttrs.addFlashAttribute("mensajeExito", "Visita " + idVisita + " cancelada.");
        else redirectAttrs.addFlashAttribute("mensajeError", "No se pudo cancelar.");
        return "redirect:/";
    }

    @PostMapping("/atender-visita")
    public String atenderVisita(RedirectAttributes redirectAttrs) {
        Visita v = plataforma.atenderSiguienteVisita();
        if (v != null) {
            redirectAttrs.addFlashAttribute("mensajeExito", "Visita atendida: " + v.getIdVisita());
        } else {
            redirectAttrs.addFlashAttribute("mensajeInfo", "La cola de visitas está vacía.");
        }
        return "redirect:/";
    }

    // =====================================================================================
    // 5. INTERACCIÓN Y PERFILES (CLIENTE)
    // =====================================================================================

    @PostMapping("/marcar-favorito")
    public String marcarFavorito(@RequestParam String idCliente, @RequestParam String codigoInmueble, RedirectAttributes redirectAttrs) {
        plataforma.marcarFavorito(idCliente, codigoInmueble);
        redirectAttrs.addFlashAttribute("mensajeExito", "Inmueble añadido a favoritos.");
        return "redirect:/";
    }

    @PostMapping("/registrar-consulta")
    public String registrarConsulta(@RequestParam String idCliente, @RequestParam String codigoInmueble, RedirectAttributes redirectAttrs) {
        Cliente c = plataforma.buscarClientePorId(idCliente);
        Inmueble i = plataforma.buscarInmueblePorCodigo(codigoInmueble);
        
        if (c != null && i != null) {
            plataforma.registrarConsultaInmueble(idCliente, codigoInmueble);
            redirectAttrs.addFlashAttribute("mensajeExito", "Consulta registrada (Grafo actualizado).");
        } else {
            redirectAttrs.addFlashAttribute("mensajeError", "Error: Datos no encontrados.");
        }
        return "redirect:/";
    }

    @PostMapping("/descartar-inmueble")
    public String descartarInmueble(@RequestParam String idCliente, @RequestParam String codigoInmueble, RedirectAttributes redirectAttrs) {
        boolean exito = plataforma.descartarInmueble(idCliente, codigoInmueble);
        if(exito) redirectAttrs.addFlashAttribute("mensajeExito", "Inmueble descartado.");
        else redirectAttrs.addFlashAttribute("mensajeError", "Error: Datos no encontrados.");
        return "redirect:/";
    }

    @PostMapping("/registrar-intencion")
    public String registrarIntencion(@RequestParam String idCliente, @RequestParam String codigoInmueble, RedirectAttributes redirectAttrs) {
        boolean exito = plataforma.registrarIntencionDeNegocio(idCliente, codigoInmueble);
        if(exito) redirectAttrs.addFlashAttribute("mensajeExito", "Intención de negocio registrada.");
        else redirectAttrs.addFlashAttribute("mensajeError", "Error en datos.");
        return "redirect:/";
    }

    @PostMapping("/ver-perfil-cliente")
    public String verPerfilCliente(@RequestParam String idCliente, RedirectAttributes redirectAttrs) {
        Cliente c = plataforma.buscarClientePorId(idCliente);
        if (c == null) {
            redirectAttrs.addFlashAttribute("mensajeError", "Cliente no encontrado.");
            return "redirect:/";
        }

        // Extracción de listas para la vista
        List<String> favs = new ArrayList<>();
        for (int i = 0; i < c.getFavoritos().getSize(); i++) favs.add("⭐ " + c.getFavoritos().getData(i).getCodigo());
        
        List<String> consultas = new ArrayList<>();
        for (int i = 0; i < c.getHistorialConsultas().getSize(); i++) consultas.add("🔍 " + c.getHistorialConsultas().getData(i).getCodigo());

        List<String> visitas = new ArrayList<>();
        for (int i = 0; i < c.getInmueblesVisitados().getSize(); i++) visitas.add("🚶 " + c.getInmueblesVisitados().getData(i).getCodigo());

        List<String> intenciones = new ArrayList<>();
        for (int i = 0; i < c.getIntenciones().getSize(); i++) intenciones.add("🎯 " + c.getIntenciones().getData(i).getCodigo());

        List<String> negociados = new ArrayList<>();
        for (int i = 0; i < c.getInmueblesNegociados().getSize(); i++) negociados.add("🤝 " + c.getInmueblesNegociados().getData(i).getCodigo());

        List<String> descartados = new ArrayList<>();
        for (int i = 0; i < c.getInmueblesDescartados().getSize(); i++) descartados.add("🚫 " + c.getInmueblesDescartados().getData(i).getCodigo());

        redirectAttrs.addFlashAttribute("nombreClientePerfil", c.getNombre());
        redirectAttrs.addFlashAttribute("listaFavoritos", favs);
        redirectAttrs.addFlashAttribute("listaConsultas", consultas);
        redirectAttrs.addFlashAttribute("listaVisitas", visitas);
        redirectAttrs.addFlashAttribute("listaIntenciones", intenciones);
        redirectAttrs.addFlashAttribute("listaNegociados", negociados);
        redirectAttrs.addFlashAttribute("listaDescartados", descartados); 
        
        redirectAttrs.addFlashAttribute("mostrarModalPerfil", true);
        return "redirect:/";
    }

    // =====================================================================================
    // 6. BUSCADOR, REPORTES Y COLAS
    // =====================================================================================

    @PostMapping("/buscar-inmuebles")
    public String buscarInmuebles(
            @RequestParam(defaultValue = "0") double precioMin, @RequestParam(defaultValue = "1000000000") double precioMax,
            @RequestParam String zona, @RequestParam(defaultValue = "0") int minHabitaciones, RedirectAttributes redirectAttrs) {

        co.edu.uniquindio.proptech.LinkedSimpleList.LinkedSimpleList<Inmueble> resultados = plataforma.buscarInmuebleConFiltros(precioMin, precioMax, zona, minHabitaciones);
        List<String> rList = new ArrayList<>();
        for (int i = 0; i < resultados.getSize(); i++) {
            Inmueble inm = resultados.getData(i);
            rList.add("🏠 " + inm.getCodigo() + " en " + inm.getBarrioZona() + " | $" + String.format("%,.0f", inm.getPrecio()));
        }

        if (rList.isEmpty()) redirectAttrs.addFlashAttribute("mensajeInfo", "Sin coincidencias.");
        else {
            redirectAttrs.addFlashAttribute("listaResultadosBusqueda", rList);
            redirectAttrs.addFlashAttribute("mostrarModalBusqueda", true);
        }
        return "redirect:/";
    }

    @PostMapping("/generar-recomendaciones")
    public String generarRecomendaciones(@RequestParam String idCliente, RedirectAttributes redirectAttrs) {
        Cliente c = plataforma.buscarClientePorId(idCliente);
        if (c == null) {
            redirectAttrs.addFlashAttribute("mensajeError", "Cliente no encontrado.");
            return "redirect:/";
        }
        co.edu.uniquindio.proptech.LinkedSimpleList.LinkedSimpleList<Inmueble> recos = plataforma.generarRecomendaciones(idCliente);
        if (recos.getSize() == 0) redirectAttrs.addFlashAttribute("mensajeInfo", "No hay sugerencias.");
        else {
            List<String> list = new ArrayList<>();
            for (int i = 0; i < recos.getSize(); i++) {
                Inmueble inm = recos.getData(i);
                list.add("📌 " + inm.getCodigo() + " - $" + String.format("%,.0f", inm.getPrecio()));
            }
            redirectAttrs.addFlashAttribute("clienteReco", c.getNombre());
            redirectAttrs.addFlashAttribute("listaSugerencias", list);
            redirectAttrs.addFlashAttribute("mostrarModalRecos", true); 
        }
        return "redirect:/";
    }

    @PostMapping("/analizar-comportamiento")
    public String analizarComportamiento(RedirectAttributes redirectAttrs) {
        plataforma.detectarComportamientosInusuales();
        co.edu.uniquindio.proptech.LinkedSimpleList.LinkedSimpleList<String> alertas = plataforma.extraerAlertas();
        List<String> list = new ArrayList<>();
        for (int i = 0; i < alertas.getSize(); i++) list.add(alertas.getData(i));

        if (list.isEmpty()) redirectAttrs.addFlashAttribute("mensajeInfo", "Sistema estable.");
        else {
            redirectAttrs.addFlashAttribute("mensajeError", "Anomalías detectadas.");
            redirectAttrs.addFlashAttribute("listaAlertas", list);
        }
        return "redirect:/";
    }

    @PostMapping("/generar-reportes")
    public String generarReportes(RedirectAttributes redirectAttrs) {
        co.edu.uniquindio.proptech.LinkedSimpleList.LinkedSimpleList<Asesor> rA = plataforma.generarRankingAsesores();
        List<String> lA = new ArrayList<>();
        for (int i = 0; i < rA.getSize(); i++) lA.add("🏆 " + rA.getData(i).getNombre() + " | Cierres: " + rA.getData(i).getNumeroCierres());

        co.edu.uniquindio.proptech.LinkedSimpleList.LinkedSimpleList<String> aZ = plataforma.obtenerResumenZonas();
        List<String> lZ = new ArrayList<>();
        for (int i = 0; i < aZ.getSize(); i++) lZ.add(aZ.getData(i));

        redirectAttrs.addFlashAttribute("listaRankingAsesores", lA);
        redirectAttrs.addFlashAttribute("listaActividadZonas", lZ);
        redirectAttrs.addFlashAttribute("mostrarModalReportes", true);
        return "redirect:/";
    }

    @PostMapping("/agregar-tarea")
    public String agregarTarea(@RequestParam String tarea, RedirectAttributes redirectAttrs) {
        plataforma.registrarTareaAdministrativa(tarea);
        redirectAttrs.addFlashAttribute("mensajeExito", "Tarea registrada.");
        return "redirect:/";
    }

    @PostMapping("/atender-tarea")
    public String atenderTarea(RedirectAttributes redirectAttrs) {
        String res = plataforma.atenderSiguienteTarea(); 
        if (res != null) redirectAttrs.addFlashAttribute("mensajeExito", "Tarea resuelta.");
        return "redirect:/";
    }

    @PostMapping("/agregar-solicitud")
    public String agregarSolicitud(@RequestParam String solicitud, RedirectAttributes redirectAttrs) {
        plataforma.registrarSolicitudCliente(solicitud);
        redirectAttrs.addFlashAttribute("mensajeExito", "Solicitud encolada.");
        return "redirect:/";
    }

    @PostMapping("/atender-solicitud")
    public String atenderSolicitud(RedirectAttributes redirectAttrs) {
        String res = plataforma.atenderSiguienteSolicitud(); 
        if (res != null) redirectAttrs.addFlashAttribute("mensajeExito", "Solicitud atendida.");
        return "redirect:/";
    }

    @PostMapping("/deshacer-accion")
    public String deshacerAccion(RedirectAttributes redirectAttrs) {
        String res = plataforma.extraerUltimoCambio();
        if (res != null) redirectAttrs.addFlashAttribute("mensajeExito", "Deshecho: " + res);
        return "redirect:/";
    }

    // =====================================================================================
    // 7. LÓGICA DEL ÁRBOL (GoJS JSON) Y DATOS DE PRUEBA
    // =====================================================================================

    private String generateGraphData(Node<Inmueble> root) {
        List<Map<String, Object>> nodeList = new ArrayList<>();
        populateJsonModel(root, nodeList, null, null);
        try {
            return new ObjectMapper().writeValueAsString(nodeList);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private void populateJsonModel(Node<Inmueble> node, List<Map<String, Object>> list, String parentKey, String direction) {
        if (node == null) return;
        Map<String, Object> nodeMap = new HashMap<>();
        String currentKey = node.getData().getCodigo(); 
        nodeMap.put("key", currentKey);
        nodeMap.put("text", currentKey + "\n$" + String.format("%,.0f", node.getData().getPrecio()));
        if (parentKey != null) {
            nodeMap.put("parent", parentKey);
            nodeMap.put("dir", direction);
        }
        list.add(nodeMap);
        populateJsonModel(node.getLeft(), list, currentKey, "L");
        populateJsonModel(node.getRight(), list, currentKey, "R");
    }

    private void cargarDatosPrueba() {
        // 1. REGISTRAMOS LOS ASESORES DEL CSV EN EL SISTEMA
        Asesor admin = new Asesor("ADMIN-01", "Admin Sup", "000", "General");
        Asesor asesor1 = new Asesor("A-101", "Juli", "3112345678", "Norte");
        Asesor asesor2 = new Asesor("A-102", "Juan", "3128765432", "Centro");

        plataforma.registrarAsesor(admin);
        plataforma.registrarAsesor(asesor1);
        plataforma.registrarAsesor(asesor2);

        // 2. REGISTRAMOS LOS CLIENTES DEL CSV EN EL SISTEMA
        Cliente cliente1 = new Cliente("C-001", "Andrés", "andres@aura.com", "3001112222", "Comprador", 300000000.0, "Apartamento", 2);
        Cliente cliente2 = new Cliente("C-002", "Nathaly", "nat@aura.com", "3003334444", "Inversionista", 600000000.0, "LocalComercial", 0);

        plataforma.registrarCliente(cliente1);
        plataforma.registrarCliente(cliente2);

        // 3. REGISTRAMOS INMUEBLES BASE ASIGNADOS A ESTOS ASESORES
        Apartamento apt1 = new Apartamento("APT-001", "Calle 10N #14-20", "Armenia", "Norte", "Venta", 250000000.0, 65.0, 3, 2, "Nuevo", true, asesor1, true, 200000.0);
        Apartamento apt2 = new Apartamento("APT-002", "Av. Centenario", "Armenia", "Norte", "Arriendo", 1500000.0, 50.0, 2, 1, "Usado", true, asesor1, false, 150000.0);
        LocalComercial loc1 = new LocalComercial("LOC-001", "Carrera 14 #23-00", "Armenia", "Centro", "Venta", 500000000.0, 120.0, 1, 2, "Remodelado", true, asesor2, true, "Comercial Mixto");

        plataforma.registrarInmueble(apt1);
        plataforma.registrarInmueble(apt2);
        plataforma.registrarInmueble(loc1);

        // 4. CREAMOS ALGUNAS INTERACCIONES INICIALES DE PRUEBA
        // Andrés consulta el inmueble APT-001 (Se agrega al Grafo)
        plataforma.registrarConsultaInmueble(cliente1.getId(), apt1.getCodigo());
    }
}