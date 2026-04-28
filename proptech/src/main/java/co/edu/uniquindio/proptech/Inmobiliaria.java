package co.edu.uniquindio.proptech;

import co.edu.uniquindio.proptech.BinarySearchTree.BinarySearchTree;
import co.edu.uniquindio.proptech.DoublyLinkedList.DoublyLinkedList;
import co.edu.uniquindio.proptech.HashTable.HashTable;
import co.edu.uniquindio.proptech.LinkedSimpleList.LinkedSimpleList;
import co.edu.uniquindio.proptech.Queues.Deque;
import co.edu.uniquindio.proptech.Queues.Queue;
import co.edu.uniquindio.proptech.Stack.Stack;

/**
 * Principal class of the project, it will contain the main system of the PropTech platform.
 * Manage all of the data structures and operations of the platform.
 * @author Juan Jose Carvajal, Juliana Andrea Bustamante Niño y Jaider Andrés Melo Rodríguez
 */
public class Inmobiliaria {
    // -------------------------------------------------------------------------------------
    // Own Data Structures
    // -------------------------------------------------------------------------------------

    // Binary Search Tree for fast sort and search of properties by price
    private BinarySearchTree<Inmueble> arbolInmueblesPorPrecio;

    // Hash tables for direct access to properties and clients by their ids or codes
    private HashTable<String, Inmueble> inmueblesPorCodigo;
    private HashTable<String, Cliente> clientesPorId;

    // Graph
    private Graph<String> grafoRelaciones;

    // Lists for cataloging properties and historical records
    private DoublyLinkedList<Inmueble> catalogoInmuebles;
    private LinkedSimpleList<Cliente> clientes;
    private LinkedSimpleList<Asesor> asesores;

    // Queues for managing appointments and client requests
    private Queue<Visita> colaVisitasPendientes;

    // Deque for alerts management with priority handling
    // Urgent alerts will be added to the front, while regular alerts will be added to the back
    private Deque<Alerta> bicolaAlertasSistema;

    // Stacks
    private Stack<String> pilaHistorialCambios;

    // -------------------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------------------
    public Inmobiliaria(){
        this.arbolInmueblesPorPrecio = new BinarySearchTree<>();
        this.inmueblesPorCodigo = new HashTable<>();
        this.clientesPorId = new HashTable<>();
        this.catalogoInmuebles = new DoublyLinkedList<>();
        this.clientes = new LinkedSimpleList<>();
        this.asesores = new LinkedSimpleList<>();
        this.colaVisitasPendientes = new Queue<>();
        this.bicolaAlertasSistema = new Deque<>();
        this.pilaHistorialCambios = new Stack<>();
        this.grafoRelaciones = new Graph<>();
    }

    // -------------------------------------------------------------------------------------
    // Recomendación de inmuebles para un cliente
    // -------------------------------------------------------------------------------------
    public LinkedSimpleList<Inmueble> generarRecomendaciones(String idCliente){
        LinkedSimpleList<Inmueble> recomendacionesFinales = new LinkedSimpleList<>();

        Cliente cliente = clientesPorId.get(idCliente);
        if(cliente == null) return recomendacionesFinales;

        if(!cliente.getHistorialConsultas().isEmpty()){
            int ultimo = cliente.getHistorialConsultas().getSize()-1;
            Inmueble ultimoVisto = cliente.getHistorialConsultas().getData(ultimo);
            LinkedSimpleList<String> idsSugeridos = buscarInmueblesSimilaresEnGrafo(ultimoVisto.getCodigo(), idCliente);

            for(int i = 0; i < idsSugeridos.getSize(); i++){
                String idSugerido = idsSugeridos.getData(i);
                Inmueble inmuebleRecomendado = inmueblesPorCodigo.get(idSugerido);

                if(inmuebleRecomendado != null && inmuebleRecomendado.isDisponibilidad() && inmuebleRecomendado.getPrecio() <= cliente.getPresupuesto()){
                    recomendacionesFinales.addLast(inmuebleRecomendado);
                }
            }
        }

        if(recomendacionesFinales.getSize() < 5){
            double min = cliente.getPresupuesto() * 0.7;
            double max = cliente.getPresupuesto();
            LinkedSimpleList<Inmueble> inmueblesOrdenados = arbolInmueblesPorPrecio.getInOrder();
            for(Inmueble inm : inmueblesOrdenados){
                if(inm.getPrecio() > max){
                    break;
                }
                if(inm.getPrecio() >= min && inm.isDisponibilidad() && inm.getClass().getSimpleName().equals(cliente.getTipoInmuebleDeseado())){
                    if(recomendacionesFinales.getIndex(inm) == -1){
                        recomendacionesFinales.addLast(inm);
                    }
                }
            }
        }
        return recomendacionesFinales;
    }

    private LinkedSimpleList<String> buscarInmueblesSimilaresEnGrafo(String idInmuebleBase, String idClienteActual){
        LinkedSimpleList<String> inmueblesRecomendados = new LinkedSimpleList<>();

        Vertice<String> vInmueble = grafoRelaciones.searchVertex(idInmuebleBase);
        if(vInmueble == null) return inmueblesRecomendados;

        LinkedSimpleList<Vertice<String>> clientesQueVisitaron = vInmueble.getAdyacentes();

        for(int i = 0; i < clientesQueVisitaron.getSize(); i++){
            Vertice<String> vOtroCliente = clientesQueVisitaron.getData(i);
            if(vOtroCliente.getDato().equals(idClienteActual)) continue;
            LinkedSimpleList<Vertice<String>> inmueblesDeOtroCliente = vOtroCliente.getAdyacentes();
            for(int j = 0; j < inmueblesDeOtroCliente.getSize(); j++){
                String idNuevoInmueble = inmueblesDeOtroCliente.getData(j).getDato();
                if(!idNuevoInmueble.equals(idInmuebleBase) && inmueblesRecomendados.getIndex(idNuevoInmueble) == -1){
                    inmueblesRecomendados.addLast(idNuevoInmueble);
                }
            }
        }
        return inmueblesRecomendados;
    }

    public LinkedSimpleList<Asesor> generarRankingAsesores(){
        BinarySearchTree<Asesor> arbolOrdenado = new BinarySearchTree<>();

        for(int i = 0; i < asesores.getSize(); i++){
            arbolOrdenado.insert(asesores.getData(i));
        }
        return arbolOrdenado.getInOrder();
    }

    public void imprimirRankingZonas(){
        System.out.println("--- Ranking de Zonas por Actividad ---");
        for(int i = 0; i < catalogoInmuebles.getSize(); i++){
            Inmueble inm = catalogoInmuebles.getData(i);
            int visitas = inm.getHistorialVisitas().getSize();
            if(visitas > 0){
                System.out.println("Zona: " + inm.getBarrioZona() + " | Inmueble: " + inm.getCodigo() + " | Visitas: " + visitas);
            }
        }
    }

    public LinkedSimpleList<Cliente> detectarClientesAltaPrioridad(){
        LinkedSimpleList<Cliente> clientesPrioridad = new LinkedSimpleList<>();

        for(int i = 0; i < clientes.getSize(); i++){
            Cliente cliente = clientes.getData(i);
            if(cliente.getEstadoBusqueda().equals("Activa") && cliente.getFavoritos().getSize() >= 3 && cliente.getHistorialConsultas().getSize() >= 3){
                clientesPrioridad.addLast(cliente);
            }
        }
        return clientesPrioridad;
    }

    public LinkedSimpleList<Inmueble> buscarInmuebleConFiltros(double precioMin, double precioMax, String zona, int minHabitaciones){
        LinkedSimpleList<Inmueble> resultados = new LinkedSimpleList<>();
        LinkedSimpleList<Inmueble> porPrecio = arbolInmueblesPorPrecio.getInOrder();

        for(Inmueble inm : porPrecio){
            if(inm.getPrecio() > precioMax) break;
            if(inm.getPrecio() >= precioMin && inm.getBarrioZona().equals(zona) && inm.getHabitaciones() >= minHabitaciones && inm.isDisponibilidad()){
                resultados.addLast(inm);
            }
        }
        return resultados;
    }

    public void simularCrecimientoDemanda(String zona){
        int visitasActuales = 0;
        int totalInmueblesZona = 0;

        for(int i = 0; i < catalogoInmuebles.getSize(); i++){
            Inmueble inm = catalogoInmuebles.getData(i);
            if(inm.getBarrioZona().equalsIgnoreCase(zona)){
                visitasActuales += inm.getHistorialVisitas().getSize();
                totalInmueblesZona++;
            }
        }
        if(totalInmueblesZona > 0){
            double promedio = (double) visitasActuales / totalInmueblesZona;
            double proyeccionCrecimiento = promedio > 5 ? 15.0 : 5.0;

            System.out.println("--- Simulación de Demanda: " + zona + " ---");
            System.out.println("Promedio de visitas por inmueble: " + promedio);
            System.out.println("Proyección de crecimiento para el próximo mes: " + proyeccionCrecimiento);
        } else {
            System.out.println("No hay datos suficientes en la zona: " + zona);
        }
    }

    public void registrarInmueble(Inmueble nuevoInmueble) {
        catalogoInmuebles.addLast(nuevoInmueble);
        inmueblesPorCodigo.put(nuevoInmueble.getCodigo(), nuevoInmueble);
        arbolInmueblesPorPrecio.insert(nuevoInmueble);
        pilaHistorialCambios.push("Registro de inmueble: " + nuevoInmueble.getCodigo());
    }

    public Inmueble buscarInmueblePorCodigo(String codigo){
        return inmueblesPorCodigo.get(codigo);
    }

    // -------------------------------------------------------------------------------------
    // Client management methods
    // -------------------------------------------------------------------------------------
    public void registrarCliente(Cliente nuevoCliente){
        clientesPorId.put(nuevoCliente.getId(), nuevoCliente);
        clientes.addLast(nuevoCliente);
        pilaHistorialCambios.push("Registro de cliente: " + nuevoCliente.getId());
    }

    public Cliente buscarClientePorId(String idCliente){
        return clientesPorId.get(idCliente);
    }

    public void registrarAsesor(Asesor nuevoAsesor){
        asesores.addLast(nuevoAsesor);
        pilaHistorialCambios.push("Registro de asesor: " + nuevoAsesor.getId());
    }

    public void conectarClientesConInmuebles(String idCliente, String codigoInmueble){
        grafoRelaciones.addVertex(idCliente);
        grafoRelaciones.addVertex(codigoInmueble);

        grafoRelaciones.connect(idCliente, codigoInmueble);
    }

    // -------------------------------------------------------------------------------------
    // Operations and flows methods (Visits and Alerts)
    // -------------------------------------------------------------------------------------
    public void agendarVisita(Visita visita){
        colaVisitasPendientes.enqueue(visita);
        pilaHistorialCambios.push("Visita agendada para cliente: " + visita.getCliente().getId());
    }

    public Visita atenderSiguienteVisita(){
        return colaVisitasPendientes.dequeue();
    }

    public void registrarAlerta(Alerta alerta){
        if(alerta.getNivelPrioridad() > 5){
            bicolaAlertasSistema.addFirst(alerta);
        } else {
            bicolaAlertasSistema.addLast(alerta);
        }
    }

    public void revisarAlertas(){
        System.out.println("\n--- BANDEJA DE ALERTAS DEL SISTEMA ---");
        if(bicolaAlertasSistema.isEmpty()){
            System.out.println("El sistema no tiene alertas pendientes.");
            return;
        }
        while(!bicolaAlertasSistema.isEmpty()){
            Alerta alertaActual = bicolaAlertasSistema.dequeue();

            System.out.println("[Prioridad " + alertaActual.getNivelPrioridad() + "] " + alertaActual.getTipoAlerta() + " | " + alertaActual.getMensaje());
        }
    }

    public LinkedSimpleList<String> extraerAlertas(){
        LinkedSimpleList<String> mensajesAlerta = new LinkedSimpleList<>();

        while(!bicolaAlertasSistema.isEmpty()){
            Alerta alerta = bicolaAlertasSistema.dequeue();

            String mensaje = "[Prioridad " + alerta.getNivelPrioridad() + "] " + alerta.getTipoAlerta() + " | " + alerta.getMensaje();
            mensajesAlerta.addLast(mensaje);
        }
        return mensajesAlerta;
    }
    

    public void detectarComportamientosInusuales(){
        for(int i = 0; i < catalogoInmuebles.getSize(); i++){
            Inmueble inm = catalogoInmuebles.getData(i);
            if(inm.isDisponibilidad() && inm.getHistorialVisitas().getSize() >= 10){
                Alerta alertaInmueble = new Alerta("ALERT-INM" + inm.getCodigo(), "Inmueble " + inm.getCodigo() + " estancado: " + inm.getHistorialVisitas().getSize() + " visitas sin cierre.", Alerta.TIPO_COMPORTAMIENTO_INUSUAL, 8);
                registrarAlerta(alertaInmueble);
            }
        }
        for(int i = 0; i < asesores.getSize(); i++){
            Asesor asesor = asesores.getData(i);
            if(asesor.getCargaTrabajoActiva() >= 15){
                Alerta alertaAsesor = new Alerta("ALERT-ASE" + asesor.getId(), "Sobrecarga: El asesor " + asesor.getNombre() + "tiene " + asesor.getCargaTrabajoActiva() + " tareas pendientes.", Alerta.TIPO_COMPORTAMIENTO_INUSUAL, 9);
                registrarAlerta(alertaAsesor);
            }
        }

        System.out.println("Análisis de comportamiento inusual finalizado. Alertas encoladas en el sistema.");
    }

    // -------------------------------------------------------------------------------------
    // Advanced manager functions
    // -------------------------------------------------------------------------------------

    public LinkedSimpleList<String> obtenerResumenZonas(){
        LinkedSimpleList<String> resumen = new LinkedSimpleList<>();

        for(int i = 0; i < catalogoInmuebles.getSize(); i++){
            Inmueble inm = catalogoInmuebles.getData(i);
            int visitas = inm.getHistorialVisitas().getSize();
            if(visitas > 0){
                resumen.addLast(inm.getBarrioZona() + " (" + inm.getCodigo() + "): " + visitas + " visitas registradas.");
            }
        }
        return resumen;
    }

    public String obtenerUltimaAccion(){
        if(!pilaHistorialCambios.isEmpty()){
            return pilaHistorialCambios.peek();
        }
        return null;
    }

    public String extraerUltimoCambio(){
        if(!pilaHistorialCambios.isEmpty()){
            return pilaHistorialCambios.pop();
        }
        return null;
    }

    public void deshacerUltimoCambio(){
        if(!pilaHistorialCambios.isEmpty()){
            String ultimaAccion = pilaHistorialCambios.peek();
            System.out.println("Deshaciendo acción: " + ultimaAccion);
        } else {
            System.out.println("No hay acciones por deshacer.");
        }
    }

    public BinarySearchTree<Inmueble> getArbolInmueblesPorPrecio() {
        return arbolInmueblesPorPrecio;
    }

    public LinkedSimpleList<Cliente> getClientes() {
        return clientes;
    }
}
