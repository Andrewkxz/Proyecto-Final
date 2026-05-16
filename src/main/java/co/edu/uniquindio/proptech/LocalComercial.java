package co.edu.uniquindio.proptech;

public class LocalComercial extends Inmueble{
    private boolean frenteCalle;
    private String categoria; // "Comidas", "Ropa", "Mixto"

    public LocalComercial(String codigo, String direccion, String ciudad, String barrioZona, String finalidad, double precio,
            double area, int habitaciones, int baños, String estado, boolean disponibilidad, Asesor asesorResponsable, boolean frenteCalle, String categoria){
                super(codigo, direccion, ciudad, barrioZona, finalidad, precio, area, habitaciones, baños, estado, disponibilidad, asesorResponsable);
                this.frenteCalle = frenteCalle;
                this.categoria = categoria;
            }

    @Override
    public String obtenerDetalles() {
        return "Local Comercial - Frente a calle: " + (frenteCalle ? "Sí" : "No") + ", Categoría: " + categoria;
    }
}
