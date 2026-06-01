package br.fatec.imobfiscal.controller;

import br.fatec.imobfiscal.dto.auth.CadastroRequest;
import br.fatec.imobfiscal.dto.auth.LoginRequest;
import br.fatec.imobfiscal.dto.auth.LoginResponse;
import br.fatec.imobfiscal.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/login → retorna o token JWT
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // POST /api/auth/cadastro → cria um novo usuário
    @PostMapping("/cadastro")
    public ResponseEntity<Void> cadastrar(@Valid @RequestBody CadastroRequest request) {
        authService.cadastrar(request);
        // 201 Created — sem corpo na resposta
        return ResponseEntity.status(201).build();
    }
}
