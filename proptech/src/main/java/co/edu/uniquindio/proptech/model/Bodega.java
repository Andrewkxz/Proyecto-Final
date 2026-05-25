package co.edu.uniquindio.proptech.model;

public class Bodega extends Inmueble {

    private double capacidadAlmacenamiento;

    public Bodega(String codigo, String direccion, String ciudad, String barrioZona, String finalidad,
                  double precio, double area, int habitaciones, int baños, String estado,
                  boolean disponibilidad, Asesor asesorResponsable, double capacidadAlmacenamiento) {

        super(codigo, direccion, ciudad, barrioZona, finalidad, precio, area, habitaciones, baños,
                estado, disponibilidad, asesorResponsable);

        this.capacidadAlmacenamiento = capacidadAlmacenamiento;
    }

    public double getCapacidadAlmacenamiento() {
        return capacidadAlmacenamiento;
    }

    public void setCapacidadAlmacenamiento(double capacidadAlmacenamiento) {
        this.capacidadAlmacenamiento = capacidadAlmacenamiento;
    }

    @Override
    public String obtenerDetalles() {
        return "Bodega - Código: " + codigo +
                ", Dirección: " + direccion +
                ", Ciudad: " + ciudad +
                ", Precio: $" + precio +
                ", Capacidad: " + capacidadAlmacenamiento + " m³";
    }
}