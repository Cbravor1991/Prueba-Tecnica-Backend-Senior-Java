package com.gestion.fintech.service;

import com.gestion.fintech.exception.UsuarioException;
import com.gestion.fintech.model.Usuario;
import com.gestion.fintech.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    protected UsuarioRepository usuarioRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public Usuario registrarUsuario(String username, String password, String nombreCompleto) {
        logger.info("Registrando nuevo usuario: {}", username);

        if (usuarioRepository.findByUsername(username).isPresent()) {
            throw new UsuarioException("El usuario ya existe.");
        }

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPasswordHash(passwordEncoder.encode(password));
        usuario.setNombreTitular(nombreCompleto);
        usuario.setRole("USER");

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        logger.info("Usuario registrado exitosamente: {}", username);

        return usuarioGuardado;
    }

    public Optional<Usuario> autenticarUsuario(String username, String password) {
        logger.info("Autenticando usuario: {}", username);

        Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
        if (usuario.isPresent() && passwordEncoder.matches(password, usuario.get().getPasswordHash())) {
            logger.info("Autenticación exitosa para el usuario: {}", username);
            return usuario;
        }


        throw new UsuarioException("Credenciales inválidas");
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        logger.info("Cargando detalles del usuario: {}", username);

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
                .orElseThrow(() -> {
                    return new UsuarioException("Usuario no encontrado: " + username);
                });
    }

    public Optional<Usuario> obtenerUsuarioPorUsername(String username) {
        logger.info("Obteniendo usuario por nombre de usuario: {}", username);
        return usuarioRepository.findByUsername(username);
    }
}
