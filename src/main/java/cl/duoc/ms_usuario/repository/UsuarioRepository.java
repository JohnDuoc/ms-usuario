package cl.duoc.ms_usuario.repository;

import cl.duoc.ms_usuario.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    // Buscar usuario por su nombre de usuario (para login y validaciones)
    Optional<UsuarioEntity> findByUsername(String username);

    // Buscar usuario por su correo electrónico
    Optional<UsuarioEntity> findByEmail(String email);

    // Verificar si ya existe un nombre de usuario registrado
    boolean existsByUsername(String username);

    // Verificar si ya existe un correo electrónico registrado
    boolean existsByEmail(String email);
}