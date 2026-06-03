package br.fatec.imobfiscal.controller;

import br.fatec.imobfiscal.model.Usuario;
import br.fatec.imobfiscal.model.dao.ImobiliariaDao;
import br.fatec.imobfiscal.model.dao.UsuarioDao;
import br.fatec.imobfiscal.view.auth.CadastroRequest;
import br.fatec.imobfiscal.view.auth.LoginRequest;
import br.fatec.imobfiscal.view.auth.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioDao usuarioDao;
    private final ImobiliariaDao imobiliariaDao;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = usuarioDao.buscarPorEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos"));

        if (!encoder.matches(request.senha(), usuario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos");
        }

        String marcadorSessao = UUID.randomUUID().toString();

        return ResponseEntity.ok(new LoginResponse(
                marcadorSessao,
                usuario.getEmail(),
                usuario.getNome(),
                usuario.getPerfil().name()));
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Void> cadastrar(@Valid @RequestBody CadastroRequest request) {
        if (usuarioDao.existePorEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
        }

        UUID imobiliariaId = UUID.fromString(request.imobiliariaId());
        imobiliariaDao.buscarPorId(imobiliariaId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Imobiliária não encontrada"));

        Usuario usuario = new Usuario();
        usuario.setImobiliariaId(imobiliariaId);
        usuario.setEmail(request.email());
        usuario.setSenha(encoder.encode(request.senha()));
        usuario.setNome(request.nome());

        usuarioDao.inserir(usuario);

        return ResponseEntity.status(201).build();
    }
}
