package br.fatec.imobfiscal.service;

import br.fatec.imobfiscal.model.Usuario;
import br.fatec.imobfiscal.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public Optional<Usuario> buscarPorEmail(String email) {
        return repository.findByEmailAndDeletedAtIsNull(email);
    }

    public boolean existePorEmail(String email) {
        return repository.existsByEmailAndDeletedAtIsNull(email);
    }

    public Usuario cadastrar(UUID imobiliariaId, String email, String senha, String nome) {
        Usuario usuario = new Usuario();
        usuario.setImobiliariaId(imobiliariaId);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setNome(nome);
        return repository.save(usuario);
    }
}
