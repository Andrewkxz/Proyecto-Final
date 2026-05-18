package co.edu.uniquindio.proptech;

public class Casa extends Inmueble{
    private boolean tieneGaraje;

    public Casa(String codigo, String direccion, String ciudad, String barrioZona, String finalidad,
                double precio, double area, int habitaciones, int baños, String estado,
                boolean disponibilidad, Asesor asesorResponsable, boolean tieneGaraje) {

        super(codigo, direccion, ciudad, barrioZona, finalidad, precio, area, habitaciones, baños,
                estado, disponibilidad, asesorResponsable);

        this.tieneGaraje = tieneGaraje;
    }

    public boolean isTieneGaraje() {
        return tieneGaraje;
    }

    public void setTieneGaraje(boolean tieneGaraje) {
        this.tieneGaraje = tieneGaraje;
    }

    @Override
    public String obtenerDetalles() {
        return "Casa - Código: " + codigo +
                ", Dirección: " + direccion +
                ", Ciudad: " + ciudad +
                ", Precio: $" + precio +
                ", Garaje: " + (tieneGaraje ? "Sí" : "No");
    }
    
}
