package com.gestion.fintech.services;

import com.gestion.fintech.model.Usuario;
import com.gestion.fintech.repository.UsuarioRepository;
import com.gestion.fintech.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsuarioServiceTest {

    @InjectMocks
    private UsuarioService usuarioService;

    @Mock
    private UsuarioRepository usuarioRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegistrarUsuario_Success() {
        String username = "christian.bravo";
        String password = "securePassword";
        String nombreCompleto = "Christian Bravo";


        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.empty());


        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(1L);
            return usuario;
        });

        Usuario usuarioGuardado = usuarioService.registrarUsuario(username, password, nombreCompleto);

        System.out.println(usuarioGuardado);


        assertNotNull(usuarioGuardado);
        assertEquals(username, usuarioGuardado.getUsername());
        assertTrue(passwordEncoder.matches(password, usuarioGuardado.getPasswordHash()));

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }


    @Test
    void testRegistrarUsuario_UsuarioYaExiste() {
        String username = "christian.bravo";
        String password = "securePassword";
        String nombreCompleto = "Christian Bravo";

        Usuario existingUser = new Usuario();
        existingUser.setUsername(username);

        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.of(existingUser));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            usuarioService.registrarUsuario(username, password, nombreCompleto);
        });

        assertEquals("El usuario ya existe.", thrown.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testAutenticarUsuario_Success() {
        String username = "christian.bravo";
        String password = "securePassword";

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPasswordHash(passwordEncoder.encode(password));

        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = usuarioService.autenticarUsuario(username, password);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
    }

    @Test
    void testAutenticarUsuario_Fail() {
        String username = "christian.bravo";
        String password = "wrongPassword";

        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.empty());

        Optional<Usuario> result = usuarioService.autenticarUsuario(username, password);

        assertFalse(result.isPresent());
    }

    @Test
    void testLoadUserByUsername_Success() {
        String username = "christian.bravo";
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setPasswordHash(passwordEncoder.encode("securePassword"));

        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.of(usuario));

        UserDetails userDetails = usuarioService.loadUserByUsername(username);

        assertEquals(username, userDetails.getUsername());
    }

    @Test
    void testLoadUserByUsername_NotFound() {
        String username = "nonexistent.user";

        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.empty());

        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> {
            usuarioService.loadUserByUsername(username);
        });

        assertEquals("Usuario no encontrado: nonexistent.user", thrown.getMessage());
    }
}
