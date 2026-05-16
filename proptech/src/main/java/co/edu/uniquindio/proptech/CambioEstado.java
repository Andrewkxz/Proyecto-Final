package co.edu.uniquindio.proptech;

public class CambioEstado {
    public static final int TIPO_REGISTRO_INMUEBLE = 1;      // Deshacer = Eliminar
    public static final int TIPO_ELIMINACION_INMUEBLE = 2;   // Deshacer = Re-insertar
    public static final int TIPO_MODIFICACION_ESTADO = 3;    // Deshacer = Restaurar variables
    public static final int TIPO_ACCION_ADMINISTRATIVA = 4;  // Deshacer = Solo notificación

    private int tipoCambio;
    private Object entidad;
    private String descripcionAccion;

    // Snapshot para restaurar estados
    private boolean disponibilidadAnterior;
    private String estadoAnterior;
    private double precioAnterior;

    public CambioEstado(int tipoCambio, Object entidad, String descripcionAccion) {
        this.tipoCambio = tipoCambio;
        this.entidad = entidad;
        this.descripcionAccion = descripcionAccion;

        // Si se modifica el estado (o se hace una venta), tomamos una foto de cómo estaba antes
        if(tipoCambio == TIPO_MODIFICACION_ESTADO && entidad instanceof Inmueble){
            Inmueble inm = (Inmueble) entidad;
            this.disponibilidadAnterior = inm.isDisponibilidad();
            this.estadoAnterior = inm.getEstado();
            this.precioAnterior = inm.getPrecio();
        }
    }

    // Getters
    public int getTipoCambio() { return tipoCambio; }
    public Object getEntidad() { return entidad; }
    public String getDescripcionAccion(){ return descripcionAccion; }
    public boolean isDisponibilidadAnterior() { return disponibilidadAnterior; }
    public String getEstadoAnterior() { return estadoAnterior; }
    public double getPrecioAnterior() { return precioAnterior; }
}