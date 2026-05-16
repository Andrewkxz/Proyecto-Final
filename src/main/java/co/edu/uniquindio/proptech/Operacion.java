package co.edu.uniquindio.proptech;

import java.time.LocalDate;

public class Operacion implements Comparable<Operacion>{
    public static final String TIPO_VENTA = "Venta";
    public static final String TIPO_ARRIENDO = "Arriendo";
    public static final String TIPO_RENOVACION = "Renovación de Contrato";
    public static final String TIPO_CANCELACION = "Cancelación de Negocio";

    public static final String ESTADO_EN_TRAMITE = "En trámite";
    public static final String ESTADO_FINALIZADO = "Finalizado";
    public static final String ESTADO_CAIDO = "Caído/revertido";

    private String id;
    private Inmueble inmuebleRelacionado;
    private Cliente cliente;
    private Asesor asesor;
    private LocalDate fecha;
    private String tipoOperacion;
    private double valorAcordado;
    private double comision;
    private String estadoProceso;
    
    public Operacion(String id, Inmueble inmuebleRelacionado, Cliente cliente, Asesor asesor, LocalDate fecha, String tipoOperacion, double valorAcordado, double porcentajeComision){
        this.id = id;
        this.inmuebleRelacionado = inmuebleRelacionado;
        this.cliente = cliente;
        this.asesor = asesor;
        this.fecha = fecha;
        this.tipoOperacion = tipoOperacion;
        this.valorAcordado = valorAcordado;

        this.comision = valorAcordado * (porcentajeComision / 100.0);
        this.estadoProceso = ESTADO_EN_TRAMITE; // Por defecto
    }

    public void finalizarOperacion(){
        this.estadoProceso = ESTADO_FINALIZADO;
        // Si es venta o arriendo, el inmueble deja de estar disponible
        if(tipoOperacion.equals(TIPO_VENTA) || tipoOperacion.equals(TIPO_ARRIENDO)){
            this.inmuebleRelacionado.setDisponibilidad(false);
        }
    }

    public void revertirOperacion(){
        this.estadoProceso = ESTADO_CAIDO;
        this.inmuebleRelacionado.setDisponibilidad(true);
    }

    public String getId() {
        return id;
    }
    public Inmueble getInmuebleRelacionado() {
        return inmuebleRelacionado;
    }
    public Cliente getCliente() {
        return cliente;
    }
    public Asesor getAsesor() {
        return asesor;
    }
    public LocalDate getFecha() {
        return fecha;
    }
    public String getTipoOperacion() {
        return tipoOperacion;
    }
    public double getValorAcordado() {
        return valorAcordado;
    }
    public double getComision() {
        return comision;
    }
    public String getEstadoProceso() {
        return estadoProceso;
    }

    @Override
    public int compareTo(Operacion otraOperacion) {
        return this.fecha.compareTo(otraOperacion.fecha);
    }
}
