package com.gestion.fintech.controller;

import com.gestion.fintech.dto.RegisterDTO;
import com.gestion.fintech.dto.LoginDTO;
import com.gestion.fintech.exception.UsuarioException;
import com.gestion.fintech.model.Usuario;
import com.gestion.fintech.service.UsuarioService;
import com.gestion.fintech.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación y Registro", description = "Operaciones para registro de usuarios y autenticación.")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "Registrar nuevo usuario", description = "Permite registrar un nuevo usuario proporcionando un nombre de usuario, contraseña y nombre completo.")
    @PostMapping("/register")
    public ResponseEntity<Usuario> register(@RequestBody RegisterDTO request) {
        Usuario usuario = usuarioService.registrarUsuario(request.getUsername(), request.getPassword(), request.getNombreCompleto());
        return ResponseEntity.ok(usuario);
    }

    @Operation(summary = "Autenticar usuario", description = "Permite autenticar un usuario registrado con su nombre de usuario y contraseña, devolviendo un token JWT.")
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO request) {
        Optional<Usuario> usuario = usuarioService.autenticarUsuario(request.getUsername(), request.getPassword());
        if (usuario.isPresent()) {
            String token = jwtUtil.generateToken(usuario.get().getUsername(), usuario.get().getNombreTitular());
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } else {
            throw new UsuarioException("Credenciales inválidas");
        }
    }
}
