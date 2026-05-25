package co.edu.uniquindio.proptech;

public class Oficina extends Inmueble {

    private int numeroCubiculos;

    public Oficina(String codigo, String direccion, String ciudad, String barrioZona, String finalidad,
                   double precio, double area, int habitaciones, int baños, String estado,
                   boolean disponibilidad, Asesor asesorResponsable, int numeroCubiculos) {

        super(codigo, direccion, ciudad, barrioZona, finalidad, precio, area, habitaciones, baños,
                estado, disponibilidad, asesorResponsable);

        this.numeroCubiculos = numeroCubiculos;
    }

    public int getNumeroCubiculos() {
        return numeroCubiculos;
    }

    public void setNumeroCubiculos(int numeroCubiculos) {
        this.numeroCubiculos = numeroCubiculos;
    }

    @Override
    public String obtenerDetalles() {
        return "Oficina - Código: " + codigo +
                ", Dirección: " + direccion +
                ", Ciudad: " + ciudad +
                ", Precio: $" + precio +
                ", Cubículos: " + numeroCubiculos;
    }
}