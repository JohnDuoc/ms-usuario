package cl.duoc.ms_usuario.dto;

import java.time.LocalDateTime;
import cl.duoc.ms_usuario.entity.UsuarioEntity;

public class UsuarioResponseDTO {

    private Long id;
    private String username;
    private String email;
    private String role;
    private LocalDateTime createdAt;

    public UsuarioResponseDTO() {}

    public UsuarioResponseDTO(Long id, String username, String email, String role, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.createdAt = createdAt;
    }

    public static UsuarioResponseDTO fromEntity(UsuarioEntity user) {
        return new UsuarioResponseDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            user.getCreatedAt()
        );
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}