package co.edu.uniquindio.proptech;

import java.time.LocalDate;

import co.edu.uniquindio.proptech.LinkedSimpleList.LinkedSimpleList;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== SISTEMA PROPTECH ===");
        Inmobiliaria plataforma = new Inmobiliaria();

        Asesor asesor1 = new Asesor("A-101", "Juli", "111", "Norte");
        Asesor asesor2 = new Asesor("A-102", "Juan", "222", "Centro");

        plataforma.registrarAsesor(asesor1);
        plataforma.registrarAsesor(asesor2);
        System.out.println("-> Asesores registrados.");
        

        Apartamento apt1 = new Apartamento("APT-001", "Calle 10N", "Armenia", "Norte", "Venta", 250000000.0, 65.0, 3, 2, "Nuevo", true, asesor1, true, 200000.0);
        Apartamento apt2 = new Apartamento("APT-002", "Av Centenario", "Armenia", "Norte", "Arriendo", 1500000.0, 50.0, 2, 1, "Usado", true, asesor1, false, 150000.0);
        LocalComercial loc1 = new LocalComercial("LOC-001", "Carrera 14", "Armenia", "Centro", "Venta", 500000000.0, 120.0, 1, 2, "Remodelado", true, asesor2, true, "Comercial Mixto");

        plataforma.registrarInmueble(apt1);
        plataforma.registrarInmueble(apt2);
        plataforma.registrarInmueble(loc1);
        System.out.println("Inmuebles registrados.");

        Cliente cliente1 = new Cliente("C-001", "Andrés", "andres@gmail.com", "333", "Comprador", 300000000.0, "Apartamento", 2);
        Cliente cliente2 = new Cliente("C-002", "Nathaly", "nat@gmail.com", "444", "Inversionista", 600000000.0, "LocalComercial", 0);

        plataforma.registrarCliente(cliente1);
        plataforma.registrarCliente(cliente2);
        System.out.println("Clientes registrados.");

        cliente1.getHistorialConsultas().addLast(apt1);
        plataforma.conectarClientesConInmuebles(cliente1.getId(), apt1.getCodigo());

        for(int i = 0; i < 12; i++){
            Visita v = new Visita(cliente2, loc1, LocalDate.now(), "10:00 AM", asesor2, 1);
            loc1.registrarVisita(v);
        }
        System.out.println("Interacciones y visitas simuladas.");

        // ===========================================================================================================
        // Ejecución de métodos analíticos y algoritmos
        // ===========================================================================================================
        System.out.println("\n=== RESULTADOS ANALÍTICOS ===");

        System.out.println("\n[A] Recomendaciones para: " + cliente1.getNombre());
        LinkedSimpleList<Inmueble> recomendaciones = plataforma.generarRecomendaciones(cliente1.getId());
        if(recomendaciones.getSize() == 0){
            System.out.println("No hay recomendaciones disponibles.");
        } else {
            for(int i = 0; i < recomendaciones.getSize(); i++){
                System.out.println("- Recomendado: " + recomendaciones.getData(i));
            }
        }

        System.out.println("\n[B] Búsqueda de inmuebles (Precio < 300M, Zona: Norte, Hab >= 2):");
        LinkedSimpleList<Inmueble> filtro = plataforma.buscarInmuebleConFiltros(0, 300000000.0, "Norte", 2);
        for(int i = 0; i < filtro.getSize(); i++){
            System.out.println("- Encontrado: " + filtro.getData(i).getCodigo());
        }

        System.out.println("\n[C] Ejecución Detección de Comportamiento Inusual...");
        plataforma.detectarComportamientosInusuales();
        plataforma.revisarAlertas();

        System.out.println("\n[D] Análisis de Zonas");
        plataforma.simularCrecimientoDemanda("Centro");

        System.out.println("\n=== PRUEBA FINALIZADA CON ÉXITO ===");
    }
}
