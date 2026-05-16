package co.edu.uniquindio.proptech;

public class Apartamento extends Inmueble{
    private boolean tieneAsesor;
    private double valorAdministracion;
    
    public Apartamento(String codigo, String direccion, String ciudad, String barrioZona, String finalidad, double precio,
            double area, int habitaciones, int baños, String estado, boolean disponibilidad, Asesor asesorResponsable, boolean tieneAsesor, double valorAdministracion){
                super(codigo, direccion, ciudad, barrioZona, finalidad, precio, area, habitaciones, baños, estado, disponibilidad, asesorResponsable);
                this.tieneAsesor = tieneAsesor;
                this.valorAdministracion = valorAdministracion;
            }

    @Override
    public String obtenerDetalles() {
        return "Apartamento - Asesor: " + (tieneAsesor ? "Sí" : "No") + ", Administración: $" + valorAdministracion;
    }
}
