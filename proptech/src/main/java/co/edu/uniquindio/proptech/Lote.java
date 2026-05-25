package co.edu.uniquindio.proptech;

public class Lote extends Inmueble {

    private boolean tieneServiciosPublicos;

    public Lote(String codigo, String direccion, String ciudad, String barrioZona, String finalidad,
                double precio, double area, int habitaciones, int baños, String estado,
                boolean disponibilidad, Asesor asesorResponsable, boolean tieneServiciosPublicos) {

        super(codigo, direccion, ciudad, barrioZona, finalidad, precio, area, habitaciones, baños,
                estado, disponibilidad, asesorResponsable);

        this.tieneServiciosPublicos = tieneServiciosPublicos;
    }

    public boolean isTieneServiciosPublicos() {
        return tieneServiciosPublicos;
    }

    public void setTieneServiciosPublicos(boolean tieneServiciosPublicos) {
        this.tieneServiciosPublicos = tieneServiciosPublicos;
    }

    @Override
    public String obtenerDetalles() {
        return "Lote - Código: " + codigo +
                ", Dirección: " + direccion +
                ", Ciudad: " + ciudad +
                ", Precio: $" + precio +
                ", Servicios públicos: " + (tieneServiciosPublicos ? "Sí" : "No");
    }
}