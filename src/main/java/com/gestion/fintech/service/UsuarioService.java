package com.gestion.fintech.service;

import com.gestion.fintech.model.Usuario;
import com.gestion.fintech.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    @Autowired
    protected UsuarioRepository usuarioRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Usuario registrarUsuario(String username, String password, String nombreCompleto) {
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPasswordHash(passwordEncoder.encode(password));
        usuario.setNombreTitular(nombreCompleto);
        usuario.setRole("USER");
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> autenticarUsuario(String username, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        if (usuario.isPresent() && passwordEncoder.matches(password, usuario.get().getPasswordHash())) {
            return usuario;
        }
        return Optional.empty();
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        // Busca el usuario en el repositorio
        return usuarioRepository.findByUsername(username)
                .map(usuario -> org.springframework.security.core.userdetails.User
                        .withUsername(usuario.getUsername())
                        .password(usuario.getPasswordHash())
                        .authorities("ROLE_USER")
                        .accountExpired(false)
                        .accountLocked(false)
                        .credentialsExpired(false)
                        .disabled(false)
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

    // Método agregado para obtener un usuario por su nombre de usuario
    public Optional<Usuario> obtenerUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }
}
