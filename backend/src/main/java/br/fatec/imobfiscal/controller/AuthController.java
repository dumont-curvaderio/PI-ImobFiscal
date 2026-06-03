package br.fatec.imobfiscal.controller;

import br.fatec.imobfiscal.model.Usuario;
import br.fatec.imobfiscal.service.ImobiliariaService;
import br.fatec.imobfiscal.service.UsuarioService;
import br.fatec.imobfiscal.view.auth.CadastroRequest;
import br.fatec.imobfiscal.view.auth.LoginRequest;
import br.fatec.imobfiscal.view.auth.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;
    private final ImobiliariaService imobiliariaService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = usuarioService.buscarPorEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos"));

        if (!passwordEncoder.matches(request.senha(), usuario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos");
        }

        return ResponseEntity.ok(new LoginResponse(
                UUID.randomUUID().toString(),
                usuario.getEmail(),
                usuario.getNome(),
                usuario.getPerfil().name()));
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Void> cadastrar(@Valid @RequestBody CadastroRequest request) {
        if (usuarioService.existePorEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }

        UUID imobiliariaId = UUID.fromString(request.imobiliariaId());
        imobiliariaService.buscarPorId(imobiliariaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Imobiliária não encontrada"));

        usuarioService.cadastrar(imobiliariaId, request.email(), request.senha(), request.nome());
        return ResponseEntity.status(201).build();
    }
}
