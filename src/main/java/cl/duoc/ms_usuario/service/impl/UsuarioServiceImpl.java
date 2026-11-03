package cl.duoc.ms_usuario.service.impl;

import cl.duoc.ms_usuario.dto.*;
import cl.duoc.ms_usuario.entity.UsuarioEntity;
import cl.duoc.ms_usuario.repository.UsuarioRepository;
import cl.duoc.ms_usuario.security.JwtService;
import cl.duoc.ms_usuario.service.UsuarioService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioServiceImpl(UsuarioRepository userRepository, 
                              PasswordEncoder passwordEncoder, 
                              JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public UsuarioResponseDTO registrar(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("El nombre de usuario ya existe.");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }

        // Asignación de rol por defecto si no es ROLE_ADMIN
        String assignedRole = (request.role() != null && request.role().equalsIgnoreCase("ROLE_ADMIN"))
                ? "ROLE_ADMIN"
                : "ROLE_USER";

        UsuarioEntity user = new UsuarioEntity(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                assignedRole
        );

        UsuarioEntity guardado = userRepository.save(user);
        return UsuarioResponseDTO.fromEntity(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(AuthRequest request) {
        UsuarioEntity user = userRepository.findByUsername(request.username())
                .filter(u -> passwordEncoder.matches(request.password(), u.getPassword()))
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas."));

        String token = jwtService.generateToken(user.getUsername(), user.getRole(), user.getId());
        return new AuthResponse(token, user.getUsername(), user.getRole());
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtenerPorId(Long id) {
        return userRepository.findById(id)
                .map(UsuarioResponseDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return userRepository.findAll().stream()
                .map(UsuarioResponseDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizar(Long id, UserUpdateRequest request) {
        UsuarioEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        if (request.email() != null && !request.email().isBlank()) {
            user.setEmail(request.email());
        }
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        UsuarioEntity actualizado = userRepository.save(user);
        return UsuarioResponseDTO.fromEntity(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        userRepository.deleteById(id);
    }
}