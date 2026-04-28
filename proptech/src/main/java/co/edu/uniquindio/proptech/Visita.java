package co.edu.uniquindio.proptech;

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
    private String estadoVisita;
    private String observaciones;
    private int prioridad; // 1(Baja) a 5(Alta)

    public Visita(Cliente cliente, Inmueble inmueble, LocalDate fecha, String hora, Asesor asesorAsignado, int prioridad){
        this.cliente = cliente;
        this.inmueble = inmueble;
        this.fecha = fecha;
        this.hora = hora;
        this.asesorAsignado = asesorAsignado;
        this.prioridad = prioridad;

        this.estadoVisita = ESTADO_PENDIENTE;
        this.observaciones = "";
    }

    public void confirmarVisita(){
        this.estadoVisita = ESTADO_CONFIRMADA;
    }

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
        this.observaciones = "Visita reprogramada del " + this.fecha + "a nueva fecha.";
    }
    @Override
    public int compareTo(Visita otraVisita) {
        return Integer.compare(otraVisita.prioridad, this.prioridad);
    }
    public Cliente getCliente() {
        return cliente;
    }
    public Inmueble getInmueble() {
        return inmueble;
    }
    public LocalDate getFecha() {
        return fecha;
    }
    public String getHora() {
        return hora;
    }
    public Asesor getAsesorAsignado() {
        return asesorAsignado;
    }
    public String getEstadoVisita() {
        return estadoVisita;
    }
    public String getObservaciones() {
        return observaciones;
    }
    public int getPrioridad() {
        return prioridad;
    }
}
