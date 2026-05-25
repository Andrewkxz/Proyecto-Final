package co.edu.uniquindio.proptech;

import co.edu.uniquindio.proptech.BinarySearchTree.BinarySearchTree;
import co.edu.uniquindio.proptech.DoublyLinkedList.DoublyLinkedList;
import co.edu.uniquindio.proptech.Grafo.Graph;
import co.edu.uniquindio.proptech.Grafo.Vertice;
import co.edu.uniquindio.proptech.HashTable.HashTable;
import co.edu.uniquindio.proptech.LinkedSimpleList.LinkedSimpleList;
import co.edu.uniquindio.proptech.Queues.PriorityQueue;
import co.edu.uniquindio.proptech.Queues.Queue;
import co.edu.uniquindio.proptech.Stack.Stack;

/**
 * Clase principal del sistema de gestión inmobiliaria PropTech.
 * Administra todas las estructuras de datos propias y la lógica de la
 * plataforma.
 * 
 * @author Juan Jose Carvajal, Juliana Andrea Bustamante Niño y Jaider Andrés
 *         Melo Rodríguez
 */
public class Inmobiliaria {
    // -------------------------------------------------------------------------------------
    // ESTRUCTURAS DE DATOS PROPIAS
    // -------------------------------------------------------------------------------------

    // --- Árboles binarios de Búsqueda (ABB) ---
    private BinarySearchTree<Inmueble> arbolInmueblesPorPrecio;
    private BinarySearchTree<Cliente> arbolClientesPorPresupuesto;

    // --- Tablas Hash ---
    private HashTable<String, Usuario> usuariosSistema;
    private HashTable<String, Inmueble> inmueblesPorCodigo;
    private HashTable<String, Cliente> clientesPorId;
    private HashTable<String, Asesor> asesoresPorId;
    private HashTable<String, LinkedSimpleList<Inmueble>> inmueblesAgrupadosPorCiudad;
    private HashTable<String, LinkedSimpleList<Inmueble>> inmueblesAgrupadosPorTipo;
    private HashTable<String, LinkedSimpleList<Inmueble>> inmueblesAgrupadosPorEstado;
    private HashTable<String, Integer> contadorCambiosPrecio;

    // --- Grafos ---
    private Graph<String> grafoRelaciones;

    // --- Listas Enlazadas (Simples y Dobles) ---
    private DoublyLinkedList<Inmueble> catalogoInmuebles;
    private LinkedSimpleList<Cliente> clientes;
    private LinkedSimpleList<Asesor> asesores;
    private LinkedSimpleList<Operacion> operacionesRealizadas;
    private LinkedSimpleList<Visita> historialVisitasGlobales;

    // --- Colas ---
    private Queue<String> colaTareasAdministrativas;
    private Queue<String> colaSolicitudesClientes;
    private PriorityQueue<Cliente> colaClientesVIP;
    private PriorityQueue<Visita> colaVisitasPendientes;
    private PriorityQueue<Alerta> colaAlertasSistema;

    // --- Pilas ---
    private Stack<CambioEstado> pilaHistorialCambios;

    // -------------------------------------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------------------------------------
    /**
     * Inicializa todas las estructuras de datos vacías al arrancar el sistema.
     */
    public Inmobiliaria() {
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

        cargarUsuariosDesdeCSV(); // Carga automáticamente al iniciar
    }

    // -------------------------------------------------------------------------------------
    // LOGIN
    // -------------------------------------------------------------------------------------
    private void cargarUsuariosDesdeCSV() {
        try {
            java.io.InputStream is = getClass().getClassLoader().getResourceAsStream("usuarios.csv");

            if (is == null) {
                System.out.println("Archivo usuarios.csv no encontrado.");
                return;
            }

            java.io.BufferedReader br = new java.io.BufferedReader(
                    new java.io.InputStreamReader(is));

            String linea;
            boolean esEncabezado = true;

            while ((linea = br.readLine()) != null) {
                if (esEncabezado) {
                    esEncabezado = false;
                    continue;
                }

                String[] datos = linea.split(",");

                if (datos.length == 4) {
                    Usuario u = new Usuario(
                            datos[0].trim(),
                            datos[1].trim(),
                            datos[2].trim(),
                            datos[3].trim());

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

            if (usuariosSistema.containsKey(usuario.getUsername())) {
                return false;
            }

            usuariosSistema.put(usuario.getUsername(), usuario);

            java.io.BufferedWriter bw = new java.io.BufferedWriter(
                    new java.io.FileWriter(archivo, true));

            bw.newLine();
            bw.write(usuario.getUsername() + "," +
                    usuario.getPassword() + "," +
                    usuario.getRol() + "," +
                    usuario.getIdAsociado());

            bw.close();

            return true;

        } catch (Exception e) {
            System.out.println("Error guardando usuario: " + e.getMessage());
            return false;
        }
    }

    public Usuario buscarUsuarioPorUsername(String username) {
        return usuariosSistema.get(username);
    }
    // -------------------------------------------------------------------------------------
    // GESTIÓN DE INMUEBLES (CRUD)
    // -------------------------------------------------------------------------------------

    /**
     * Registra un inmueble en la Lista, Tabla Hash, Árbol y Agrupación por ciudad.
     * 
     * @param nuevoInmueble El inmueble a registrar en la inmobiliaria.
     */
    public boolean registrarInmueble(Inmueble nuevoInmueble) {
        if (inmueblesPorCodigo.containsKey(nuevoInmueble.getCodigo()))
            return false;

        catalogoInmuebles.addLast(nuevoInmueble);
        inmueblesPorCodigo.put(nuevoInmueble.getCodigo(), nuevoInmueble);
        arbolInmueblesPorPrecio.insert(nuevoInmueble);

        String ciudad = nuevoInmueble.getCiudad().toUpperCase();
        String tipo = nuevoInmueble.getClass().getSimpleName().toUpperCase();
        String estado = nuevoInmueble.getEstado().toUpperCase();

        if (inmueblesAgrupadosPorCiudad.get(ciudad) == null)
            inmueblesAgrupadosPorCiudad.put(ciudad, new LinkedSimpleList<>());
        inmueblesAgrupadosPorCiudad.get(ciudad).addLast(nuevoInmueble);

        if (inmueblesAgrupadosPorTipo.get(tipo) == null)
            inmueblesAgrupadosPorTipo.put(tipo, new LinkedSimpleList<>());
        inmueblesAgrupadosPorTipo.get(tipo).addLast(nuevoInmueble);

        if (inmueblesAgrupadosPorEstado.get(estado) == null)
            inmueblesAgrupadosPorEstado.put(estado, new LinkedSimpleList<>());
        inmueblesAgrupadosPorEstado.get(estado).addLast(nuevoInmueble);

        String nodoZona = "Zona-" + nuevoInmueble.getBarrioZona().toUpperCase();
        grafoRelaciones.addVertex(nodoZona);
        grafoRelaciones.addVertex(nuevoInmueble.getCodigo());
        grafoRelaciones.connect(nuevoInmueble.getCodigo(), nodoZona);

        pilaHistorialCambios.push(new CambioEstado(CambioEstado.TIPO_REGISTRO_INMUEBLE, nuevoInmueble,
                "Registro de inmueble: " + nuevoInmueble.getCodigo()));
        return true;
    }

    /**
     * Modifica el precio, estado y disponibilidad de un inmueble. Reordena el Árbol
     * si el precio cambia.
     * 
     * @param codigo              código único del inmueble a modificar.
     * @param nuevoPrecio         nuevo precio del inmueble. Si no cambia, se puede
     *                            pasar el mismo valor.
     * @param nuevoEstado         nuevo estado del inmueble (Ej: "Disponible", "En
     *                            negociación", "Vendido"). Si no cambia, se puede
     *                            pasar el mismo valor.
     * @param nuevaDisponibilidad nueva disponibilidad del inmueble (true para
     *                            disponible, false para no disponible). Si no
     *                            cambia, se puede pasar el mismo valor.
     * @return true si la modificación fue exitosa, false si no se encontró el
     *         inmueble con el código dado.
     */
    public boolean modificarInmueble(String codigo, double nuevoPrecio, String nuevoEstado,
            boolean nuevaDisponibilidad) {
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
            return true;
        }
        return false;
    }

    /**
     * Elimina un inmueble del sistema, removiéndolo de todas las estructuras de
     * datos donde esté registrado.
     * 
     * @param codigo código único del inmueble a eliminar.
     * @return true si la eliminación fue exitosa, false si no se encontró el
     *         inmueble con el código dado.
     */
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
            if (listaCiudad != null) {
                listaCiudad.removeData(inmueble);
            }
            return true;
        }
        return false;
    }

    /**
     * Busca un inmueble en 0(1) usando la Tabla Hash por su código único.
     * 
     * @param codigo código único del inmueble a buscar.
     * @return el inmueble encontrado, o null si no se encontró ningún inmueble con
     *         el código dado.
     */
    public Inmueble buscarInmueblePorCodigo(String codigo) {
        return inmueblesPorCodigo.get(codigo);
    }

    // -------------------------------------------------------------------------------------
    // GESTIÓN DE CLIENTES (CRUD)
    // -------------------------------------------------------------------------------------

    /**
     * Registra un cliente en la Tabla Hash, Lista y Árbol de presupuestos.
     * 
     * @param nuevoCliente el cliente a registrar en la inmobiliaria.
     */
    public boolean registrarCliente(Cliente nuevoCliente) {
        if (clientesPorId.containsKey(nuevoCliente.getId()))
            return false;

        clientesPorId.put(nuevoCliente.getId(), nuevoCliente);
        clientes.addLast(nuevoCliente);
        arbolClientesPorPresupuesto.insert(nuevoCliente);
        return true;
    }

    /**
     * Modifica el presupuesto de un cliente en la inmobiliaria y actualiza el Árbol
     * Binario.
     * 
     * @param idCliente        el ID del cliente a modificar.
     * @param nuevoPresupuesto el nuevo presupuesto del cliente.
     * @return true si la modificación fue exitosa, false si no se encontró el
     *         cliente con el ID dado.
     */
    public boolean modificarCliente(String idCliente, double nuevoPresupuesto) {
        Cliente cliente = clientesPorId.get(idCliente);
        if (cliente != null) {
            try {
                arbolClientesPorPresupuesto.delete(cliente);
            } catch (Exception e) {
                cliente.setPresupuesto(nuevoPresupuesto);
                arbolClientesPorPresupuesto.insert(cliente);
                return true;
            }
        }
        return false;
    }

    /**
     * Elimina un cliente del sistema, removiendo su registro de todas las
     * estructuras de datos donde esté registrado.
     * 
     * @param idCliente el ID del cliente a eliminar.
     * @return true si la eliminación fue exitosa, false si no se encontró el
     *         cliente con el ID dado.
     */
    public boolean eliminarCliente(String idCliente) {
        Cliente cliente = clientesPorId.get(idCliente);
        if (cliente != null) {
            clientesPorId.remove(idCliente);
            try {
                clientes.removeData(cliente);
            } catch (Exception e) {
            }

            try {
                arbolClientesPorPresupuesto.delete(cliente);
            } catch (Exception e) {
                System.out.println("Error al eliminar cliente del árbol de presupuestos: " + e.getMessage());
            }
            return true;
        }
        return false;
    }

    /**
     * Busca un cliente en 0(1) usando la Tabla Hash por su ID único.
     * 
     * @param idCliente el ID del cliente a buscar.
     * @return el cliente encontrado, o null si no se encontró ningún cliente con el
     *         ID dado.
     */
    public Cliente buscarClientePorId(String idCliente) {
        return clientesPorId.get(idCliente);
    }

    // -------------------------------------------------------------------------------------
    // GESTIÓN DE ASESORES
    // -------------------------------------------------------------------------------------

    /**
     * Registra un asesor en la Tabla Hash y en la Lista de asesores.
     * 
     * @param nuevoAsesor el asesor a registrar en la inmobiliaria.
     */
    public boolean registrarAsesor(Asesor nuevoAsesor) {
        if (asesoresPorId.containsKey(nuevoAsesor.getId()))
            return false;

        asesores.addLast(nuevoAsesor);
        asesoresPorId.put(nuevoAsesor.getId(), nuevoAsesor);
        return true;
    }

    /**
     * Busca un asesor rápidamente por su ID. 0(1)
     * 
     * @param idAsesor el ID del asesor a buscar.
     * @return el asesor encontrado, o null si no se encontró ningún asesor con el
     *         ID dado.
     */
    public Asesor buscarAsesorPorId(String idAsesor) {
        return asesoresPorId.get(idAsesor);
    }

    // -------------------------------------------------------------------------------------
    // HISTORIAL DE INTERACCIÓN Y FAVORITOS DE CLIENTES
    // -------------------------------------------------------------------------------------

    /**
     * Añade un inmueble a la lista de favoritos de un cliente.
     * 
     * @param idCliente      el ID del cliente que marca el inmueble como favorito.
     * @param codigoInmueble el código del inmueble que se marca como favorito.
     */
    public void marcarFavorito(String idCliente, String codigoInmueble) {
        Cliente cliente = clientesPorId.get(idCliente);
        Inmueble inmueble = inmueblesPorCodigo.get(codigoInmueble);

        if (cliente != null && inmueble != null) {
            cliente.getFavoritos().addLast(inmueble);
        }
    }

    /**
     * Registra una consulta y genera una arista en el Grafo conectando al Cliente
     * con el Inmueble.
     * 
     * @param idCliente      el ID del cliente que realizó la consulta.
     * @param codigoInmueble el código del inmueble que fue consultado por el
     *                       cliente.
     */
    public void registrarConsultaInmueble(String idCliente, String codigoInmueble) {
        Cliente cliente = clientesPorId.get(idCliente);
        Inmueble inmueble = inmueblesPorCodigo.get(codigoInmueble);

        if (cliente != null && inmueble != null) {
            cliente.getHistorialConsultas().addLast(inmueble);
            conectarClientesConInmuebles(idCliente, codigoInmueble);
        }
    }

    public boolean descartarInmueble(String idCliente, String codigoInmueble) {
        Cliente cliente = clientesPorId.get(idCliente);
        Inmueble inmueble = inmueblesPorCodigo.get(codigoInmueble);

        if (cliente != null && inmueble != null) {
            cliente.descartarInmueble(inmueble);
            return true;
        }
        return false;
    }

    /**
     * Conecta un cliente con un inmueble en el grafo de relaciones, indicando que
     * el cliente ha interactuado con ese inmueble.
     * 
     * @param idCliente      el ID del cliente que se conectará con el inmueble.
     * @param codigoInmueble el código del inmueble con el que se conectará el
     *                       cliente.
     */
    public void conectarClientesConInmuebles(String idCliente, String codigoInmueble) {
        grafoRelaciones.addVertex(idCliente);
        grafoRelaciones.addVertex(codigoInmueble);

        grafoRelaciones.connect(idCliente, codigoInmueble);
    }

    // -------------------------------------------------------------------------------------
    // GESTIÓN DE VISITAS (COLAS DE PRIORIDAD)
    // -------------------------------------------------------------------------------------

    /**
     * Encola una visita usando su nivel de urgencia y la guarda en el historial
     * global de visitas.
     * 
     * @param visita la visita a agendar en la cola de visitas pendientes.
     */
    public void agendarVisita(Visita visita) {
        colaVisitasPendientes.offer(visita);
        historialVisitasGlobales.addLast(visita);
    }

    /**
     * Desencola la visita de mayor urgencia, saltándose aquellas que hayan sido
     * canceladas, y la marca como realizada.
     * 
     * @return la visita atendida, o null si no hay visitas pendientes por atender.
     */
    public Visita atenderSiguienteVisita() {
        Visita visita = null;
        do {
            visita = colaVisitasPendientes.poll();
        } while (visita != null && visita.getEstadoVisita().equals(Visita.ESTADO_CANCELADA));
        if (visita != null) {
            visita.registrarRealizacion("La visita se ejecutó de forma satisfactoria. El cliente mostró gran interés.");
            pilaHistorialCambios.push(new CambioEstado(CambioEstado.TIPO_MODIFICACION_ESTADO, visita.getInmueble(),
                    "Visita realizada para cliente: " + visita.getCliente().getId()));
        }
        return visita;
    }

    /**
     * Cambia el estado de una visita de Pendiente a Confirmada.
     * 
     * @param idVisita ID único de la visita.
     * @return true si se confirmó, false si no existía o no estaba pendiente.
     */
    public boolean confirmarVisita(String idVisita) {
        Visita visita = buscarVisitaPorId(idVisita);
        if (visita != null && visita.getEstadoVisita().equals(Visita.ESTADO_PENDIENTE)) {
            visita.confirmarVisita();
            return true;
        }
        return false;
    }

    /**
     * Busca una visita específica en la lista global de visitas usando su ID.
     * 
     * @param idVisita el ID de la visita a buscar.
     * @return la visita encontrada, o null si no se encontró ninguna visita con el
     *         ID dado.
     */
    public Visita buscarVisitaPorId(String idVisita) {
        for (int i = 0; i < historialVisitasGlobales.getSize(); i++) {
            Visita visita = historialVisitasGlobales.getData(i);
            if (visita.getIdVisita().equals(idVisita)) {
                return visita;
            }
        }
        return null;
    }

    /**
     * Cambia el estado de una visita a cancelada, siempre y cuando no haya sido
     * realizada.
     * 
     * @param idVisita el ID de la visita a cancelar.
     * @return true si la visita fue cancelada exitosamente, false si no se encontró
     *         la visita o si ya había sido realizada.
     */
    public boolean cancelarVisita(String idVisita) {
        Visita visita = buscarVisitaPorId(idVisita);
        if (visita != null && !visita.getEstadoVisita().equals(Visita.ESTADO_REALIZADA)) {
            visita.cancelarVisita("Cancelada por el cliente o el asesor.");
            return true;
        }
        return false;
    }

    /**
     * Reprograma fecha y hora de una visita existente, siempre y cuando no haya
     * sido cancelada o realizada.
     * 
     * @param idVisita   el ID de la visita a reprogramar.
     * @param nuevaFecha la nueva fecha para la visita reprogramada.
     * @param nuevaHora  la nueva hora para la visita reprogramada.
     * @return true si la visita fue reprogramada exitosamente, false si no se
     *         encontró la visita o si ya había sido cancelada o realizada.
     */
    public boolean reprogramarVisita(String idVisita, java.time.LocalDate nuevaFecha, String nuevaHora) {
        Visita visita = buscarVisitaPorId(idVisita);
        if (visita != null && !visita.getEstadoVisita().equals(Visita.ESTADO_CANCELADA)
                && !visita.getEstadoVisita().equals(Visita.ESTADO_REALIZADA)) {
            visita.reprogramarVisita(nuevaFecha, nuevaHora);
            return true;
        }
        return false;
    }

    // -------------------------------------------------------------------------------------
    // TAREAS ADMINISTRATIVAS Y ALERTAS(COLAS)
    // -------------------------------------------------------------------------------------

    /**
     * Encola una tarea administrativa en una Cola FIFO.
     * 
     * @param descipcionTarea la descripción de la tarea administrativa a registrar
     *                        en la cola de tareas. Ej: "Revisar contrato del
     *                        cliente X", "Llamar al cliente Y para seguimiento",
     *                        etc.
     */
    public void registrarTareaAdministrativa(String descripcionTarea) {
        colaTareasAdministrativas.offer(descripcionTarea);
        pilaHistorialCambios.push(new CambioEstado(CambioEstado.TIPO_ACCION_ADMINISTRATIVA, null,
                "Nueva tarea administrativa registrada: " + descripcionTarea));
    }

    /**
     * Desencola la siguiente tarea administrativa a resolver.
     * 
     * @return la descripción de la tarea administrativa atendida, o null si no hay
     *         tareas pendientes en la cola.
     */
    public String atenderSiguienteTarea() {
        if (!colaTareasAdministrativas.isEmpty()) {
            return colaTareasAdministrativas.poll();
        }
        return null;
    }

    /**
     * Observa la próxima tarea sin sacarla de la cola, para que el equipo
     * administrativo pueda planificar su agenda.
     * 
     * @return la descripción de la próxima tarea administrativa, o null si no hay
     *         tareas pendientes en la cola.
     */
    public String verSiguienteTarea() {
        if (!colaTareasAdministrativas.isEmpty()) {
            return colaTareasAdministrativas.peek();
        }
        return null;
    }

    /**
     * Registra una alerta en la Cola de Prioridad de Alertas del sistema.
     * 
     * @param alerta la alerta a registrar en el sistema, con su nivel de prioridad,
     *               tipo de alerta y mensaje descriptivo. Ej: "Alerta de pago
     *               atrasado para cliente X", "Alerta de inmueble con baja
     *               disponibilidad en zona Y", etc.
     */
    public void registrarAlerta(Alerta alerta) {
        colaAlertasSistema.offer(alerta);
    }

    /**
     * Imprime las alertas ordenadas por prioridad.
     */
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

    /**
     * Extrae todas las alertas pendiente en la Cola de Prioridad de Alertas del
     * sistema, mostrándolas en orden de prioridad.
     * 
     * @return una lista con los mensajes de alerta extraídos de la cola, ordenados
     *         por prioridad (de mayor a menor).
     */
    public LinkedSimpleList<String> extraerAlertas() {
        LinkedSimpleList<String> mensajesAlerta = new LinkedSimpleList<>();

        while (!colaAlertasSistema.isEmpty()) {
            Alerta alerta = colaAlertasSistema.poll();

            String mensaje = "[Prioridad " + alerta.getNivelPrioridad() + "] " + alerta.getTipoAlerta() + " | "
                    + alerta.getMensaje();
            mensajesAlerta.addLast(mensaje);
        }
        return mensajesAlerta;
    }

    /**
     * Registra la intención de compra o arriendo de un cliente sobre un inmueble.
     */
    public boolean registrarIntencionDeNegocio(String idCliente, String codigoInmueble) {
        Cliente cliente = clientesPorId.get(idCliente);
        Inmueble inmueble = inmueblesPorCodigo.get(codigoInmueble);

        if (cliente != null && inmueble != null) {
            cliente.registrarIntencion(inmueble);
            return true;
        }
        return false;
    }

    /**
     * Analiza las estructuras para detectar comportamientos inusuales y generar
     * alertas automáticas, como inmuebles con muchas visitas sin cierre, asesores
     * con sobrecarga de tareas, etc.
     */
    public void detectarComportamientosInusuales() {
        java.time.LocalDate hoy = java.time.LocalDate.now();
        // Hash Table temporal para agrupar las visitas por zona
        HashTable<String, Integer> visitasPorZona = new HashTable<>();
        LinkedSimpleList<String> zonasUnicas = new LinkedSimpleList<>(); // Para iterar el Hash luego

        // Análisis de inmuebles (Alta demanda, Estancados, Sin visitas)
        for (int i = 0; i < catalogoInmuebles.getSize(); i++) {
            Inmueble inm = catalogoInmuebles.getData(i);
            int numVisitas = inm.getHistorialVisitas().getSize();
            String zona = inm.getBarrioZona().toUpperCase();

            // Llenamos el Hash de zonas para evaluar la concentración de interés
            if (zonasUnicas.getIndex(zona) == -1)
                zonasUnicas.addLast(zona);
            Integer conteoActual = visitasPorZona.get(zona);
            visitasPorZona.put(zona, (conteoActual == null ? numVisitas : conteoActual + numVisitas));

            if (inm.isDisponibilidad()) {
                // Inmuebles con muchas vistas sin cierre
                if (numVisitas >= 10) {
                    registrarAlerta(new Alerta("ALERT-INM-" + inm.getCodigo(),
                            "Inmueble " + inm.getCodigo() + " estancado con " + numVisitas + " visitas sin cierre.",
                            Alerta.TIPO_COMPORTAMIENTO_INUSUAL, 8));
                    // Inmuebles con alta demanda
                } else if (numVisitas >= 5) {
                    registrarAlerta(new Alerta("ALT-DEM-" + inm.getCodigo(),
                            "Inmueble " + inm.getCodigo() + " tiene " + numVisitas + " visitas registradas.",
                            Alerta.TIPO_ALTA_DEMANDA, 5));
                    // Inmuebles sin visitas en mucho tiempo
                } else if (numVisitas == 0) {
                    registrarAlerta(new Alerta("ALT-SVIS-" + inm.getCodigo(),
                            "Inmueble abandonado: " + inm.getCodigo() + " no tiene visitas registradas.",
                            Alerta.TIPO_INACTIVIDAD, 4));
                }
                // Inmuebles cuyo precio cambia con demasiada frecuencia
                Integer cambiosPrecio = contadorCambiosPrecio.get(inm.getCodigo());
                if (cambiosPrecio != null && cambiosPrecio >= 3) {
                    registrarAlerta(new Alerta(
                            "ALT-PRE-" + inm.getCodigo(), "Flujo de precio extraño: El inmueble " + inm.getCodigo()
                                    + " ha cambiado de precio " + cambiosPrecio + " veces.",
                            Alerta.TIPO_COMPORTAMIENTO_INUSUAL, 7));
                }
            }
        }

        // Concentración de interés en una misma zona
        for (int i = 0; i < zonasUnicas.getSize(); i++) {
            String z = zonasUnicas.getData(i);
            Integer visitasTotalesZona = visitasPorZona.get(z);
            // Si una zona acumula más de 15 visitas globales, es una concentración de
            // interés
            if (visitasTotalesZona != null && visitasTotalesZona >= 15) {
                registrarAlerta(new Alerta("ALT-ZON-" + z,
                        "Concentración de demanda: La zona " + z + " ha acumulado " + visitasTotalesZona + " visitas.",
                        Alerta.TIPO_ALTA_DEMANDA, 6));
            }
        }

        // Análisis de visitas (Pendientes por confirmar)
        for (int i = 0; i < historialVisitasGlobales.getSize(); i++) {
            Visita visita = historialVisitasGlobales.getData(i);
            // Visitas pendientes por confirmar
            if (visita.getEstadoVisita().equals(Visita.ESTADO_PENDIENTE)) {
                registrarAlerta(new Alerta("ALERT-VIS-" + visita.getIdVisita(),
                        "Visita " + visita.getIdVisita() + " pendiente de confirmación.", Alerta.TIPO_INACTIVIDAD, 6));
            }
        }

        // Análisis de operaciones (Contratos por vencer y Reservas estancadas)
        for (int i = 0; i < operacionesRealizadas.getSize(); i++) {
            Operacion op = operacionesRealizadas.getData(i);

            // Contratos próximos a vender (arriendos con más de 11 meses de antigüedad)
            if (op.getTipoOperacion().equals(Operacion.TIPO_ARRIENDO)
                    && op.getEstadoProceso().equals(Operacion.ESTADO_FINALIZADO)) {
                if (op.getFecha().isBefore(hoy.minusMonths(11))) {
                    registrarAlerta(
                            new Alerta("ALT-VENC-" + op.getId(), "Oportunidad de renovación: El contrato de arriendo "
                                    + op.getId() + "está próximo a vencer.", "Alerta Comercial", 9));
                }
            }

            // Inmuebles reservados por mucho tiempo sin cierre (Operaciones "En trámite" de
            // más de 15 días)
            if (op.getEstadoProceso().equals(Operacion.ESTADO_EN_TRAMITE)) {
                if (op.getFecha().isBefore(hoy.minusDays(15))) {
                    registrarAlerta(new Alerta("ALT-RES-" + op.getId(),
                            "Alerta de estancamiento: Operación " + op.getId()
                                    + "lleva más de 15 días en trámite sin finalizarse.",
                            Alerta.TIPO_COMPORTAMIENTO_INUSUAL, 7));
                }
            }
        }

        // Análisis de clientes con múltiples visitas en corto tiempo sin cierre
        for (int i = 0; i < clientes.getSize(); i++) {
            Cliente c = clientes.getData(i);
            if (c.getInmueblesVisitados().getSize() >= 4 && c.getInmueblesNegociados().isEmpty()) {
                registrarAlerta(new Alerta("ALT-CLI-" + c.getId(),
                        "Comprador atípico: El cliente " + c.getNombre() + " tiene "
                                + c.getInmueblesVisitados().getSize() + " visitas pero cero intenciones o cierres.",
                        Alerta.TIPO_COMPORTAMIENTO_INUSUAL, 9));
            }
        }

        // Análisis de clientes (sin seguimiento)
        for (int i = 0; i < clientes.getSize(); i++) {
            Cliente c = clientes.getData(i);
            // Clientes sin seguimiento reciente (Buscan casa activamente pero no tienen
            // interacciones)
            if (c.getEstadoBusqueda().equals("Activa") && c.getHistorialConsultas().isEmpty()
                    && c.getInmueblesVisitados().isEmpty()) {
                registrarAlerta(new Alerta("ALT-CLI-" + c.getId(),
                        "Falta seguimiento: El cliente " + c.getNombre() + " no ha tenido interacciones recientes",
                        "Alerta Operativa", 6));
            }
        }

        // Análisis de asesores (Sobrecarga)
        for (int i = 0; i < asesores.getSize(); i++) {
            Asesor asesor = asesores.getData(i);
            if (asesor.getCargaTrabajoActiva() >= 5) {
                registrarAlerta(new Alerta(
                        "ALERT-ASE" + asesor.getId(), "Sobrecarga crítica: El asesor " + asesor.getNombre() + "tiene "
                                + asesor.getCargaTrabajoActiva() + " tareas pendientes.",
                        Alerta.TIPO_COMPORTAMIENTO_INUSUAL, 9));
            }
        }
    }

    // -------------------------------------------------------------------------------------
    // OPERACIONES Y SOLICITUDES
    // -------------------------------------------------------------------------------------

    /**
     * Registra un arriendo o venta, finalizando la disponibilidad del inmueble y
     * actualizando el historial de operaciones realizadas.
     * 
     * @param nuevaOperacion la operación a registrar.
     */
    public void registrarOperacion(Operacion nuevaOperacion) {
        pilaHistorialCambios.push(new CambioEstado(CambioEstado.TIPO_MODIFICACION_ESTADO,
                nuevaOperacion.getInmuebleRelacionado(),
                "Registro de operación (" + nuevaOperacion.getTipoOperacion() + "): " + nuevaOperacion.getId()));

        operacionesRealizadas.addLast(nuevaOperacion);

        String tipo = nuevaOperacion.getTipoOperacion();
        if (tipo.equals(Operacion.TIPO_VENTA) || tipo.equals(Operacion.TIPO_ARRIENDO)) {
            // "Finalizado": Bloque disponibilidad si es venta/arriendo, y mantiene ocupado
            // si es renovación
            nuevaOperacion.finalizarOperacion();
        } else if (tipo.equals(Operacion.TIPO_CANCELACION)) {
            // "Caído/revertido": Libera el inmueble, dejando su disponibilidad = true para
            // volver a ser comercializado.
            nuevaOperacion.revertirOperacion();
        }
        if (nuevaOperacion.getAsesor() != null) {
            // Suma el cierre exitoso al contador interno del Asesor
            nuevaOperacion.getAsesor().registrarCierre(nuevaOperacion);
        }
    }

    public void registrarSolicitudCliente(String solicitud) {
        colaSolicitudesClientes.offer(solicitud);
    }

    public String atenderSiguienteSolicitud() {
        return colaSolicitudesClientes.isEmpty() ? null : colaSolicitudesClientes.poll();
    }

    public String verSiguienteSolicitud() {
        return colaSolicitudesClientes.isEmpty() ? null : colaSolicitudesClientes.peek();
    }

    // -------------------------------------------------------------------------------------
    // INTELIGENCIA DE NEGOCIOS Y REPORTES
    // -------------------------------------------------------------------------------------

    /**
     * Algoritmo que cruza Grafo de historial y Árbol de presupuestos para generar
     * recomendaciones personalizadas de inmuebles a un cliente.
     * 
     * @param idCliente el ID del cliente para el cual se generarán las
     *                  recomendaciones de inmuebles.
     * @return una lista de inmuebles recomendados para el cliente.
     */
    public LinkedSimpleList<Inmueble> generarRecomendaciones(String idCliente) {
        LinkedSimpleList<Inmueble> recomendacionesFinales = new LinkedSimpleList<>();

        Cliente cliente = clientesPorId.get(idCliente);
        if (cliente == null)
            return recomendacionesFinales;

        if (!cliente.getHistorialConsultas().isEmpty()) {
            int ultimo = cliente.getHistorialConsultas().getSize() - 1;
            Inmueble ultimoVisto = cliente.getHistorialConsultas().getData(ultimo);

            LinkedSimpleList<String> idsSugeridos = buscarInmueblesSimilaresEnGrafo(ultimoVisto.getCodigo(), idCliente);

            for (int i = 0; i < idsSugeridos.getSize(); i++) {
                String idSugerido = idsSugeridos.getData(i);
                Inmueble inmuebleRecomendado = inmueblesPorCodigo.get(idSugerido);

                if (inmuebleRecomendado != null && inmuebleRecomendado.isDisponibilidad()
                        && inmuebleRecomendado.getPrecio() <= cliente.getPresupuesto()
                        && inmuebleRecomendado.getHabitaciones() >= cliente.getMinHabitaciones()) {
                    recomendacionesFinales.addLast(inmuebleRecomendado);
                }
            }
        }

        if (recomendacionesFinales.getSize() < 5) {
            double min = cliente.getPresupuesto() * 0.7;
            double max = cliente.getPresupuesto();

            LinkedSimpleList<Inmueble> inmueblesOrdenados = arbolInmueblesPorPrecio.getInOrder();
            for (Inmueble inm : inmueblesOrdenados) {
                if (inm.getPrecio() > max) {
                    break;
                }
                if (inm.getPrecio() >= min && inm.isDisponibilidad()
                        && inm.getClass().getSimpleName().equals(cliente.getTipoInmuebleDeseado())
                        && inm.getHabitaciones() >= cliente.getMinHabitaciones()) {
                    boolean coincideZona = false;
                    if (cliente.getZonasInteres().isEmpty()) {
                        coincideZona = true;
                    } else {
                        for (int j = 0; j < cliente.getZonasInteres().getSize(); j++) {
                            if (cliente.getZonasInteres().getData(j).equalsIgnoreCase(inm.getBarrioZona())) {
                                coincideZona = true;
                                break;
                            }
                        }
                    }

                    if (coincideZona && recomendacionesFinales.getIndex(inm) == -1) {
                        recomendacionesFinales.addLast(inm);
                    }
                }
            }
        }
        return recomendacionesFinales;
    }

    /**
     * Busca en el Grafo qué otros inmuebles vieron los clientes que vieron el mismo
     * inmueble base, para generar recomendaciones de inmuebles similares basados en
     * el comportamiento de otros clientes con gustos similares.
     * 
     * @param idInmuebleBase  el ID del inmueble base que se usará para encontrar
     *                        clientes similares y sus interacciones, con el fin de
     *                        recomendar inmuebles relacionados que hayan sido
     *                        consultados por esos clientes similares.
     * @param idClienteActual el ID del cliente actual para evitar recomendarle
     *                        inmuebles que el mismo cliente ya haya visto o marcado
     *                        como favorito.
     * @return una lista de IDs de inmuebles recomendados basados en la similitud de
     *         comportamiento de otros clientes en el grafo de relaciones.
     */
    private LinkedSimpleList<String> buscarInmueblesSimilaresEnGrafo(String idInmuebleBase, String idClienteActual) {
        LinkedSimpleList<String> inmueblesRecomendados = new LinkedSimpleList<>();

        Vertice<String> vInmueble = grafoRelaciones.searchVertex(idInmuebleBase);
        if (vInmueble == null)
            return inmueblesRecomendados;

        LinkedSimpleList<Vertice<String>> clientesQueVisitaron = vInmueble.getAdyacentes();

        for (int i = 0; i < clientesQueVisitaron.getSize(); i++) {
            Vertice<String> vOtroCliente = clientesQueVisitaron.getData(i);
            if (vOtroCliente.getDato().equals(idClienteActual))
                continue;
            LinkedSimpleList<Vertice<String>> inmueblesDeOtroCliente = vOtroCliente.getAdyacentes();
            for (int j = 0; j < inmueblesDeOtroCliente.getSize(); j++) {
                String idNuevoInmueble = inmueblesDeOtroCliente.getData(j).getDato();
                if (!idNuevoInmueble.equals(idInmuebleBase) && inmueblesRecomendados.getIndex(idNuevoInmueble) == -1) {
                    inmueblesRecomendados.addLast(idNuevoInmueble);
                }
            }
        }
        return inmueblesRecomendados;
    }

    /**
     * Utiliza un Árbol Binario temporal para ordenar a los asesores por su
     * efectividad.
     * 
     * @return una lista enlazada simple con los asesores ordenados de mayor a menor
     *         efectividad, donde la efectividad se calcula como el número de
     *         operaciones exitosas realizadas por el asesor dividido por el número
     *         total de visitas que ha atendido.
     */
    public LinkedSimpleList<Asesor> generarRankingAsesores() {
        BinarySearchTree<Asesor> arbolOrdenado = new BinarySearchTree<>();

        for (int i = 0; i < asesores.getSize(); i++) {
            arbolOrdenado.insert(asesores.getData(i));
        }
        return arbolOrdenado.getInOrder();
    }

    /**
     * Genera un ranking de las zonas con mayor número de visitas registradas.
     * 
     * @return una lista enlazada simple con las zonas ordenadas de mayor a menor
     *         número de visitas.
     */
    public LinkedSimpleList<String> obtenerRankingZonas() {
        LinkedSimpleList<String> ranking = new LinkedSimpleList<>();
        for (int i = 0; i < catalogoInmuebles.getSize(); i++) {
            Inmueble inm = catalogoInmuebles.getData(i);
            int visitas = inm.getHistorialVisitas().getSize();
            if (visitas > 0) {
                ranking.addLast(inm.getBarrioZona() + " | Inmueble: " + inm.getCodigo() + " | Visitas: " + visitas);
            }
        }
        return ranking;
    }

    /**
     * Obtiene una lista de inmuebles agrupados previamente en la Tabla Hash por
     * ciudad, para facilitar la consulta de inmuebles disponibles en una zona
     * específica.
     * 
     * @param ciudad la ciudad para la cual se desea obtener la lista de inmuebles
     *               disponibles.
     * @return una lista enlazada simple con los inmuebles disponibles en la ciudad
     *         especificada, o una lista vacía si no hay inmuebles registrados para
     *         esa ciudad.
     */
    public LinkedSimpleList<Inmueble> obtenerInmueblesPorCiudad(String ciudad) {
        LinkedSimpleList<Inmueble> lista = inmueblesAgrupadosPorCiudad.get(ciudad.toUpperCase());
        return lista != null ? lista : new LinkedSimpleList<>();
    }

    /**
     * Realiza un filtrado eficiente descartando ramas del Árbol Binario de
     * Inmuebles ordenados por precio.
     * 
     * @param precioMin       el precio mínimo deseado.
     * @param precioMax       el precio máximo deseado.
     * @param zona            la zona deseada.
     * @param minHabitaciones el número mínimo de habitaciones deseado.
     * @return una lista enlazada simple con los inmuebles que cumplen con los
     *         criterios de filtrado.
     */
    public LinkedSimpleList<Inmueble> buscarInmuebleConFiltros(double precioMin, double precioMax, String zona,
            int minHabitaciones) {
        LinkedSimpleList<Inmueble> resultados = new LinkedSimpleList<>();
        LinkedSimpleList<Inmueble> porPrecio = arbolInmueblesPorPrecio.getInOrder();

        for (Inmueble inm : porPrecio) {
            if (inm.getPrecio() > precioMax)
                break;
            if (inm.getPrecio() >= precioMin && inm.getBarrioZona().equals(zona)
                    && inm.getHabitaciones() >= minHabitaciones && inm.isDisponibilidad()) {
                resultados.addLast(inm);
            }
        }
        return resultados;
    }

    /**
     * Simula el crecimiento de la demanda en un sector dividiendo visitas entre
     * oferta.
     * 
     * @param zona la zona para la cual se desea simular el crecimiento de la
     *             demanda.
     * @return un mensaje con el promedio de visitas por inmueble en esa zona y la
     *         proyección de crecimiento para el proximo es, o un mensaje indicando
     *         que no hay datos suficientes para realizar la proyección si no se
     *         encuentran inmuebles registrados en esa zona.
     */
    public String simularCrecimientoDemanda(String zona) {
        int visitasActuales = 0;
        int totalInmueblesZona = 0;

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
        } else {
            System.out.println("No hay datos suficientes en la zona: " + zona);
        }
        return "No hay datos suficientes para proyectar el crecimiento en la zona: " + zona;
    }

    /**
     * Detecta clientes que cumplen con el perfil de Alta Prioridad de cierre, que
     * se define con aquellos clientes que tienen una búsqueda activa, han marcado
     * al menos 3 inmuebles como favoritos y han consultado al menos 3 inmuebles en
     * su historial de consultas.
     */
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

    // -------------------------------------------------------------------------------------
    // PILA DE HISTORIAL (DESHACER ACCIONES)
    // -------------------------------------------------------------------------------------

    /**
     * Observa el texto de la última acción sin extraerla de la pila.
     * 
     * @return la descripción de la última acción realizada, o null si no hay
     *         acciones registradas en la pila de historial de cambios.
     */
    public String obtenerUltimaAccion() {
        if (!pilaHistorialCambios.isEmpty()) {
            return pilaHistorialCambios.peek().getDescripcionAccion();
        }
        return null;
    }

    /**
     * Extraer (pop) el último cambio y ejecuta la restauración de ese cambio, ya
     * sea deshaciendo una modificación de estado, eliminando un registro de
     * inmueble o cliente, etc., dependiendo del tipo de cambio registrado en el
     * objeto CambioEstado.
     * 
     * @return la descripción de la acción que se ha deshecho, o null si no hay
     *         acciones registradas en la pila de historial de cambios para
     *         deshacer.
     */
    public String extraerUltimoCambio() {
        if (!pilaHistorialCambios.isEmpty()) {
            CambioEstado ultimo = pilaHistorialCambios.pop();

            // Deshacer Modificación o Venta (Recupera el estado anterior del inmueble,
            // incluyendo precio, estado y disponibilidad)
            if (ultimo.getTipoCambio() == CambioEstado.TIPO_MODIFICACION_ESTADO) {
                Inmueble inm = (Inmueble) ultimo.getEntidad();
                if (inm.getPrecio() != ultimo.getPrecioAnterior()) {
                    arbolInmueblesPorPrecio.delete(inm);
                    inm.setPrecio(ultimo.getPrecioAnterior());
                    arbolInmueblesPorPrecio.insert(inm);
                }
                inm.setEstado(ultimo.getEstadoAnterior());
                inm.setDisponibilidad(ultimo.isDisponibilidadAnterior());
            }

            // Deshacer Registro (Borrar)
            else if (ultimo.getTipoCambio() == CambioEstado.TIPO_REGISTRO_INMUEBLE) {
                Inmueble inm = (Inmueble) ultimo.getEntidad();
                arbolInmueblesPorPrecio.delete(inm);
                inmueblesPorCodigo.remove(inm.getCodigo());
                catalogoInmuebles.removeData(inm);
                LinkedSimpleList<Inmueble> lista = inmueblesAgrupadosPorCiudad.get(inm.getCiudad().toUpperCase());
                if (lista != null) {
                    lista.removeData(inm);
                }
            }

            // Deshacer Eliminación (Volver a insertar en todo el sistema)
            else if (ultimo.getTipoCambio() == CambioEstado.TIPO_ELIMINACION_INMUEBLE) {
                Inmueble inm = (Inmueble) ultimo.getEntidad();
                catalogoInmuebles.addLast(inm);
                inmueblesPorCodigo.put(inm.getCodigo(), inm);
                arbolInmueblesPorPrecio.insert(inm);

                String ciudad = inm.getCiudad().toUpperCase();
                LinkedSimpleList<Inmueble> lista = inmueblesAgrupadosPorCiudad.get(ciudad);
                if (lista == null) {
                    lista = new LinkedSimpleList<>();
                    inmueblesAgrupadosPorCiudad.put(ciudad, lista);
                }
                lista.addLast(inm);
            }

            // Deshacer Tarea (Notificación)
            else if (ultimo.getTipoCambio() == CambioEstado.TIPO_ACCION_ADMINISTRATIVA) {
            }
            return ultimo.getDescripcionAccion();
        }
        return null;
    }

    // -------------------------------------------------------------------------------------
    // GETTERS
    // -------------------------------------------------------------------------------------

    public BinarySearchTree<Inmueble> getArbolInmueblesPorPrecio() {
        return arbolInmueblesPorPrecio;
    }

    public LinkedSimpleList<Cliente> getClientes() {
        return clientes;
    }

    public LinkedSimpleList<Asesor> getAsesores() {
        return asesores;
    }

    /**
     * Método auxiliar para obtener un resumen de las zonas con mayor número de
     * visitas registradas.
     * 
     * @return una lista enlazada simple con mensajes resumen de las zonas ordenadas
     *         de mayor a menor número de visitas, indicando el código del inmueble,
     *         la zona y el número de visitas registradas para cada inmueble que
     *         tenga al menos una visita registrada.
     */
    public LinkedSimpleList<String> obtenerResumenZonas() {
        LinkedSimpleList<String> resumen = new LinkedSimpleList<>();

        for (int i = 0; i < catalogoInmuebles.getSize(); i++) {
            Inmueble inm = catalogoInmuebles.getData(i);
            int visitas = inm.getHistorialVisitas().getSize();
            if (visitas > 0) {
                resumen.addLast(
                        inm.getBarrioZona() + " (" + inm.getCodigo() + "): " + visitas + " visitas registradas.");
            }
        }
        return resumen;
    }

    /**
     * Imprime en consola un ranking de las zonas con mayor número de visitas
     * registradas, mostrando el código del inmueble, la zona y el número de visitas
     * para cada inmueble que tenga al menos una visita registrada, ordenados de
     * mayor a menor número de visitas.
     */
    public void imprimirRankingZonas() {
        System.out.println("--- Ranking de Zonas por Actividad ---");
        for (int i = 0; i < catalogoInmuebles.getSize(); i++) {
            Inmueble inm = catalogoInmuebles.getData(i);
            int visitas = inm.getHistorialVisitas().getSize();
            if (visitas > 0) {
                System.out.println(
                        "Zona: " + inm.getBarrioZona() + " | Inmueble: " + inm.getCodigo() + " | Visitas: " + visitas);
            }
        }
    }
}
