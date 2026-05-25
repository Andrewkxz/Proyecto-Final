package co.edu.uniquindio.proptech;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import co.edu.uniquindio.proptech.BinarySearchTree.BinarySearchTree;
import co.edu.uniquindio.proptech.DoublyLinkedList.DoublyLinkedList;
import co.edu.uniquindio.proptech.Grafo.Graph;
import co.edu.uniquindio.proptech.Grafo.Vertice;
import co.edu.uniquindio.proptech.HashTable.HashTable;
import co.edu.uniquindio.proptech.LinkedSimpleList.LinkedSimpleList;
import co.edu.uniquindio.proptech.Queues.PriorityQueue;
import co.edu.uniquindio.proptech.Queues.Queue;
import co.edu.uniquindio.proptech.Stack.Stack;
import co.edu.uniquindio.proptech.service.DatabaseService;

@Service
public class Inmobiliaria {

    // --- Estructuras de datos propias ---
    private BinarySearchTree<Inmueble> arbolInmueblesPorPrecio;
    private BinarySearchTree<Cliente> arbolClientesPorPresupuesto;
    private HashTable<String, Usuario> usuariosSistema;
    private HashTable<String, Inmueble> inmueblesPorCodigo;
    private HashTable<String, Cliente> clientesPorId;
    private HashTable<String, Asesor> asesoresPorId;
    private HashTable<String, LinkedSimpleList<Inmueble>> inmueblesAgrupadosPorCiudad;
    private HashTable<String, LinkedSimpleList<Inmueble>> inmueblesAgrupadosPorTipo;
    private HashTable<String, LinkedSimpleList<Inmueble>> inmueblesAgrupadosPorEstado;
    private HashTable<String, Integer> contadorCambiosPrecio;
    private Graph<String> grafoRelaciones;
    private DoublyLinkedList<Inmueble> catalogoInmuebles;
    private LinkedSimpleList<Cliente> clientes;
    private LinkedSimpleList<Asesor> asesores;
    private LinkedSimpleList<Operacion> operacionesRealizadas;
    private LinkedSimpleList<Visita> historialVisitasGlobales;
    private Queue<String> colaTareasAdministrativas;
    private Queue<String> colaSolicitudesClientes;
    private PriorityQueue<Cliente> colaClientesVIP;
    private PriorityQueue<Visita> colaVisitasPendientes;
    private PriorityQueue<Alerta> colaAlertasSistema;
    private Stack<CambioEstado> pilaHistorialCambios;

    private DatabaseService databaseService;

    public Inmobiliaria() {
        inicializarEstructuras();
        cargarUsuariosDesdeCSV();
    }

    @Autowired
    public Inmobiliaria(DatabaseService databaseService) {
        this();
        this.databaseService = databaseService;
    }

    private void inicializarEstructuras() {
        this.arbolInmueblesPorPrecio = new BinarySearchTree<>();
        this.arbolClientesPorPresupuesto = new BinarySearchTree<>();
        this.usuariosSistema = new HashTable<>();
        this.inmueblesPorCodigo = new HashTable<>();
        this.clientesPorId = new HashTable<>();
        this.asesoresPorId = new HashTable<>();
        this.inmueblesAgrupadosPorCiudad = new HashTable<>();
        this.inmueblesAgrupadosPorTipo = new HashTable<>();
        this.inmueblesAgrupadosPorEstado = new HashTable<>();
        this.contadorCambiosPrecio = new HashTable<>();
        this.catalogoInmuebles = new DoublyLinkedList<>();
        this.clientes = new LinkedSimpleList<>();
        this.asesores = new LinkedSimpleList<>();
        this.operacionesRealizadas = new LinkedSimpleList<>();
        this.historialVisitasGlobales = new LinkedSimpleList<>();
        this.colaTareasAdministrativas = new Queue<>();
        this.colaSolicitudesClientes = new Queue<>();
        this.colaClientesVIP = new PriorityQueue<>();
        this.colaVisitasPendientes = new PriorityQueue<>();
        this.colaAlertasSistema = new PriorityQueue<>();
        this.pilaHistorialCambios = new Stack<>();
        this.grafoRelaciones = new Graph<>();
    }

    @PostConstruct
    public void initFromDatabase() {
        if (databaseService == null) return;

        for (Asesor a : databaseService.loadAsesores()) {
            asesoresPorId.put(a.getId(), a);
            asesores.addLast(a);
        }
        for (Cliente c : databaseService.loadClientes()) {
            c.postLoad();
            clientesPorId.put(c.getId(), c);
            clientes.addLast(c);
            arbolClientesPorPresupuesto.insert(c);
        }
        for (Inmueble i : databaseService.loadInmuebles()) {
            inmueblesPorCodigo.put(i.getCodigo(), i);
            catalogoInmuebles.addLast(i);
            arbolInmueblesPorPrecio.insert(i);
            String ciudad = i.getCiudad().toUpperCase();
            String tipo = i.getClass().getSimpleName().toUpperCase();
            String estado = i.getEstado().toUpperCase();
            if (inmueblesAgrupadosPorCiudad.get(ciudad) == null)
                inmueblesAgrupadosPorCiudad.put(ciudad, new LinkedSimpleList<>());
            inmueblesAgrupadosPorCiudad.get(ciudad).addLast(i);
            if (inmueblesAgrupadosPorTipo.get(tipo) == null)
                inmueblesAgrupadosPorTipo.put(tipo, new LinkedSimpleList<>());
            inmueblesAgrupadosPorTipo.get(tipo).addLast(i);
            if (inmueblesAgrupadosPorEstado.get(estado) == null)
                inmueblesAgrupadosPorEstado.put(estado, new LinkedSimpleList<>());
            inmueblesAgrupadosPorEstado.get(estado).addLast(i);
            String nodoZona = "Zona-" + i.getBarrioZona().toUpperCase();
            grafoRelaciones.addVertex(nodoZona);
            grafoRelaciones.addVertex(i.getCodigo());
            grafoRelaciones.connect(i.getCodigo(), nodoZona);
        }
        for (Usuario u : databaseService.loadUsuarios()) {
            usuariosSistema.put(u.getUsername(), u);
        }
        for (Visita v : databaseService.loadVisitas()) {
            historialVisitasGlobales.addLast(v);
        }
        for (Operacion o : databaseService.loadOperaciones()) {
            operacionesRealizadas.addLast(o);
        }
        System.out.println("Datos cargados desde la base de datos.");
    }

    private boolean hasDb() {
        return databaseService != null;
    }

    // --- Login ---
    private void cargarUsuariosDesdeCSV() {
        try {
            java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("usuarios.csv");
            if (is == null) return;
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(is));
            String linea;
            boolean esEncabezado = true;
            while ((linea = br.readLine()) != null) {
                if (esEncabezado) { esEncabezado = false; continue; }
                String[] datos = linea.split(",");
                if (datos.length == 4) {
                    Usuario u = new Usuario(datos[0].trim(), datos[1].trim(), datos[2].trim(), datos[3].trim());
                    usuariosSistema.put(u.getUsername(), u);
                }
            }
            br.close();
            System.out.println("Usuarios cargados exitosamente desde CSV.");
        } catch (Exception e) {
            System.out.println("Error leyendo el CSV: " + e.getMessage());
        }
    }

    public boolean guardarUsuarioEnCSV(Usuario usuario) {
        try {
            java.io.File archivo = new java.io.File("src/main/resources/usuarios.csv");
            if (!archivo.exists()) {
                archivo.createNewFile();
                java.io.FileWriter fwHeader = new java.io.FileWriter(archivo, true);
                fwHeader.write("username,password,rol,idAsociado\n");
                fwHeader.close();
            }
            if (usuariosSistema.containsKey(usuario.getUsername())) return false;
            usuariosSistema.put(usuario.getUsername(), usuario);
            java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(archivo, true));
            bw.newLine();
            bw.write(usuario.getUsername() + "," + usuario.getPassword() + "," + usuario.getRol() + "," + usuario.getIdAsociado());
            bw.close();
            return true;
        } catch (Exception e) {
            System.out.println("Error guardando usuario: " + e.getMessage());
            return false;
        }
    }

    public Usuario buscarUsuarioPorUsername(String username) { return usuariosSistema.get(username); }

    // --- Gestión de Inmuebles ---
    public boolean registrarInmueble(Inmueble nuevoInmueble) {
        if (inmueblesPorCodigo.containsKey(nuevoInmueble.getCodigo())) return false;
        catalogoInmuebles.addLast(nuevoInmueble);
        inmueblesPorCodigo.put(nuevoInmueble.getCodigo(), nuevoInmueble);
        arbolInmueblesPorPrecio.insert(nuevoInmueble);
        String ciudad = nuevoInmueble.getCiudad().toUpperCase();
        String tipo = nuevoInmueble.getClass().getSimpleName().toUpperCase();
        String estado = nuevoInmueble.getEstado().toUpperCase();
        if (inmueblesAgrupadosPorCiudad.get(ciudad) == null) inmueblesAgrupadosPorCiudad.put(ciudad, new LinkedSimpleList<>());
        inmueblesAgrupadosPorCiudad.get(ciudad).addLast(nuevoInmueble);
        if (inmueblesAgrupadosPorTipo.get(tipo) == null) inmueblesAgrupadosPorTipo.put(tipo, new LinkedSimpleList<>());
        inmueblesAgrupadosPorTipo.get(tipo).addLast(nuevoInmueble);
        if (inmueblesAgrupadosPorEstado.get(estado) == null) inmueblesAgrupadosPorEstado.put(estado, new LinkedSimpleList<>());
        inmueblesAgrupadosPorEstado.get(estado).addLast(nuevoInmueble);
        String nodoZona = "Zona-" + nuevoInmueble.getBarrioZona().toUpperCase();
        grafoRelaciones.addVertex(nodoZona);
        grafoRelaciones.addVertex(nuevoInmueble.getCodigo());
        grafoRelaciones.connect(nuevoInmueble.getCodigo(), nodoZona);
        pilaHistorialCambios.push(new CambioEstado(CambioEstado.TIPO_REGISTRO_INMUEBLE, nuevoInmueble,
                "Registro de inmueble: " + nuevoInmueble.getCodigo()));
        if (hasDb()) databaseService.saveInmueble(nuevoInmueble);
        return true;
    }

    public boolean modificarInmueble(String codigo, double nuevoPrecio, String nuevoEstado, boolean nuevaDisponibilidad) {
        Inmueble inmueble = inmueblesPorCodigo.get(codigo);
        if (inmueble != null) {
            pilaHistorialCambios.push(new CambioEstado(CambioEstado.TIPO_MODIFICACION_ESTADO, inmueble,
                    "Modificación de inmueble: " + codigo));
            if (inmueble.getPrecio() != nuevoPrecio) {
                arbolInmueblesPorPrecio.delete(inmueble);
                Integer cambios = contadorCambiosPrecio.get(codigo);
                contadorCambiosPrecio.put(codigo, (cambios == null) ? 1 : cambios + 1);
                inmueble.setPrecio(nuevoPrecio);
                arbolInmueblesPorPrecio.insert(inmueble);
            }
            inmueble.setEstado(nuevoEstado);
            inmueble.setDisponibilidad(nuevaDisponibilidad);
            if (hasDb()) databaseService.saveInmueble(inmueble);
            return true;
        }
        return false;
    }

    public boolean eliminarInmueble(String codigo) {
        Inmueble inmueble = inmueblesPorCodigo.get(codigo);
        if (inmueble != null) {
            pilaHistorialCambios.push(new CambioEstado(CambioEstado.TIPO_ELIMINACION_INMUEBLE, inmueble,
                    "Eliminación de inmueble: " + codigo));
            inmueblesPorCodigo.remove(codigo);
            catalogoInmuebles.removeData(inmueble);
            arbolInmueblesPorPrecio.delete(inmueble);
            String ciudad = inmueble.getCiudad().toUpperCase();
            LinkedSimpleList<Inmueble> listaCiudad = inmueblesAgrupadosPorCiudad.get(ciudad);
            if (listaCiudad != null) listaCiudad.removeData(inmueble);
            if (hasDb()) databaseService.deleteInmueble(inmueble);
            return true;
        }
        return false;
    }

    public Inmueble buscarInmueblePorCodigo(String codigo) { return inmueblesPorCodigo.get(codigo); }

    // --- Gestión de Clientes ---
    public boolean registrarCliente(Cliente nuevoCliente) {
        if (clientesPorId.containsKey(nuevoCliente.getId())) return false;
        clientesPorId.put(nuevoCliente.getId(), nuevoCliente);
        clientes.addLast(nuevoCliente);
        arbolClientesPorPresupuesto.insert(nuevoCliente);
        if (hasDb()) databaseService.saveCliente(nuevoCliente);
        return true;
    }

    public boolean modificarCliente(String idCliente, double nuevoPresupuesto) {
        Cliente cliente = clientesPorId.get(idCliente);
        if (cliente != null) {
            try { arbolClientesPorPresupuesto.delete(cliente); } catch (Exception e) {}
            cliente.setPresupuesto(nuevoPresupuesto);
            arbolClientesPorPresupuesto.insert(cliente);
            if (hasDb()) databaseService.saveCliente(cliente);
            return true;
        }
        return false;
    }

    public boolean eliminarCliente(String idCliente) {
        Cliente cliente = clientesPorId.get(idCliente);
        if (cliente != null) {
            clientesPorId.remove(idCliente);
            try { clientes.removeData(cliente); } catch (Exception e) {}
            try { arbolClientesPorPresupuesto.delete(cliente); } catch (Exception e) {}
            if (hasDb()) databaseService.deleteCliente(cliente);
            return true;
        }
        return false;
    }

    public Cliente buscarClientePorId(String idCliente) { return clientesPorId.get(idCliente); }

    // --- Gestión de Asesores ---
    public boolean registrarAsesor(Asesor nuevoAsesor) {
        if (asesoresPorId.containsKey(nuevoAsesor.getId())) return false;
        asesores.addLast(nuevoAsesor);
        asesoresPorId.put(nuevoAsesor.getId(), nuevoAsesor);
        if (hasDb()) databaseService.saveAsesor(nuevoAsesor);
        return true;
    }

    public Asesor buscarAsesorPorId(String idAsesor) { return asesoresPorId.get(idAsesor); }

    // --- Historial y Favoritos ---
    public void marcarFavorito(String idCliente, String codigoInmueble) {
        Cliente cliente = clientesPorId.get(idCliente);
        Inmueble inmueble = inmueblesPorCodigo.get(codigoInmueble);
        if (cliente != null && inmueble != null) {
            cliente.getFavoritos().addLast(inmueble);
            if (hasDb()) databaseService.saveCliente(cliente);
        }
    }

    public void registrarConsultaInmueble(String idCliente, String codigoInmueble) {
        Cliente cliente = clientesPorId.get(idCliente);
        Inmueble inmueble = inmueblesPorCodigo.get(codigoInmueble);
        if (cliente != null && inmueble != null) {
            cliente.getHistorialConsultas().addLast(inmueble);
            conectarClientesConInmuebles(idCliente, codigoInmueble);
            if (hasDb()) databaseService.saveCliente(cliente);
        }
    }

    public boolean descartarInmueble(String idCliente, String codigoInmueble) {
        Cliente cliente = clientesPorId.get(idCliente);
        Inmueble inmueble = inmueblesPorCodigo.get(codigoInmueble);
        if (cliente != null && inmueble != null) {
            cliente.descartarInmueble(inmueble);
            if (hasDb()) databaseService.saveCliente(cliente);
            return true;
        }
        return false;
    }

    public void conectarClientesConInmuebles(String idCliente, String codigoInmueble) {
        grafoRelaciones.addVertex(idCliente);
        grafoRelaciones.addVertex(codigoInmueble);
        grafoRelaciones.connect(idCliente, codigoInmueble);
    }

    // --- Visitas ---
    public void agendarVisita(Visita visita) {
        colaVisitasPendientes.offer(visita);
        historialVisitasGlobales.addLast(visita);
        if (hasDb()) databaseService.saveVisita(visita);
    }

    public Visita atenderSiguienteVisita() {
        Visita visita = null;
        do { visita = colaVisitasPendientes.poll(); }
        while (visita != null && visita.getEstadoVisita().equals(Visita.ESTADO_CANCELADA));
        if (visita != null) {
            visita.registrarRealizacion("La visita se ejecutó de forma satisfactoria. El cliente mostró gran interés.");
            pilaHistorialCambios.push(new CambioEstado(CambioEstado.TIPO_MODIFICACION_ESTADO, visita.getInmueble(),
                    "Visita realizada para cliente: " + visita.getCliente().getId()));
            if (hasDb()) databaseService.saveVisita(visita);
        }
        return visita;
    }

    public boolean confirmarVisita(String idVisita) {
        Visita visita = buscarVisitaPorId(idVisita);
        if (visita != null && visita.getEstadoVisita().equals(Visita.ESTADO_PENDIENTE)) {
            visita.confirmarVisita();
            if (hasDb()) databaseService.saveVisita(visita);
            return true;
        }
        return false;
    }

    public Visita buscarVisitaPorId(String idVisita) {
        for (int i = 0; i < historialVisitasGlobales.getSize(); i++) {
            Visita visita = historialVisitasGlobales.getData(i);
            if (visita.getIdVisita().equals(idVisita)) return visita;
        }
        return null;
    }

    public boolean cancelarVisita(String idVisita) {
        Visita visita = buscarVisitaPorId(idVisita);
        if (visita != null && !visita.getEstadoVisita().equals(Visita.ESTADO_REALIZADA)) {
            visita.cancelarVisita("Cancelada por el cliente o el asesor.");
            if (hasDb()) databaseService.saveVisita(visita);
            return true;
        }
        return false;
    }

    public boolean reprogramarVisita(String idVisita, java.time.LocalDate nuevaFecha, String nuevaHora) {
        Visita visita = buscarVisitaPorId(idVisita);
        if (visita != null && !visita.getEstadoVisita().equals(Visita.ESTADO_CANCELADA)
                && !visita.getEstadoVisita().equals(Visita.ESTADO_REALIZADA)) {
            visita.reprogramarVisita(nuevaFecha, nuevaHora);
            if (hasDb()) databaseService.saveVisita(visita);
            return true;
        }
        return false;
    }

    // --- Tareas Administrativas y Alertas ---
    public void registrarTareaAdministrativa(String descripcionTarea) {
        colaTareasAdministrativas.offer(descripcionTarea);
        pilaHistorialCambios.push(new CambioEstado(CambioEstado.TIPO_ACCION_ADMINISTRATIVA, null,
                "Nueva tarea administrativa registrada: " + descripcionTarea));
    }

    public String atenderSiguienteTarea() {
        return colaTareasAdministrativas.isEmpty() ? null : colaTareasAdministrativas.poll();
    }

    public String verSiguienteTarea() {
        return colaTareasAdministrativas.isEmpty() ? null : colaTareasAdministrativas.peek();
    }

    public void registrarAlerta(Alerta alerta) { colaAlertasSistema.offer(alerta); }

    public void revisarAlertas() {
        System.out.println("\n--- BANDEJA DE ALERTAS DEL SISTEMA ---");
        if (colaAlertasSistema.isEmpty()) {
            System.out.println("El sistema no tiene alertas pendientes.");
            return;
        }
        while (!colaAlertasSistema.isEmpty()) {
            Alerta alertaActual = colaAlertasSistema.poll();
            System.out.println("[Prioridad " + alertaActual.getNivelPrioridad() + "] " + alertaActual.getTipoAlerta()
                    + " | " + alertaActual.getMensaje());
        }
    }

    public LinkedSimpleList<String> extraerAlertas() {
        LinkedSimpleList<String> mensajesAlerta = new LinkedSimpleList<>();
        while (!colaAlertasSistema.isEmpty()) {
            Alerta alerta = colaAlertasSistema.poll();
            mensajesAlerta.addLast("[Prioridad " + alerta.getNivelPrioridad() + "] " + alerta.getTipoAlerta()
                    + " | " + alerta.getMensaje());
        }
        return mensajesAlerta;
    }

    public boolean registrarIntencionDeNegocio(String idCliente, String codigoInmueble) {
        Cliente cliente = clientesPorId.get(idCliente);
        Inmueble inmueble = inmueblesPorCodigo.get(codigoInmueble);
        if (cliente != null && inmueble != null) {
            cliente.registrarIntencion(inmueble);
            if (hasDb()) databaseService.saveCliente(cliente);
            return true;
        }
        return false;
    }

    public void detectarComportamientosInusuales() {
        java.time.LocalDate hoy = java.time.LocalDate.now();
        HashTable<String, Integer> visitasPorZona = new HashTable<>();
        LinkedSimpleList<String> zonasUnicas = new LinkedSimpleList<>();
        for (int i = 0; i < catalogoInmuebles.getSize(); i++) {
            Inmueble inm = catalogoInmuebles.getData(i);
            int numVisitas = inm.getHistorialVisitas().getSize();
            String zona = inm.getBarrioZona().toUpperCase();
            if (zonasUnicas.getIndex(zona) == -1) zonasUnicas.addLast(zona);
            Integer conteoActual = visitasPorZona.get(zona);
            visitasPorZona.put(zona, (conteoActual == null ? numVisitas : conteoActual + numVisitas));
            if (inm.isDisponibilidad()) {
                if (numVisitas >= 10)
                    registrarAlerta(new Alerta("ALERT-INM-" + inm.getCodigo(),
                            "Inmueble " + inm.getCodigo() + " estancado con " + numVisitas + " visitas sin cierre.",
                            Alerta.TIPO_COMPORTAMIENTO_INUSUAL, 8));
                else if (numVisitas >= 5)
                    registrarAlerta(new Alerta("ALT-DEM-" + inm.getCodigo(),
                            "Inmueble " + inm.getCodigo() + " tiene " + numVisitas + " visitas registradas.",
                            Alerta.TIPO_ALTA_DEMANDA, 5));
                else if (numVisitas == 0)
                    registrarAlerta(new Alerta("ALT-SVIS-" + inm.getCodigo(),
                            "Inmueble abandonado: " + inm.getCodigo() + " no tiene visitas registradas.",
                            Alerta.TIPO_INACTIVIDAD, 4));
                Integer cambiosPrecio = contadorCambiosPrecio.get(inm.getCodigo());
                if (cambiosPrecio != null && cambiosPrecio >= 3)
                    registrarAlerta(new Alerta("ALT-PRE-" + inm.getCodigo(),
                            "Flujo de precio extraño: El inmueble " + inm.getCodigo()
                                    + " ha cambiado de precio " + cambiosPrecio + " veces.",
                            Alerta.TIPO_COMPORTAMIENTO_INUSUAL, 7));
            }
        }
        for (int i = 0; i < zonasUnicas.getSize(); i++) {
            String z = zonasUnicas.getData(i);
            Integer visitasTotalesZona = visitasPorZona.get(z);
            if (visitasTotalesZona != null && visitasTotalesZona >= 15)
                registrarAlerta(new Alerta("ALT-ZON-" + z,
                        "Concentración de demanda: La zona " + z + " ha acumulado " + visitasTotalesZona + " visitas.",
                        Alerta.TIPO_ALTA_DEMANDA, 6));
        }
        for (int i = 0; i < historialVisitasGlobales.getSize(); i++) {
            Visita visita = historialVisitasGlobales.getData(i);
            if (visita.getEstadoVisita().equals(Visita.ESTADO_PENDIENTE))
                registrarAlerta(new Alerta("ALERT-VIS-" + visita.getIdVisita(),
                        "Visita " + visita.getIdVisita() + " pendiente de confirmación.", Alerta.TIPO_INACTIVIDAD, 6));
        }
        for (int i = 0; i < operacionesRealizadas.getSize(); i++) {
            Operacion op = operacionesRealizadas.getData(i);
            if (op.getTipoOperacion().equals(Operacion.TIPO_ARRIENDO)
                    && op.getEstadoProceso().equals(Operacion.ESTADO_FINALIZADO)) {
                if (op.getFecha().isBefore(hoy.minusMonths(11)))
                    registrarAlerta(new Alerta("ALT-VENC-" + op.getId(),
                            "Oportunidad de renovación: El contrato de arriendo "
                                    + op.getId() + "está próximo a vencer.", "Alerta Comercial", 9));
            }
            if (op.getEstadoProceso().equals(Operacion.ESTADO_EN_TRAMITE)) {
                if (op.getFecha().isBefore(hoy.minusDays(15)))
                    registrarAlerta(new Alerta("ALT-RES-" + op.getId(),
                            "Alerta de estancamiento: Operación " + op.getId()
                                    + "lleva más de 15 días en trámite sin finalizarse.",
                            Alerta.TIPO_COMPORTAMIENTO_INUSUAL, 7));
            }
        }
        for (int i = 0; i < clientes.getSize(); i++) {
            Cliente c = clientes.getData(i);
            if (c.getInmueblesVisitados().getSize() >= 4 && c.getInmueblesNegociados().isEmpty())
                registrarAlerta(new Alerta("ALT-CLI-" + c.getId(),
                        "Comprador atípico: El cliente " + c.getNombre() + " tiene "
                                + c.getInmueblesVisitados().getSize() + " visitas pero cero intenciones o cierres.",
                        Alerta.TIPO_COMPORTAMIENTO_INUSUAL, 9));
        }
        for (int i = 0; i < clientes.getSize(); i++) {
            Cliente c = clientes.getData(i);
            if (c.getEstadoBusqueda().equals("Activa") && c.getHistorialConsultas().isEmpty()
                    && c.getInmueblesVisitados().isEmpty())
                registrarAlerta(new Alerta("ALT-CLI-" + c.getId(),
                        "Falta seguimiento: El cliente " + c.getNombre() + " no ha tenido interacciones recientes",
                        "Alerta Operativa", 6));
        }
        for (int i = 0; i < asesores.getSize(); i++) {
            Asesor asesor = asesores.getData(i);
            if (asesor.getCargaTrabajoActiva() >= 5)
                registrarAlerta(new Alerta("ALERT-ASE" + asesor.getId(),
                        "Sobrecarga crítica: El asesor " + asesor.getNombre() + "tiene "
                                + asesor.getCargaTrabajoActiva() + " tareas pendientes.",
                        Alerta.TIPO_COMPORTAMIENTO_INUSUAL, 9));
        }
    }

    // --- Operaciones ---
    public void registrarOperacion(Operacion nuevaOperacion) {
        pilaHistorialCambios.push(new CambioEstado(CambioEstado.TIPO_MODIFICACION_ESTADO,
                nuevaOperacion.getInmuebleRelacionado(),
                "Registro de operación (" + nuevaOperacion.getTipoOperacion() + "): " + nuevaOperacion.getId()));
        operacionesRealizadas.addLast(nuevaOperacion);
        String tipo = nuevaOperacion.getTipoOperacion();
        if (tipo.equals(Operacion.TIPO_VENTA) || tipo.equals(Operacion.TIPO_ARRIENDO))
            nuevaOperacion.finalizarOperacion();
        else if (tipo.equals(Operacion.TIPO_CANCELACION))
            nuevaOperacion.revertirOperacion();
        if (nuevaOperacion.getAsesor() != null)
            nuevaOperacion.getAsesor().registrarCierre(nuevaOperacion);
        if (hasDb()) databaseService.saveOperacion(nuevaOperacion);
    }

    public void registrarSolicitudCliente(String solicitud) { colaSolicitudesClientes.offer(solicitud); }
    public String atenderSiguienteSolicitud() { return colaSolicitudesClientes.isEmpty() ? null : colaSolicitudesClientes.poll(); }
    public String verSiguienteSolicitud() { return colaSolicitudesClientes.isEmpty() ? null : colaSolicitudesClientes.peek(); }

    // --- Recomendaciones ---
    public LinkedSimpleList<Inmueble> generarRecomendaciones(String idCliente) {
        LinkedSimpleList<Inmueble> recomendacionesFinales = new LinkedSimpleList<>();
        Cliente cliente = clientesPorId.get(idCliente);
        if (cliente == null) return recomendacionesFinales;
        if (!cliente.getHistorialConsultas().isEmpty()) {
            int ultimo = cliente.getHistorialConsultas().getSize() - 1;
            Inmueble ultimoVisto = cliente.getHistorialConsultas().getData(ultimo);
            LinkedSimpleList<String> idsSugeridos = buscarInmueblesSimilaresEnGrafo(ultimoVisto.getCodigo(), idCliente);
            for (int i = 0; i < idsSugeridos.getSize(); i++) {
                String idSugerido = idsSugeridos.getData(i);
                Inmueble inmuebleRecomendado = inmueblesPorCodigo.get(idSugerido);
                if (inmuebleRecomendado != null && inmuebleRecomendado.isDisponibilidad()
                        && inmuebleRecomendado.getPrecio() <= cliente.getPresupuesto()
                        && inmuebleRecomendado.getHabitaciones() >= cliente.getMinHabitaciones())
                    recomendacionesFinales.addLast(inmuebleRecomendado);
            }
        }
        if (recomendacionesFinales.getSize() < 5) {
            double min = cliente.getPresupuesto() * 0.7;
            double max = cliente.getPresupuesto();
            LinkedSimpleList<Inmueble> inmueblesOrdenados = arbolInmueblesPorPrecio.getInOrder();
            for (Inmueble inm : inmueblesOrdenados) {
                if (inm.getPrecio() > max) break;
                if (inm.getPrecio() >= min && inm.isDisponibilidad()
                        && inm.getClass().getSimpleName().equals(cliente.getTipoInmuebleDeseado())
                        && inm.getHabitaciones() >= cliente.getMinHabitaciones()) {
                    boolean coincideZona = false;
                    if (cliente.getZonasInteres().isEmpty()) coincideZona = true;
                    else {
                        for (int j = 0; j < cliente.getZonasInteres().getSize(); j++) {
                            if (cliente.getZonasInteres().getData(j).equalsIgnoreCase(inm.getBarrioZona())) {
                                coincideZona = true; break;
                            }
                        }
                    }
                    if (coincideZona && recomendacionesFinales.getIndex(inm) == -1)
                        recomendacionesFinales.addLast(inm);
                }
            }
        }
        return recomendacionesFinales;
    }

    private LinkedSimpleList<String> buscarInmueblesSimilaresEnGrafo(String idInmuebleBase, String idClienteActual) {
        LinkedSimpleList<String> inmueblesRecomendados = new LinkedSimpleList<>();
        Vertice<String> vInmueble = grafoRelaciones.searchVertex(idInmuebleBase);
        if (vInmueble == null) return inmueblesRecomendados;
        LinkedSimpleList<Vertice<String>> clientesQueVisitaron = vInmueble.getAdyacentes();
        for (int i = 0; i < clientesQueVisitaron.getSize(); i++) {
            Vertice<String> vOtroCliente = clientesQueVisitaron.getData(i);
            if (vOtroCliente.getDato().equals(idClienteActual)) continue;
            LinkedSimpleList<Vertice<String>> inmueblesDeOtroCliente = vOtroCliente.getAdyacentes();
            for (int j = 0; j < inmueblesDeOtroCliente.getSize(); j++) {
                String idNuevoInmueble = inmueblesDeOtroCliente.getData(j).getDato();
                if (!idNuevoInmueble.equals(idInmuebleBase) && inmueblesRecomendados.getIndex(idNuevoInmueble) == -1)
                    inmueblesRecomendados.addLast(idNuevoInmueble);
            }
        }
        return inmueblesRecomendados;
    }

    public LinkedSimpleList<Asesor> generarRankingAsesores() {
        BinarySearchTree<Asesor> arbolOrdenado = new BinarySearchTree<>();
        for (int i = 0; i < asesores.getSize(); i++) arbolOrdenado.insert(asesores.getData(i));
        return arbolOrdenado.getInOrder();
    }

    public LinkedSimpleList<String> obtenerRankingZonas() {
        LinkedSimpleList<String> ranking = new LinkedSimpleList<>();
        for (int i = 0; i < catalogoInmuebles.getSize(); i++) {
            Inmueble inm = catalogoInmuebles.getData(i);
            int visitas = inm.getHistorialVisitas().getSize();
            if (visitas > 0)
                ranking.addLast(inm.getBarrioZona() + " | Inmueble: " + inm.getCodigo() + " | Visitas: " + visitas);
        }
        return ranking;
    }

    public LinkedSimpleList<Inmueble> obtenerInmueblesPorCiudad(String ciudad) {
        LinkedSimpleList<Inmueble> lista = inmueblesAgrupadosPorCiudad.get(ciudad.toUpperCase());
        return lista != null ? lista : new LinkedSimpleList<>();
    }

    public LinkedSimpleList<Inmueble> buscarInmuebleConFiltros(double precioMin, double precioMax, String zona, int minHabitaciones) {
        LinkedSimpleList<Inmueble> resultados = new LinkedSimpleList<>();
        LinkedSimpleList<Inmueble> porPrecio = arbolInmueblesPorPrecio.getInOrder();
        for (Inmueble inm : porPrecio) {
            if (inm.getPrecio() > precioMax) break;
            if (inm.getPrecio() >= precioMin && inm.getBarrioZona().equals(zona)
                    && inm.getHabitaciones() >= minHabitaciones && inm.isDisponibilidad())
                resultados.addLast(inm);
        }
        return resultados;
    }

    public String simularCrecimientoDemanda(String zona) {
        int visitasActuales = 0, totalInmueblesZona = 0;
        for (int i = 0; i < catalogoInmuebles.getSize(); i++) {
            Inmueble inm = catalogoInmuebles.getData(i);
            if (inm.getBarrioZona().equalsIgnoreCase(zona)) {
                visitasActuales += inm.getHistorialVisitas().getSize();
                totalInmueblesZona++;
            }
        }
        if (totalInmueblesZona > 0) {
            double promedio = (double) visitasActuales / totalInmueblesZona;
            double proyeccionCrecimiento = promedio > 5 ? 15.0 : 5.0;
            return "Zona " + zona.toUpperCase() + " -> Promedio de visitas: " + String.format("%.1f", promedio)
                    + " | Crecimiento proyectado el proximo mes: +" + proyeccionCrecimiento + "%";
        }
        return "No hay datos suficientes para proyectar el crecimiento en la zona: " + zona;
    }

    public LinkedSimpleList<Cliente> detectarClientesAltaPrioridad() {
        LinkedSimpleList<Cliente> clientesPrioridad = new LinkedSimpleList<>();
        colaClientesVIP = new PriorityQueue<>();
        for (int i = 0; i < clientes.getSize(); i++) {
            Cliente cliente = clientes.getData(i);
            if (cliente.getEstadoBusqueda().equals("Activa") && cliente.getFavoritos().getSize() >= 3
                    && cliente.getHistorialConsultas().getSize() >= 3) {
                clientesPrioridad.addLast(cliente);
                colaClientesVIP.offer(cliente);
            }
        }
        return clientesPrioridad;
    }

    // --- Historial (Pila) ---
    public String obtenerUltimaAccion() {
        return pilaHistorialCambios.isEmpty() ? null : pilaHistorialCambios.peek().getDescripcionAccion();
    }

    public String extraerUltimoCambio() {
        if (!pilaHistorialCambios.isEmpty()) {
            CambioEstado ultimo = pilaHistorialCambios.pop();
            if (ultimo.getTipoCambio() == CambioEstado.TIPO_MODIFICACION_ESTADO) {
                Inmueble inm = (Inmueble) ultimo.getEntidad();
                if (inm.getPrecio() != ultimo.getPrecioAnterior()) {
                    arbolInmueblesPorPrecio.delete(inm);
                    inm.setPrecio(ultimo.getPrecioAnterior());
                    arbolInmueblesPorPrecio.insert(inm);
                }
                inm.setEstado(ultimo.getEstadoAnterior());
                inm.setDisponibilidad(ultimo.isDisponibilidadAnterior());
                if (hasDb()) databaseService.saveInmueble(inm);
            } else if (ultimo.getTipoCambio() == CambioEstado.TIPO_REGISTRO_INMUEBLE) {
                Inmueble inm = (Inmueble) ultimo.getEntidad();
                arbolInmueblesPorPrecio.delete(inm);
                inmueblesPorCodigo.remove(inm.getCodigo());
                catalogoInmuebles.removeData(inm);
                LinkedSimpleList<Inmueble> lista = inmueblesAgrupadosPorCiudad.get(inm.getCiudad().toUpperCase());
                if (lista != null) lista.removeData(inm);
                if (hasDb()) databaseService.deleteInmueble(inm);
            } else if (ultimo.getTipoCambio() == CambioEstado.TIPO_ELIMINACION_INMUEBLE) {
                Inmueble inm = (Inmueble) ultimo.getEntidad();
                catalogoInmuebles.addLast(inm);
                inmueblesPorCodigo.put(inm.getCodigo(), inm);
                arbolInmueblesPorPrecio.insert(inm);
                String ciudad = inm.getCiudad().toUpperCase();
                if (inmueblesAgrupadosPorCiudad.get(ciudad) == null)
                    inmueblesAgrupadosPorCiudad.put(ciudad, new LinkedSimpleList<>());
                inmueblesAgrupadosPorCiudad.get(ciudad).addLast(inm);
                if (hasDb()) databaseService.saveInmueble(inm);
            }
            return ultimo.getDescripcionAccion();
        }
        return null;
    }

    // --- Getters para el controlador ---
    public BinarySearchTree<Inmueble> getArbolInmueblesPorPrecio() { return arbolInmueblesPorPrecio; }
    public LinkedSimpleList<Asesor> getAsesores() { return asesores; }
    public LinkedSimpleList<Cliente> getClientes() { return clientes; }
    public LinkedSimpleList<Visita> getHistorialVisitasGlobales() { return historialVisitasGlobales; }
    public LinkedSimpleList<Operacion> getOperacionesRealizadas() { return operacionesRealizadas; }
    public LinkedSimpleList<String> obtenerResumenZonas() { return obtenerRankingZonas(); }
}
