package co.edu.uniquindio.proptech.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import co.edu.uniquindio.proptech.*;
import co.edu.uniquindio.proptech.repository.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepo;
    private final AsesorRepository asesorRepo;
    private final ClienteRepository clienteRepo;
    private final InmuebleRepository inmuebleRepo;

    public DataInitializer(UsuarioRepository usuarioRepo, AsesorRepository asesorRepo,
                           ClienteRepository clienteRepo, InmuebleRepository inmuebleRepo) {
        this.usuarioRepo = usuarioRepo;
        this.asesorRepo = asesorRepo;
        this.clienteRepo = clienteRepo;
        this.inmuebleRepo = inmuebleRepo;
    }

    @Override
    public void run(String... args) {
        if (asesorRepo.count() > 0) {
            System.out.println("La base de datos ya contiene datos. Omitiendo inicialización.");
            return;
        }

        System.out.println("Sembrando base de datos con datos iniciales...");

        // --- Asesores ---
        Asesor admin = new Asesor("ADMIN-01", "Admin Sup", "000", "General");
        Asesor asesor1 = new Asesor("A-101", "Juli", "3112345678", "Norte");
        Asesor asesor2 = new Asesor("A-102", "Juan", "3128765432", "Centro");

        asesorRepo.save(admin);
        asesorRepo.save(asesor1);
        asesorRepo.save(asesor2);

        // --- Clientes ---
        Cliente cliente1 = new Cliente("C-001", "Andrés", "andres@aura.com", "3001112222",
                "Comprador", 300000000.0, "Apartamento", 2);
        Cliente cliente2 = new Cliente("C-002", "Nathaly", "nat@aura.com", "3003334444",
                "Inversionista", 600000000.0, "LocalComercial", 0);

        clienteRepo.save(cliente1);
        clienteRepo.save(cliente2);

        // --- Inmuebles ---
        inmuebleRepo.save(new Apartamento("APT-001", "Calle 10N #14-20", "Armenia", "Norte",
                "Venta", 250000000.0, 65.0, 3, 2, "Nuevo", true, asesor1, true, 200000.0));
        inmuebleRepo.save(new Apartamento("APT-002", "Av. Centenario", "Armenia", "Norte",
                "Arriendo", 1500000.0, 50.0, 2, 1, "Usado", true, asesor1, false, 150000.0));
        inmuebleRepo.save(new LocalComercial("LOC-001", "Carrera 14 #23-00", "Armenia", "Centro",
                "Venta", 500000000.0, 120.0, 1, 2, "Remodelado", true, asesor2, true, "Comercial Mixto"));
        inmuebleRepo.save(new Casa("CAS-001", "Cra 19 #12-45", "Armenia", "La Castellana",
                "Venta", 380000000.0, 140.0, 4, 3, "Nuevo", true, asesor1, true));
        inmuebleRepo.save(new Casa("CAS-002", "Barrio Granada", "Armenia", "Sur",
                "Arriendo", 2200000.0, 110.0, 3, 2, "Usado", true, asesor2, false));
        inmuebleRepo.save(new Casa("CAS-003", "Conjunto Portal del Edén", "Armenia", "Occidente",
                "Venta", 420000000.0, 160.0, 5, 4, "Remodelado", true, asesor1, true));
        inmuebleRepo.save(new Casa("CAS-004", "Cra 13 #8-55", "Calarcá", "Centro",
                "Arriendo", 1800000.0, 95.0, 3, 2, "Usado", true, asesor1, false));
        inmuebleRepo.save(new Bodega("BOD-001", "Zona Industrial El Caimo", "Armenia", "Industrial",
                "Venta", 650000000.0, 300.0, 2, 2, "Usado", true, asesor2, 500.0));
        inmuebleRepo.save(new Bodega("BOD-002", "Km 3 Vía La Tebaida", "Armenia", "Zona Industrial",
                "Arriendo", 4800000.0, 450.0, 1, 1, "Usado", true, asesor2, 750.0));
        inmuebleRepo.save(new Oficina("OFI-001", "Edificio Mocawa Plaza", "Armenia", "Centro",
                "Arriendo", 3200000.0, 85.0, 4, 2, "Nuevo", true, asesor1, 8));
        inmuebleRepo.save(new Oficina("OFI-002", "Edificio Banco de Occidente", "Armenia", "Centro",
                "Venta", 290000000.0, 70.0, 3, 1, "Usado", true, asesor1, 6));
        inmuebleRepo.save(new Lote("LOT-001", "Vía Armenia - Circasia", "Armenia", "Periferia",
                "Venta", 180000000.0, 600.0, 0, 0, "Nuevo", true, asesor2, true));
        inmuebleRepo.save(new Lote("LOT-002", "Vereda El Caimo", "Armenia", "Rural",
                "Venta", 95000000.0, 1200.0, 0, 0, "Nuevo", true, asesor2, false));

        // --- Usuarios ---
        usuarioRepo.save(new Usuario("admin", "admin123", "ADMIN", "ADMIN-01"));
        usuarioRepo.save(new Usuario("juli", "1234", "ASESOR", "A-101"));
        usuarioRepo.save(new Usuario("juan", "1234", "ASESOR", "A-102"));
        usuarioRepo.save(new Usuario("andres", "pass", "CLIENTE", "C-001"));
        usuarioRepo.save(new Usuario("nathaly", "pass", "CLIENTE", "C-002"));

        System.out.println("Base de datos inicializada con éxito.");
    }
}
