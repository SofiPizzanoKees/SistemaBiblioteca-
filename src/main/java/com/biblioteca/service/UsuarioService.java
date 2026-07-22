package com.biblioteca.service;

import com.biblioteca.model.Usuario;
import com.biblioteca.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // RF01: login con DNI + contraseña (aplica a los 3 perfiles, incluido el bibliotecario)
    public Optional<Usuario> login(String dni, String contrasenia) {
        return usuarioRepository.findByDniAndActivoTrue(dni)
                .filter(u -> u.getContrasenia().equals(contrasenia));
    }

    // RF03: alta de usuario (Alumno, Profesor o Bibliotecario, ya construidos por el llamador)
    public Usuario guardar(Usuario usuario) {
        if (usuarioRepository.existsByDni(usuario.getDni()) && usuario.getId() == null) {
            throw new IllegalArgumentException("Ya existe un usuario con ese DNI");
        }
        return usuarioRepository.save(usuario);
    }

    // RF03: baja logica
    public void darDeBaja(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + id));
    }

    public List<Usuario> listarActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    // Usado por los controllers para saber quien esta logueado
    public Usuario usuarioDeSesion(HttpSession session) {
        Long id = (Long) session.getAttribute("usuarioId");
        return buscarPorId(id);
    }
}
