package co.edu.uniquindio.proptech;

public class CambioEstado {
    public static final int TIPO_REGISTRO_INMUEBLE = 1;
    public static final int TIPO_REGISTRO_CLIENTE = 2;
    public static final int TIPO_MODIFICACION_ESTADO = 3;

    private int tipoCambio;
    private Object entidad;
    private String descripcionAccion;

    private boolean disponibilidadAnterior;
    private String estadoAnterior;
    private double precioAnterior;
    

    public CambioEstado(int tipoCambio, Object entidad, String descripcionAccion) {
        this.tipoCambio = tipoCambio;
        this.entidad = entidad;
        this.descripcionAccion = descripcionAccion;

        if(tipoCambio == TIPO_MODIFICACION_ESTADO && entidad instanceof Inmueble){
            Inmueble inm = (Inmueble) entidad;
            this.disponibilidadAnterior = inm.isDisponibilidad();
            this.estadoAnterior = inm.getEstado();
            this.precioAnterior = inm.getPrecio();
        }
    }

    public void restaurar(){
        if(tipoCambio == TIPO_MODIFICACION_ESTADO && entidad instanceof Inmueble){
            Inmueble inm = (Inmueble) entidad;
            inm.setDisponibilidad(disponibilidadAnterior);
            inm.setEstado(estadoAnterior);
            inm.setPrecio(precioAnterior);
        }
    }

    public int getTipoCambio() {
        return tipoCambio;
    }
    public Object getEntidad() {
        return entidad;
    }
    public String getDescripcionAccion(){
        return descripcionAccion;
    }
}
