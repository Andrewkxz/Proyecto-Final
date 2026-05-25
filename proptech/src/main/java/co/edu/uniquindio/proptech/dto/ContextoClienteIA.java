package co.edu.uniquindio.proptech.dto;

public class ContextoClienteIA {
    private String ciudad;
    private String tipoInmueble;
    private Double presupuesto;
    private Integer habitaciones;
    private String finalidad;
    private String zona;

    public ContextoClienteIA() {
        // Constructor vacío para deserialización
    }

    public ContextoClienteIA(String ciudad, String tipoInmueble, Double presupuesto, Integer habitaciones, String finalidad, String zona) {
        this.ciudad = ciudad;
        this.tipoInmueble = tipoInmueble;
        this.presupuesto = presupuesto;
        this.habitaciones = habitaciones;
        this.finalidad = finalidad;
        this.zona = zona;
    }

    public String getCiudad() {
        return ciudad;
    }
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
    public String getTipoInmueble() {
        return tipoInmueble;
    }
    public void setTipoInmueble(String tipoInmueble) {
        this.tipoInmueble = tipoInmueble;
    }
    public Double getPresupuesto() {
        return presupuesto;
    }
    public void setPresupuesto(Double presupuesto) {
        this.presupuesto = presupuesto;
    }
    public Integer getHabitaciones() {
        return habitaciones;
    }
    public void setHabitaciones(Integer habitaciones) {
        this.habitaciones = habitaciones;
    }
    public String getFinalidad() {
        return finalidad;
    }
    public void setFinalidad(String finalidad) {
        this.finalidad = finalidad;
    }
    public String getZona() {
        return zona;
    }
    public void setZona(String zona) {
        this.zona = zona;
    }
}