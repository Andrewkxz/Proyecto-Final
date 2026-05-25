package co.edu.uniquindio.proptech;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import co.edu.uniquindio.proptech.BinarySearchTree.Node;
import jakarta.servlet.http.HttpSession;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class PropTechController {

    @Autowired
    private Inmobiliaria plataforma;

    @Autowired
    private GeminiService geminiService;

    private Map<String, String> inmuebleToMap(Inmueble inm) {
        Map<String, String> map = new HashMap<>();
        map.put("codigo", inm.getCodigo());
        map.put("direccion", inm.getDireccion() != null ? inm.getDireccion() : "Sin Dirección");
        map.put("barrioZona", inm.getBarrioZona() != null ? inm.getBarrioZona() : "Sin Zona");
        map.put("precio", String.format("%,.0f", inm.getPrecio()));
        map.put("estado", inm.getEstado() != null ? inm.getEstado() : "Activo");
        map.put("habitaciones", getSafeFieldValue(inm, "getHabitaciones", "0"));
        map.put("banos", getSafeFieldValue(inm, "getBaños", "0"));
        map.put("area", getSafeFieldValue(inm, "getArea", "0"));

        String[] fotos;
        if (inm instanceof Casa) {
            fotos = new String[] {
                    "https://images.unsplash.com/photo-1568605114967-8130f3a36994?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1576941089067-2de3c901e126?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1600585154526-990dced4db0d?auto=format&fit=crop&w=800&q=80"
            };
        } else if (inm instanceof Apartamento) {
            fotos = new String[] {
                    "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1494526585095-c41746248156?auto=format&fit=crop&w=800&q=80"
            };
        } else if (inm instanceof Oficina) {
            fotos = new String[] {
                    "https://images.unsplash.com/photo-1497366754035-f200968a6e72?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1497366811353-6870744d04b2?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1497215842964-222b430dc094?auto=format&fit=crop&w=800&q=80"
            };
        } else if (inm instanceof Bodega) {
            fotos = new String[] {
                    "https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1553413077-190dd305871c?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1581092918056-0c4c3acd3789?auto=format&fit=crop&w=800&q=80"
            };
        } else if (inm instanceof Lote) {
            fotos = new String[] {
                    "https://images.unsplash.com/photo-1500382017468-9049fed747ef?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=800&q=80",
                    "https://images.unsplash.com/photo-1473448912268-2022ce9509d8?auto=format&fit=crop&w=800&q=80"
            };
        } else {
            fotos = new String[] {
                    "https://images.unsplash.com/photo-1564013799919-ab600027ffc6?auto=format&fit=crop&w=800&q=80"
            };
        }
        map.put("imagen", fotos[Math.abs(inm.getCodigo().hashCode()) % fotos.length]);
        return map;
    }

    private String getSafeFieldValue(Object obj, String methodName, String def) {
        try {
            return String.valueOf(obj.getClass().getMethod(methodName).invoke(obj));
        } catch (Exception e) { return def; }
    }

    private List<Map<String, String>> convertirListaInmuebles(
            co.edu.uniquindio.proptech.DoublyLinkedList.DoublyLinkedList<Inmueble> listaPropia) {
        List<Map<String, String>> listaSegura = new ArrayList<>();
        for (int i = 0; i < listaPropia.getSize(); i++)
            listaSegura.add(inmuebleToMap(listaPropia.getData(i)));
        return listaSegura;
    }

    private List<Map<String, String>> convertirListaInmuebles(
            co.edu.uniquindio.proptech.LinkedSimpleList.LinkedSimpleList<Inmueble> listaPropia) {
        List<Map<String, String>> listaSegura = new ArrayList<>();
        for (int i = 0; i < listaPropia.getSize(); i++)
            listaSegura.add(inmuebleToMap(listaPropia.getData(i)));
        return listaSegura;
    }

    @GetMapping("/login")
    public String mostrarLogin() { return "login"; }

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

    @GetMapping("/")
    public String dashboard(HttpSession session, Model model) {
        Usuario usuarioLogueado = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuarioLogueado == null) return "redirect:/login";

        model.addAttribute("usuarioActual", usuarioLogueado);
        model.addAttribute("rolActual", usuarioLogueado.getRol());
        model.addAttribute("idAsociadoActual", usuarioLogueado.getIdAsociado());
        model.addAttribute("totalInmuebles", plataforma.getArbolInmueblesPorPrecio().getSize());

        String ultimaAccion = plataforma.obtenerUltimaAccion();
        model.addAttribute("ultimaAccion", ultimaAccion != null ? ultimaAccion : "No hay acciones recientes.");
        model.addAttribute("hayHistorial", ultimaAccion != null);
        model.addAttribute("siguienteTarea",
                plataforma.verSiguienteTarea() != null ? plataforma.verSiguienteTarea() : "No hay tareas.");
        model.addAttribute("siguienteSolicitud",
                plataforma.verSiguienteSolicitud() != null ? plataforma.verSiguienteSolicitud() : "No hay solicitudes.");

        List<Asesor> listaAsesores = new ArrayList<>();
        for (int i = 0; i < plataforma.getAsesores().getSize(); i++)
            listaAsesores.add(plataforma.getAsesores().getData(i));
        model.addAttribute("listaAsesoresActivos", listaAsesores);

        model.addAttribute("catalogoInmuebles",
                convertirListaInmuebles(plataforma.getArbolInmueblesPorPrecio().getInOrder()));

        if (usuarioLogueado.getRol().equals("ADMIN") || usuarioLogueado.getRol().equals("ASESOR")) {
            List<String> visitasPendientes = new ArrayList<>();
            for (int i = 0; i < plataforma.getHistorialVisitasGlobales().getSize(); i++) {
                Visita visita = plataforma.getHistorialVisitasGlobales().getData(i);
                String estado = visita.getEstadoVisita();
                if ("Pendiente".equalsIgnoreCase(estado) || "Reprogramada".equalsIgnoreCase(estado)) {
                    visitasPendientes.add(visita.getIdVisita() + " | " + visita.getCliente().getNombre()
                            + " | Inm: " + visita.getInmueble().getCodigo());
                }
            }
            model.addAttribute("listaVisitasPendientes", visitasPendientes);
        }

        String treeJson = "[]";
        if (usuarioLogueado.getRol().equals("ADMIN") || usuarioLogueado.getRol().equals("ASESOR")) {
            if (!plataforma.getArbolInmueblesPorPrecio().isEmpty())
                treeJson = generateGraphData(plataforma.getArbolInmueblesPorPrecio().root);
        }
        model.addAttribute("treeJsonData", treeJson);

        return "dashboard";
    }

    @PostMapping("/registrar-operacion")
    public String registrarOperacion(@RequestParam String idOperacion, @RequestParam String tipoOperacion,
            @RequestParam String idCliente, @RequestParam String codigoInmueble, @RequestParam double valorAcordado,
            @RequestParam double porcentajeComision, @RequestParam String idAsesor, RedirectAttributes redirectAttrs) {
        Cliente c = plataforma.buscarClientePorId(idCliente);
        Inmueble i = plataforma.buscarInmueblePorCodigo(codigoInmueble);
        Asesor a = plataforma.buscarAsesorPorId(idAsesor);
        if (i != null && !i.isDisponibilidad()) {
            redirectAttrs.addFlashAttribute("mensajeError", "El inmueble " + codigoInmueble + " ya fue negociado o no está disponible.");
            return "redirect:/";
        }
        if (c != null && i != null && a != null) {
            Operacion op = new Operacion(idOperacion, i, c, a, LocalDate.now(), tipoOperacion, valorAcordado, porcentajeComision);
            plataforma.registrarOperacion(op);
            c.registrarInmuebleNegociado(i);
            redirectAttrs.addFlashAttribute("mensajeExito", "Operación (" + tipoOperacion + ") sumada al asesor: " + a.getNombre());
        } else {
            redirectAttrs.addFlashAttribute("mensajeError", "Error: Verifique IDs.");
        }
        return "redirect:/";
    }

    @PostMapping("/agendar-visita-cliente")
    public String agendarVisitaCliente(@RequestParam String idCliente, @RequestParam String codigoInmueble,
            @RequestParam String fecha, @RequestParam String hora, RedirectAttributes redirectAttrs) {
        Cliente c = plataforma.buscarClientePorId(idCliente);
        Inmueble i = plataforma.buscarInmueblePorCodigo(codigoInmueble);
        if (c != null && i != null) {
            boolean intencionDuplicada = false;
            for (int idx = 0; idx < c.getIntenciones().getSize(); idx++) {
                if (c.getIntenciones().getData(idx).getCodigo().equals(codigoInmueble)) {
                    intencionDuplicada = true; break;
                }
            }
            if (intencionDuplicada) {
                redirectAttrs.addFlashAttribute("visitaDuplicada", codigoInmueble);
                return "redirect:/";
            }
            plataforma.registrarIntencionDeNegocio(idCliente, codigoInmueble);
            Asesor a = i.getAsesorResponsable() != null ? i.getAsesorResponsable() : plataforma.getAsesores().getData(0);
            String idVisitaGen = "V-" + (int) (Math.random() * 10000);
            plataforma.agendarVisita(new Visita(idVisitaGen, c, i, LocalDate.parse(fecha), hora, a, 1));
            redirectAttrs.addFlashAttribute("mensajeExito", "¡Excelente! Solicitud registrada y visita agendada con éxito.");
        } else
            redirectAttrs.addFlashAttribute("mensajeError", "Error al procesar la solicitud.");
        return "redirect:/";
    }

    @PostMapping("/reprogramar-visita-cliente")
    public String reprogramarVisitaCliente(@RequestParam String idCliente, @RequestParam String codigoInmueble,
            @RequestParam String nuevaFecha, @RequestParam String nuevaHora, RedirectAttributes redirectAttrs) {
        boolean found = false;
        for (int i = 0; i < plataforma.getHistorialVisitasGlobales().getSize(); i++) {
            Visita visita = plataforma.getHistorialVisitasGlobales().getData(i);
            String estado = visita.getEstadoVisita();
            if ("Pendiente".equalsIgnoreCase(estado) || "Reprogramada".equalsIgnoreCase(estado)) {
                if (visita.getCliente().getId().equals(idCliente) && visita.getInmueble().getCodigo().equals(codigoInmueble)) {
                    visita.setFecha(LocalDate.parse(nuevaFecha));
                    visita.setHora(nuevaHora);
                    visita.setEstadoVisita("Reprogramada");
                    found = true; break;
                }
            }
        }
        if (found)
            redirectAttrs.addFlashAttribute("mensajeExito", "Visita reprogramada con éxito.");
        else
            redirectAttrs.addFlashAttribute("mensajeError", "No se encontró visita activa para reprogramar.");
        return "redirect:/";
    }

    @PostMapping("/agendar-visita")
    public String agendarVisita(@RequestParam String idVisita, @RequestParam String idCliente,
            @RequestParam String codigoInmueble, @RequestParam String fecha, @RequestParam String hora,
            @RequestParam int nivelUrgencia, HttpSession session, RedirectAttributes redirectAttrs) {
        Cliente c = plataforma.buscarClientePorId(idCliente);
        Inmueble i = plataforma.buscarInmueblePorCodigo(codigoInmueble);
        Asesor a = plataforma.buscarAsesorPorId(((Usuario) session.getAttribute("usuarioLogueado")).getIdAsociado());
        if (c != null && i != null && a != null) {
            plataforma.agendarVisita(new Visita(idVisita, c, i, LocalDate.parse(fecha), hora, a, nivelUrgencia));
            redirectAttrs.addFlashAttribute("mensajeExito", "Visita " + idVisita + " encolada en Priority Queue.");
        } else
            redirectAttrs.addFlashAttribute("mensajeError", "Error al agendar. Faltan datos.");
        return "redirect:/";
    }

    @PostMapping("/confirmar-visita")
    public String confirmarVisita(@RequestParam String idVisita, RedirectAttributes redirectAttrs) {
        if (plataforma.confirmarVisita(idVisita))
            redirectAttrs.addFlashAttribute("mensajeExito", "Visita confirmada.");
        else
            redirectAttrs.addFlashAttribute("mensajeError", "Visita no encontrada. (Revise mayúsculas)");
        return "redirect:/";
    }

    @PostMapping("/cancelar-visita")
    public String cancelarVisita(@RequestParam String idVisita, RedirectAttributes redirectAttrs) {
        if (plataforma.cancelarVisita(idVisita))
            redirectAttrs.addFlashAttribute("mensajeExito", "Visita cancelada.");
        else
            redirectAttrs.addFlashAttribute("mensajeError", "Visita no encontrada.");
        return "redirect:/";
    }

    @PostMapping("/reprogramar-visita")
    public String reprogramarVisita(@RequestParam String idVisita, @RequestParam String nuevaFecha,
            @RequestParam String nuevaHora, RedirectAttributes redirectAttrs) {
        Visita v = plataforma.buscarVisitaPorId(idVisita);
        if (v != null) {
            v.setFecha(LocalDate.parse(nuevaFecha));
            v.setHora(nuevaHora);
            v.setEstadoVisita(Visita.ESTADO_REPROGRAMADA);
            redirectAttrs.addFlashAttribute("mensajeExito", "Visita reprogramada con éxito.");
        } else
            redirectAttrs.addFlashAttribute("mensajeError", "Visita no encontrada.");
        return "redirect:/";
    }

    @PostMapping("/atender-visita")
    public String atenderVisita(RedirectAttributes redirectAttrs) {
        Visita v = plataforma.atenderSiguienteVisita();
        if (v != null)
            redirectAttrs.addFlashAttribute("mensajeExito",
                    "Visita atendida: " + v.getIdVisita() + " (Cliente: " + v.getCliente().getNombre() + ")");
        else
            redirectAttrs.addFlashAttribute("mensajeInfo", "La cola VIP está vacía.");
        return "redirect:/";
    }

    @PostMapping("/marcar-favorito")
    public String marcarFavorito(@RequestParam String idCliente, @RequestParam String codigoInmueble,
            RedirectAttributes redirectAttrs) {
        Cliente c = plataforma.buscarClientePorId(idCliente);
        if (c != null) {
            for (int i = 0; i < c.getFavoritos().getSize(); i++) {
                if (c.getFavoritos().getData(i).getCodigo().equals(codigoInmueble)) {
                    redirectAttrs.addFlashAttribute("mensajeInfo", "Este inmueble ya está en tus favoritos.");
                    return "redirect:/";
                }
            }
        }
        plataforma.marcarFavorito(idCliente, codigoInmueble);
        redirectAttrs.addFlashAttribute("mensajeExito", "Añadido a favoritos.");
        return "redirect:/";
    }

    @PostMapping("/registrar-consulta")
    public String registrarConsulta(@RequestParam String idCliente, @RequestParam String codigoInmueble,
            RedirectAttributes redirectAttrs) {
        Cliente c = plataforma.buscarClientePorId(idCliente);
        if (c != null) {
            for (int i = 0; i < c.getHistorialConsultas().getSize(); i++) {
                if (c.getHistorialConsultas().getData(i).getCodigo().equals(codigoInmueble)) {
                    redirectAttrs.addFlashAttribute("mensajeInfo", "Este inmueble ya fue marcado como visto.");
                    return "redirect:/";
                }
            }
        }
        if (c != null && plataforma.buscarInmueblePorCodigo(codigoInmueble) != null) {
            plataforma.registrarConsultaInmueble(idCliente, codigoInmueble);
            redirectAttrs.addFlashAttribute("mensajeExito", "Consulta registrada en el historial.");
        } else
            redirectAttrs.addFlashAttribute("mensajeError", "Datos no encontrados.");
        return "redirect:/";
    }

    @PostMapping("/descartar-inmueble")
    public String descartarInmueble(@RequestParam String idCliente, @RequestParam String codigoInmueble,
            RedirectAttributes redirectAttrs) {
        Cliente c = plataforma.buscarClientePorId(idCliente);
        if (c != null) {
            for (int i = 0; i < c.getInmueblesDescartados().getSize(); i++) {
                if (c.getInmueblesDescartados().getData(i).getCodigo().equals(codigoInmueble)) {
                    redirectAttrs.addFlashAttribute("mensajeInfo", "Este inmueble ya está en tus descartados.");
                    return "redirect:/";
                }
            }
        }
        if (plataforma.descartarInmueble(idCliente, codigoInmueble))
            redirectAttrs.addFlashAttribute("mensajeExito", "Inmueble movido a descartados.");
        else
            redirectAttrs.addFlashAttribute("mensajeError", "Datos no encontrados.");
        return "redirect:/";
    }

    @PostMapping("/registrar-intencion")
    public String registrarIntencion(@RequestParam String idCliente, @RequestParam String codigoInmueble,
            RedirectAttributes redirectAttrs) {
        Cliente c = plataforma.buscarClientePorId(idCliente);
        if (c != null) {
            for (int i = 0; i < c.getIntenciones().getSize(); i++) {
                if (c.getIntenciones().getData(i).getCodigo().equals(codigoInmueble)) {
                    redirectAttrs.addFlashAttribute("mensajeInfo", "Atención: Ya has registrado una solicitud para este inmueble.");
                    return "redirect:/";
                }
            }
        }
        if (plataforma.registrarIntencionDeNegocio(idCliente, codigoInmueble))
            redirectAttrs.addFlashAttribute("mensajeExito", "Intención de negocio registrada.");
        else
            redirectAttrs.addFlashAttribute("mensajeError", "Error en datos.");
        return "redirect:/";
    }

    @PostMapping("/registrar-inmueble")
    public String registrarInmueble(@RequestParam String codigo, @RequestParam String direccion,
            @RequestParam String ciudad, @RequestParam String barrioZona, @RequestParam String finalidad,
            @RequestParam double precio, @RequestParam double area, @RequestParam int habitaciones,
            @RequestParam int banos, @RequestParam String estado, @RequestParam String idAsesor,
            RedirectAttributes redirectAttrs) {
        Asesor a = plataforma.buscarAsesorPorId(idAsesor);
        if (a == null) {
            redirectAttrs.addFlashAttribute("mensajeError", "Asesor no existe.");
            return "redirect:/";
        }
        if (plataforma.registrarInmueble(new Apartamento(codigo, direccion, ciudad, barrioZona, finalidad, precio, area,
                habitaciones, banos, estado, true, a, true, 0.0)))
            redirectAttrs.addFlashAttribute("mensajeExito", "Inmueble registrado.");
        else
            redirectAttrs.addFlashAttribute("mensajeError", "Código duplicado.");
        return "redirect:/";
    }

    @GetMapping("/registro")
    public String mostrarRegistro() { return "registro"; }

    @PostMapping("/registro")
    public String registrarNuevoCliente(@RequestParam String username, @RequestParam String password,
            @RequestParam String identificacion, @RequestParam String nombre, @RequestParam String correo,
            @RequestParam String telefono, @RequestParam String tipoCliente, @RequestParam double presupuesto,
            @RequestParam String tipoInmuebleDeseado, @RequestParam int minHabitaciones,
            RedirectAttributes redirectAttrs) {
        if (plataforma.buscarUsuarioPorUsername(username) != null) {
            redirectAttrs.addFlashAttribute("error", "El nombre de usuario ya existe.");
            return "redirect:/registro";
        }
        Cliente nuevoCliente = new Cliente(identificacion, nombre, correo, telefono, tipoCliente, presupuesto,
                tipoInmuebleDeseado, minHabitaciones);
        boolean clienteRegistrado = plataforma.registrarCliente(nuevoCliente);
        if (!clienteRegistrado) {
            redirectAttrs.addFlashAttribute("error", "Ya existe un cliente con esa identificación.");
            return "redirect:/registro";
        }
        Usuario nuevoUsuario = new Usuario(username, password, "CLIENTE", identificacion);
        boolean usuarioGuardado = plataforma.guardarUsuarioEnCSV(nuevoUsuario);
        if (!usuarioGuardado) {
            redirectAttrs.addFlashAttribute("mensajeError", "No se pudo guardar el usuario.");
            return "redirect:/registro";
        }
        redirectAttrs.addFlashAttribute("mensajeExito", "Registro exitoso. Ya puedes iniciar sesión.");
        return "redirect:/login";
    }

    @PostMapping("/registrar-asesor")
    public String registrarAsesor(@RequestParam String identificacion, @RequestParam String nombre,
            @RequestParam String contacto, @RequestParam String especialidad, RedirectAttributes redirectAttrs) {
        if (plataforma.registrarAsesor(new Asesor(identificacion, nombre, contacto, especialidad)))
            redirectAttrs.addFlashAttribute("mensajeExito", "Asesor registrado.");
        else
            redirectAttrs.addFlashAttribute("mensajeError", "El asesor ya existe.");
        return "redirect:/";
    }

    @PostMapping("/ver-perfil-cliente")
    public String verPerfilCliente(@RequestParam String idCliente, RedirectAttributes redirectAttrs) {
        Cliente c = plataforma.buscarClientePorId(idCliente);
        if (c == null) {
            redirectAttrs.addFlashAttribute("mensajeError", "Cliente no encontrado.");
            return "redirect:/";
        }
        redirectAttrs.addFlashAttribute("nombreClientePerfil", c.getNombre());
        redirectAttrs.addFlashAttribute("listaFavoritosObj", convertirListaInmuebles(c.getFavoritos()));
        redirectAttrs.addFlashAttribute("listaConsultasObj", convertirListaInmuebles(c.getHistorialConsultas()));
        redirectAttrs.addFlashAttribute("listaIntencionesObj", convertirListaInmuebles(c.getIntenciones()));
        redirectAttrs.addFlashAttribute("listaNegociadosObj", convertirListaInmuebles(c.getInmueblesNegociados()));
        redirectAttrs.addFlashAttribute("listaDescartadosObj", convertirListaInmuebles(c.getInmueblesDescartados()));
        redirectAttrs.addFlashAttribute("mostrarModalPerfil", true);
        return "redirect:/";
    }

    @PostMapping("/generar-reportes")
    public String generarReportes(RedirectAttributes redirectAttrs) {
        List<Asesor> listaOrdenada = new ArrayList<>();
        for (int i = 0; i < plataforma.getAsesores().getSize(); i++)
            listaOrdenada.add(plataforma.getAsesores().getData(i));
        listaOrdenada.sort((a1, a2) -> Integer.compare(a2.getNumeroCierres(), a1.getNumeroCierres()));
        List<String> lA = new ArrayList<>();
        for (Asesor a : listaOrdenada)
            lA.add("🏆 " + a.getNombre() + " | Cierres: " + a.getNumeroCierres());

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
        return "redirect:/";
    }

    @PostMapping("/atender-tarea")
    public String atenderTarea(RedirectAttributes redirectAttrs) {
        plataforma.atenderSiguienteTarea();
        return "redirect:/";
    }

    @PostMapping("/agregar-solicitud")
    public String agregarSolicitud(@RequestParam String solicitud, RedirectAttributes redirectAttrs) {
        plataforma.registrarSolicitudCliente(solicitud);
        return "redirect:/";
    }

    @PostMapping("/atender-solicitud")
    public String atenderSolicitud(RedirectAttributes redirectAttrs) {
        plataforma.atenderSiguienteSolicitud();
        return "redirect:/";
    }

    @PostMapping("/deshacer-accion")
    public String deshacerAccion(RedirectAttributes redirectAttrs) {
        String res = plataforma.extraerUltimoCambio();
        if (res != null) redirectAttrs.addFlashAttribute("mensajeExito", "Deshecho: " + res);
        return "redirect:/";
    }

    @PostMapping("/api/chat")
    @org.springframework.web.bind.annotation.ResponseBody
    public Map<String, String> procesarChatIA(@RequestParam String mensaje) {
        String respuesta = geminiService.generarRespuesta(mensaje);
        Map<String, String> response = new HashMap<>();
        response.put("respuesta", respuesta);
        return response;
    }

    @GetMapping("/api/test-gemini")
    @org.springframework.web.bind.annotation.ResponseBody
    public Map<String, String> testGemini() {
        Map<String, String> response = new HashMap<>();
        try {
            String resultado = geminiService.generarRespuesta("Hola, ¿cómo estás?");
            response.put("estado", "success");
            response.put("respuesta", resultado);
        } catch (Exception e) {
            response.put("estado", "error");
            response.put("error", e.getMessage());
        }
        return response;
    }

    private String generateGraphData(Node<Inmueble> root) {
        List<Map<String, Object>> nodeList = new ArrayList<>();
        populateJsonModel(root, nodeList, null, null);
        try { return new ObjectMapper().writeValueAsString(nodeList); }
        catch (JsonProcessingException e) { return "[]"; }
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
}
