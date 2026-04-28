package co.edu.uniquindio.proptech;

import co.edu.uniquindio.proptech.LinkedSimpleList.LinkedSimpleList;

public abstract class Inmueble implements Comparable<Inmueble>{
    protected String codigo;
    protected String direccion;
    protected String ciudad;
    protected String barrioZona;
    protected String finalidad; // Venta o Arriendo
    protected double precio;
    protected double area;
    protected int habitaciones;
    protected int baños;
    protected String estado; // Nuevo, Usado, Remodelado
    protected boolean disponibilidad;

    protected Asesor asesorResponsable;
    protected LinkedSimpleList<Visita> historialVisitas;

    public Inmueble(String codigo, String direccion, String ciudad, String barrioZona, String finalidad, double precio,
            double area, int habitaciones, int baños, String estado, boolean disponibilidad, Asesor asesorResponsable) {
        this.codigo = codigo;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.barrioZona = barrioZona;
        this.finalidad = finalidad;
        this.precio = precio;
        this.area = area;
        this.habitaciones = habitaciones;
        this.baños = baños;
        this.estado = estado;
        this.disponibilidad = disponibilidad;
        this.asesorResponsable = asesorResponsable;

        this.historialVisitas = new LinkedSimpleList<>();
    }

    public abstract String obtenerDetalles();

    @Override
    public int compareTo(Inmueble otroInmueble){
        return Double.compare(this.precio, otroInmueble.precio);
    }
    public String getCodigo() {
        return codigo;
    }
    
    public String getDireccion() {
        return direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getBarrioZona() {
        return barrioZona;
    }

    public String getFinalidad() {
        return finalidad;
    }

    public double getArea() {
        return area;
    }

    public int getHabitaciones() {
        return habitaciones;
    }

    public int getBaños() {
        return baños;
    }

    public String getEstado() {
        return estado;
    }

    public Asesor getAsesorResponsable() {
        return asesorResponsable;
    }

    public double getPrecio() {
        return precio;
    }
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    public boolean isDisponibilidad() {
        return disponibilidad;
    }
    public void setDisponibilidad(boolean disponibilidad) {
        this.disponibilidad = disponibilidad;
    }
    public LinkedSimpleList<Visita> getHistorialVisitas() {
        return historialVisitas;
    }
    public void registrarVisita(Visita visita){
        this.historialVisitas.addLast(visita);
    }
    @Override
    public String toString(){
        return this.getClass().getSimpleName() + " " + this.codigo + " (Precio: $" + String.format("%,.0f", this.precio) + ") ";
    }
}
