package co.edu.uniquindio.proptech.repository;

import co.edu.uniquindio.proptech.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {
}
