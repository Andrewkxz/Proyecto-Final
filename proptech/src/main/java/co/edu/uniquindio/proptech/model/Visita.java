package co.edu.uniquindio.proptech.model;

import java.time.LocalDate;

public class Visita implements Comparable<Visita>{
    public static final String ESTADO_PENDIENTE = "Pendiente";
    public static final String ESTADO_CONFIRMADA = "Confirmada";
    public static final String ESTADO_REALIZADA = "Realizada";
    public static final String ESTADO_CANCELADA = "Cancelada";
    public static final String ESTADO_REPROGRAMADA = "Reprogramada";

    private Cliente cliente;
    private Inmueble inmueble;
    private LocalDate fecha;
    private String hora;
    private Asesor asesorAsignado;
    private String idVisita;
    private String estadoVisita;
    private String observaciones;
    private int nivelUrgencia; 

    public Visita(String idVisita, Cliente cliente, Inmueble inmueble, LocalDate fecha, String hora, Asesor asesorAsignado, int nivelUrgencia){
        this.idVisita = idVisita; 
        this.cliente = cliente;
        this.inmueble = inmueble;
        this.fecha = fecha;
        this.hora = hora;
        this.asesorAsignado = asesorAsignado;
        this.nivelUrgencia = nivelUrgencia;
        this.estadoVisita = ESTADO_PENDIENTE;
        this.observaciones = "";
    }

    public void confirmarVisita(){ this.estadoVisita = ESTADO_CONFIRMADA; }
    public void registrarRealizacion(String observaciones){
        this.estadoVisita = ESTADO_REALIZADA;
        this.observaciones = observaciones;
        this.inmueble.registrarVisita(this);
    }
    public void cancelarVisita(String motivo){
        this.estadoVisita = ESTADO_CANCELADA;
        this.observaciones = "Visita cancelada. Motivo: " + motivo;
    }
    public void reprogramarVisita(LocalDate nuevaFecha, String nuevaHora){
        this.estadoVisita = ESTADO_REPROGRAMADA;
        this.fecha = nuevaFecha;
        this.hora = nuevaHora;
        this.observaciones = "Visita reprogramada del " + this.fecha + " a nueva fecha " + nuevaFecha;
    }
    @Override
    public int compareTo(Visita otraVisita) {
        return Integer.compare(otraVisita.getNivelUrgencia(), this.getNivelUrgencia());
    }

    // Getters y Setters
    public Cliente getCliente() { return cliente; }
    public Inmueble getInmueble() { return inmueble; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha){ this.fecha = fecha; }
    public String getHora() { return hora; }
    public void setHora(String hora){ this.hora = hora; }
    public Asesor getAsesorAsignado() { return asesorAsignado; }
    public String getEstadoVisita() { return estadoVisita; }
    public void setEstadoVisita(String estadoVisita) { this.estadoVisita = estadoVisita; }
    public String getObservaciones() { return observaciones; }
    public int getNivelUrgencia() { return nivelUrgencia; }
    public String getIdVisita() { return idVisita; }
}