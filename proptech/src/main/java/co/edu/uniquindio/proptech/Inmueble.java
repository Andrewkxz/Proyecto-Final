package co.edu.uniquindio.proptech;

import co.edu.uniquindio.proptech.LinkedSimpleList.LinkedSimpleList;
import jakarta.persistence.*;

@Entity
@Table(name = "inmueble")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_inmueble")
public abstract class Inmueble implements Comparable<Inmueble>{
    @Id
    protected String codigo;
    protected String direccion;
    protected String ciudad;
    protected String barrioZona;
    protected String finalidad;
    protected double precio;
    protected double area;
    protected int habitaciones;
    protected int baños;
    protected String estado;
    protected boolean disponibilidad;

    @ManyToOne
    @JoinColumn(name = "asesor_id")
    protected Asesor asesorResponsable;

    @Transient
    protected LinkedSimpleList<Visita> historialVisitas;

    public Inmueble() {
        this.historialVisitas = new LinkedSimpleList<>();
    }

    public Inmueble(String codigo, String direccion, String ciudad, String barrioZona, String finalidad, double precio,
            double area, int habitaciones, int baños, String estado, boolean disponibilidad, Asesor asesorResponsable) {
        this();
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
    }

    public abstract String obtenerDetalles();

    @Override
    public int compareTo(Inmueble otroInmueble){
        return Double.compare(this.precio, otroInmueble.precio);
    }
    public String getCodigo() { return codigo; }
    public String getDireccion() { return direccion; }
    public String getCiudad() { return ciudad; }
    public String getBarrioZona() { return barrioZona; }
    public String getFinalidad() { return finalidad; }
    public double getArea() { return area; }
    public int getHabitaciones() { return habitaciones; }
    public int getBaños() { return baños; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Asesor getAsesorResponsable() { return asesorResponsable; }
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    public boolean isDisponibilidad() { return disponibilidad; }
    public void setDisponibilidad(boolean disponibilidad) { this.disponibilidad = disponibilidad; }
    public LinkedSimpleList<Visita> getHistorialVisitas() { return historialVisitas; }
    public void registrarVisita(Visita visita){ this.historialVisitas.addLast(visita); }
    @Override
    public String toString(){
        return this.getClass().getSimpleName() + " " + this.codigo + " (Precio: $" + String.format("%,.0f", this.precio) + ") ";
    }
}
