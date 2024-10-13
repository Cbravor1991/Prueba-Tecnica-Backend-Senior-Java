package com.gestion.fintech.controller;

import com.gestion.fintech.dto.RegisterDTO;
import com.gestion.fintech.dto.LoginDTO;
import com.gestion.fintech.model.Usuario;
import com.gestion.fintech.service.UsuarioService;
import com.gestion.fintech.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO request) {

        Usuario usuario = usuarioService.registrarUsuario(request.getUsername(), request.getPassword(), request.getNombreCompleto());
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO request) {
        Optional<Usuario> usuario = usuarioService.autenticarUsuario(request.getUsername(), request.getPassword());
        if (usuario.isPresent()) {

            String token = jwtUtil.generateToken(usuario.get().getUsername(), usuario.get().getNombreTitular());
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }
}
