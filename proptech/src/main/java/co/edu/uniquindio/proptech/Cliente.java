package co.edu.uniquindio.proptech;

import java.util.ArrayList;
import java.util.List;

import co.edu.uniquindio.proptech.CircularLinkedList.CircularLinkedList;
import co.edu.uniquindio.proptech.DoublyLinkedList.DoublyLinkedList;
import co.edu.uniquindio.proptech.LinkedSimpleList.LinkedSimpleList;
import jakarta.persistence.*;

@Entity
@Table(name = "clientes")
public class Cliente implements Comparable<Cliente>{
    @Id
    private String id;
    private String nombre;
    private String correo;
    private String telefono;
    private String tipoCliente;
    private double presupuesto;
    private String tipoInmuebleDeseado;
    private int minHabitaciones;
    private String estadoBusqueda;

    // --- Estructuras propias (en memoria) ---
    @Transient
    private CircularLinkedList<String> zonasInteres;
    @Transient
    private DoublyLinkedList<Inmueble> favoritos;
    @Transient
    private DoublyLinkedList<Inmueble> historialConsultas;
    @Transient
    private DoublyLinkedList<Inmueble> inmueblesDescartados;
    @Transient
    private DoublyLinkedList<Inmueble> inmueblesVisitados;
    @Transient
    private LinkedSimpleList<Inmueble> inmueblesNegociados;
    @Transient
    private DoublyLinkedList<Inmueble> intenciones;

    // --- Listas JPA para persistencia ---
    @ElementCollection
    private List<String> zonasInteresJpa = new ArrayList<>();
    @ManyToMany
    private List<Inmueble> favoritosJpa = new ArrayList<>();
    @ManyToMany
    private List<Inmueble> historialConsultasJpa = new ArrayList<>();
    @ManyToMany
    private List<Inmueble> inmueblesDescartadosJpa = new ArrayList<>();
    @ManyToMany
    private List<Inmueble> inmueblesVisitadosJpa = new ArrayList<>();
    @ManyToMany
    private List<Inmueble> inmueblesNegociadosJpa = new ArrayList<>();
    @ManyToMany
    private List<Inmueble> intencionesJpa = new ArrayList<>();

    public Cliente() {
        inicializarColecciones();
    }

    private void inicializarColecciones() {
        this.zonasInteres = new CircularLinkedList<>();
        this.favoritos = new DoublyLinkedList<>();
        this.historialConsultas = new DoublyLinkedList<>();
        this.inmueblesDescartados = new DoublyLinkedList<>();
        this.inmueblesVisitados = new DoublyLinkedList<>();
        this.inmueblesNegociados = new LinkedSimpleList<>();
        this.intenciones = new DoublyLinkedList<>();
    }

    public Cliente(String id, String nombre, String correo, String telefono, String tipoCliente, double presupuesto, String tipoInmuebleDeseado, int minHabitaciones){
        this();
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.tipoCliente = tipoCliente;
        this.presupuesto = presupuesto;
        this.tipoInmuebleDeseado = tipoInmuebleDeseado;
        this.minHabitaciones = minHabitaciones;
        this.estadoBusqueda = "Activa";
    }

    @PostLoad
    public void postLoad() {
        inicializarColecciones();
        for (String z : zonasInteresJpa) zonasInteres.addFirst(z);
        for (Inmueble i : favoritosJpa) favoritos.addLast(i);
        for (Inmueble i : historialConsultasJpa) historialConsultas.addLast(i);
        for (Inmueble i : inmueblesDescartadosJpa) inmueblesDescartados.addLast(i);
        for (Inmueble i : inmueblesVisitadosJpa) inmueblesVisitados.addLast(i);
        for (Inmueble i : inmueblesNegociadosJpa) inmueblesNegociados.addLast(i);
        for (Inmueble i : intencionesJpa) intenciones.addLast(i);
    }

    @PrePersist
    @PreUpdate
    public void prePersist() {
        zonasInteresJpa = new ArrayList<>();
        favoritosJpa = new ArrayList<>();
        historialConsultasJpa = new ArrayList<>();
        inmueblesDescartadosJpa = new ArrayList<>();
        inmueblesVisitadosJpa = new ArrayList<>();
        inmueblesNegociadosJpa = new ArrayList<>();
        intencionesJpa = new ArrayList<>();
        for (int i = 0; i < zonasInteres.getSize(); i++) zonasInteresJpa.add(zonasInteres.getData(i));
        for (int i = 0; i < favoritos.getSize(); i++) favoritosJpa.add(favoritos.getData(i));
        for (int i = 0; i < historialConsultas.getSize(); i++) historialConsultasJpa.add(historialConsultas.getData(i));
        for (int i = 0; i < inmueblesDescartados.getSize(); i++) inmueblesDescartadosJpa.add(inmueblesDescartados.getData(i));
        for (int i = 0; i < inmueblesVisitados.getSize(); i++) inmueblesVisitadosJpa.add(inmueblesVisitados.getData(i));
        for (int i = 0; i < inmueblesNegociados.getSize(); i++) inmueblesNegociadosJpa.add(inmueblesNegociados.getData(i));
        for (int i = 0; i < intenciones.getSize(); i++) intencionesJpa.add(intenciones.getData(i));
    }

    public void agregarZonaInteres(String zona){ this.zonasInteres.addFirst(zona); }
    public void registrarConsulta(Inmueble inmueble){ this.historialConsultas.addLast(inmueble); }
    public void marcarFavorito(Inmueble inmueble){ this.favoritos.addLast(inmueble); }
    public void descartarInmueble(Inmueble inmueble){ this.inmueblesDescartados.addLast(inmueble); }
    public void registrarVisita(Inmueble inmueble){ this.inmueblesVisitados.addLast(inmueble); }
    public void registrarInmuebleNegociado(Inmueble inmueble){ this.inmueblesNegociados.addLast(inmueble); }
    public void registrarIntencion(Inmueble inmueble){ this.intenciones.addLast(inmueble); }

    @Override
    public int compareTo(Cliente otroCliente) {
        return Double.compare(this.presupuesto, otroCliente.getPresupuesto());
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getTelefono() { return telefono; }
    public String getTipoCliente() { return tipoCliente; }
    public double getPresupuesto() { return presupuesto; }
    public void setPresupuesto(double presupuesto) { this.presupuesto = presupuesto; }
    public String getTipoInmuebleDeseado() { return tipoInmuebleDeseado; }
    public int getMinHabitaciones() { return minHabitaciones; }
    public String getEstadoBusqueda() { return estadoBusqueda; }
    public void setEstadoBusqueda(String estadoBusqueda) { this.estadoBusqueda = estadoBusqueda; }
    public CircularLinkedList<String> getZonasInteres() { return zonasInteres; }
    public DoublyLinkedList<Inmueble> getFavoritos() { return favoritos; }
    public DoublyLinkedList<Inmueble> getHistorialConsultas() { return historialConsultas; }
    public LinkedSimpleList<Inmueble> getInmueblesNegociados() { return inmueblesNegociados; }
    public DoublyLinkedList<Inmueble> getInmueblesVisitados() { return inmueblesVisitados; }
    public DoublyLinkedList<Inmueble> getIntenciones(){ return intenciones; }
    public DoublyLinkedList<Inmueble> getInmueblesDescartados(){ return inmueblesDescartados; }
}
