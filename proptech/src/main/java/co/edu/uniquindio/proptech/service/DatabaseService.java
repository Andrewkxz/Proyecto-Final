package co.edu.uniquindio.proptech.service;

import java.util.List;

import org.springframework.stereotype.Service;

import co.edu.uniquindio.proptech.*;
import co.edu.uniquindio.proptech.repository.*;

@Service
public class DatabaseService {

    private final UsuarioRepository usuarioRepo;
    private final InmuebleRepository inmuebleRepo;
    private final ClienteRepository clienteRepo;
    private final AsesorRepository asesorRepo;
    private final VisitaRepository visitaRepo;
    private final OperacionRepository operacionRepo;

    public DatabaseService(UsuarioRepository usuarioRepo, InmuebleRepository inmuebleRepo,
                           ClienteRepository clienteRepo, AsesorRepository asesorRepo,
                           VisitaRepository visitaRepo, OperacionRepository operacionRepo) {
        this.usuarioRepo = usuarioRepo;
        this.inmuebleRepo = inmuebleRepo;
        this.clienteRepo = clienteRepo;
        this.asesorRepo = asesorRepo;
        this.visitaRepo = visitaRepo;
        this.operacionRepo = operacionRepo;
    }

    // --- Usuarios ---
    public List<Usuario> loadUsuarios() { return usuarioRepo.findAll(); }
    public void saveUsuario(Usuario u) { usuarioRepo.save(u); }

    // --- Inmuebles ---
    public List<Inmueble> loadInmuebles() { return inmuebleRepo.findAll(); }
    public void saveInmueble(Inmueble i) { inmuebleRepo.save(i); }
    public void deleteInmueble(Inmueble i) { inmuebleRepo.delete(i); }

    // --- Clientes ---
    public List<Cliente> loadClientes() { return clienteRepo.findAll(); }
    public void saveCliente(Cliente c) { clienteRepo.save(c); }
    public void deleteCliente(Cliente c) { clienteRepo.delete(c); }

    // --- Asesores ---
    public List<Asesor> loadAsesores() { return asesorRepo.findAll(); }
    public void saveAsesor(Asesor a) { asesorRepo.save(a); }

    // --- Visitas ---
    public List<Visita> loadVisitas() { return visitaRepo.findAll(); }
    public void saveVisita(Visita v) { visitaRepo.save(v); }

    // --- Operaciones ---
    public List<Operacion> loadOperaciones() { return operacionRepo.findAll(); }
    public void saveOperacion(Operacion o) { operacionRepo.save(o); }
}
