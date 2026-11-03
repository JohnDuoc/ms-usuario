package cl.duoc.ms_usuario.controller;

import cl.duoc.ms_usuario.dto.AuthRequest;
import cl.duoc.ms_usuario.dto.AuthResponse;
import cl.duoc.ms_usuario.dto.RegisterRequest;
import cl.duoc.ms_usuario.dto.UserUpdateRequest;
import cl.duoc.ms_usuario.entity.UsuarioEntity;
import cl.duoc.ms_usuario.repository.UsuarioRepository;
import cl.duoc.ms_usuario.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UsuarioController {

    private final UsuarioRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // 1. Corregido: El constructor ahora se llama igual que la clase (UsuarioController)
    public UsuarioController(UsuarioRepository userRepository, 
                             PasswordEncoder passwordEncoder, 
                             JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // 1. Registro de usuario (ROLE_USER o ROLE_ADMIN)
    @PostMapping("/auth/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            return ResponseEntity.badRequest().body("El nombre de usuario ya existe.");
        }
        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.badRequest().body("El email ya está registrado.");
        }

        // Asigna ROLE_USER por defecto salvo que se indique explícitamente ROLE_ADMIN
        String assignedRole = (request.role() != null && request.role().equalsIgnoreCase("ROLE_ADMIN")) 
                ? "ROLE_ADMIN" 
                : "ROLE_USER";

        
        UsuarioEntity user = new UsuarioEntity(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                assignedRole
        );
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado exitosamente.");
    }

    // 2. Inicio de sesión y generación de JWT
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        return userRepository.findByUsername(request.username())
                .filter(u -> passwordEncoder.matches(request.password(), u.getPassword()))
                .map(u -> {
                    String token = jwtService.generateToken(u.getUsername(), u.getRole(), u.getId());
                    return ResponseEntity.ok(new AuthResponse(token, u.getUsername(), u.getRole()));
                })
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse(null, null, "Credenciales inválidas.")));
    }

    // 3. Modificación de usuario por ID
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
        return userRepository.findById(id).map(user -> {
            if (request.email() != null && !request.email().isBlank()) {
                user.setEmail(request.email());
            }
            if (request.password() != null && !request.password().isBlank()) {
                user.setPassword(passwordEncoder.encode(request.password()));
            }
            userRepository.save(user);
            return ResponseEntity.ok("Usuario actualizado correctamente.");
        }).orElse(ResponseEntity.notFound().build());
    }

    // 4. Eliminación de usuario por ID
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok("Usuario eliminado correctamente.");
    }
}