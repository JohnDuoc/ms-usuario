package cl.duoc.ms_usuario.service;

import cl.duoc.ms_usuario.dto.*;
import java.util.List;

public interface UsuarioService {

    UsuarioResponseDTO registrar(RegisterRequest request);
    AuthResponse login(AuthRequest request);
    UsuarioResponseDTO actualizar(Long id, UserUpdateRequest request);
    UsuarioResponseDTO obtenerPorId(Long id);
    List<UsuarioResponseDTO> listarTodos();
    void eliminar(Long id);
}