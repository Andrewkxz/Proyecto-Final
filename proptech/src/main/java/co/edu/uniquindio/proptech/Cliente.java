package co.edu.uniquindio.proptech;

import co.edu.uniquindio.proptech.CircularLinkedList.CircularLinkedList;
import co.edu.uniquindio.proptech.DoublyLinkedList.DoublyLinkedList;
import co.edu.uniquindio.proptech.LinkedSimpleList.LinkedSimpleList;

public class Cliente implements Comparable<Cliente>{
    private String id;
    private String nombre;
    private String correo;
    private String telefono;
    private String tipoCliente; // "Comprador", "Arrendador", "Inversionista"
    private double presupuesto;
    private String tipoInmuebleDeseado;
    private int minHabitaciones;
    private String estadoBusqueda; // "Activa", "Pausada", "Cerrada"

    private CircularLinkedList<String> zonasInteres;
    private DoublyLinkedList<Inmueble> favoritos;
    private DoublyLinkedList<Inmueble> historialConsultas;
    private DoublyLinkedList<Inmueble> inmueblesDescartados;
    private DoublyLinkedList<Inmueble> inmueblesVisitados;
    private LinkedSimpleList<Inmueble> inmueblesNegociados;
    private DoublyLinkedList<Inmueble> intenciones;

    public Cliente(String id, String nombre, String correo, String telefono, String tipoCliente, double presupuesto, String tipoInmuebleDeseado, int minHabitaciones){
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.tipoCliente = tipoCliente;
        this.presupuesto = presupuesto;
        this.tipoInmuebleDeseado = tipoInmuebleDeseado;
        this.minHabitaciones = minHabitaciones;
        this.estadoBusqueda = "Activa"; // Por defecto

        this.zonasInteres = new CircularLinkedList<>();
        this.favoritos = new DoublyLinkedList<>();
        this.historialConsultas = new DoublyLinkedList<>();
        this.inmueblesDescartados = new DoublyLinkedList<>();
        this.inmueblesVisitados = new DoublyLinkedList<>();
        this.inmueblesNegociados = new LinkedSimpleList<>();
        this.intenciones = new DoublyLinkedList<>();
    }

    public void agregarZonaInteres(String zona){
        this.zonasInteres.addFirst(zona);
    }

    public void registrarConsulta(Inmueble inmueble){
        this.historialConsultas.addLast(inmueble);
    }

    public void marcarFavorito(Inmueble inmueble){
        this.favoritos.addLast(inmueble);
    }

    public void descartarInmueble(Inmueble inmueble){
        this.inmueblesDescartados.addLast(inmueble);
    }

    public void registrarVisita(Inmueble inmueble){
        this.inmueblesVisitados.addLast(inmueble);
    }

    public void registrarInmuebleNegociado(Inmueble inmueble){
        this.inmueblesNegociados.addLast(inmueble);
    }

    public void registrarIntencion(Inmueble inmueble){
        this.intenciones.addLast(inmueble);
    }

    @Override
    public int compareTo(Cliente otroCliente) {
        return Double.compare(this.presupuesto, otroCliente.getPresupuesto());
    }

    public String getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }
    public String getCorreo() {
        return correo;
    }
    public String getTelefono() {
        return telefono;
    }
    public String getTipoCliente() {
        return tipoCliente;
    }
    public double getPresupuesto() {
        return presupuesto;
    }
    public void setPresupuesto(double presupuesto) {
        this.presupuesto = presupuesto;
    }
    public String getTipoInmuebleDeseado() {
        return tipoInmuebleDeseado;
    }
    public int getMinHabitaciones() {
        return minHabitaciones;
    }
    public String getEstadoBusqueda() {
        return estadoBusqueda;
    }
    public void setEstadoBusqueda(String estadoBusqueda) {
        this.estadoBusqueda = estadoBusqueda;
    }
    public CircularLinkedList<String> getZonasInteres() {
        return zonasInteres;
    }
    public DoublyLinkedList<Inmueble> getFavoritos() {
        return favoritos;
    }
    public DoublyLinkedList<Inmueble> getHistorialConsultas() {
        return historialConsultas;
    }
    public LinkedSimpleList<Inmueble> getInmueblesNegociados() {
        return inmueblesNegociados;
    }
    public DoublyLinkedList<Inmueble> getInmueblesVisitados() {
        return inmueblesVisitados;
    }
    public DoublyLinkedList<Inmueble> getIntenciones(){
        return intenciones;
    }
    public DoublyLinkedList<Inmueble> getInmueblesDescartados(){
        return inmueblesDescartados;
    }
}
