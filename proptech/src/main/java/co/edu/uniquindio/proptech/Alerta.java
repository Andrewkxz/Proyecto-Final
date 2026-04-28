package co.edu.uniquindio.proptech;

import java.time.LocalDate;

public class Alerta implements Comparable<Alerta>{

    public static final String TIPO_VENCIMIENTO = "Contrato Próximo a Vencer";
    public static final String TIPO_INACTIVIDAD = "Inmueble sin visitas/Cliente sin seguimiento";
    public static final String TIPO_ALTA_DEMANDA = "Propiedad con Alta Demanda";
    public static final String TIPO_COMPORTAMIENTO_INUSUAL = "Comportamiento Inusual";

    private String idAlerta;
    private String mensaje;
    private String tipoAlerta;
    private int nivelPrioridad; // 1 (Bajo) a 10 (Crítico)
    private LocalDate fechaGeneracion;
    private boolean revisada; // Saber si ya fue procesada

    public Alerta(String idAlerta, String mensaje, String tipoAlerta, int nivelPrioridad){
        this.idAlerta = idAlerta;
        this.mensaje = mensaje;
        this.tipoAlerta = tipoAlerta;
        this.nivelPrioridad = nivelPrioridad;
        this.fechaGeneracion = LocalDate.now();
        this.revisada = false;
    }

    public void marcarComoRevisada(){
        this.revisada = true;
    }

    @Override
    public int compareTo(Alerta otraAlerta) {
        return Integer.compare(otraAlerta.nivelPrioridad, this.nivelPrioridad);
    }

    public String getIdAlerta() {
        return idAlerta;
    }
    public String getMensaje() {
        return mensaje;
    }
    public String getTipoAlerta() {
        return tipoAlerta;
    }
    public int getNivelPrioridad() {
        return nivelPrioridad;
    }
    public LocalDate getFechaGeneracion() {
        return fechaGeneracion;
    }
    public boolean isRevisada() {
        return revisada;
    }
}
