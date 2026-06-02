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

// Controller de autenticação (login e cadastro).
//
// IMPORTANTE: a API NÃO usa mais JWT (decisão do projeto — comunicação só com o
// frontend deste sistema). O login apenas confere e-mail + senha no banco e
// devolve os dados do usuário. As demais rotas da API são abertas.
//
// MVC clássico: o Controller coordena e fala direto com o Model (os DAOs).
// A senha continua guardada como hash BCrypt no banco — nunca em texto puro.
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioDao usuarioDao;
    private final ImobiliariaDao imobiliariaDao;

    // BCrypt para comparar/gerar o hash da senha. Não depende do Spring Security
    // completo — vem da biblioteca leve spring-security-crypto.
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    // POST /api/auth/login → confere as credenciais e devolve os dados do usuário.
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = usuarioDao.buscarPorEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos"));

        // Compara a senha digitada com o hash BCrypt guardado no banco.
        if (!encoder.matches(request.senha(), usuario.getSenha())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos");
        }

        // "token" não é mais um JWT — é só um marcador de sessão para o frontend
        // saber que o login deu certo. A API não valida esse valor em nenhuma rota.
        String marcadorSessao = UUID.randomUUID().toString();

        return ResponseEntity.ok(new LoginResponse(
                marcadorSessao,
                usuario.getEmail(),
                usuario.getNome(),
                usuario.getPerfil().name()));
    }

    // POST /api/auth/cadastro → cria um novo usuário.
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
        // Sempre armazena a senha como hash BCrypt — nunca em texto puro.
        usuario.setSenha(encoder.encode(request.senha()));
        usuario.setNome(request.nome());

        usuarioDao.inserir(usuario);

        // 201 Created — sem corpo na resposta.
        return ResponseEntity.status(201).build();
    }
}
