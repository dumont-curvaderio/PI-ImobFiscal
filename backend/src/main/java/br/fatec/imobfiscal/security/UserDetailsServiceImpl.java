package br.fatec.imobfiscal.security;

import br.fatec.imobfiscal.entity.Usuario;
import br.fatec.imobfiscal.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Spring Security chama este serviço para buscar o usuário no banco durante a autenticação
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

        // Converte nossa entidade Usuario para o formato que o Spring Security entende
        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())          // já deve estar em BCrypt no banco
                .roles(usuario.getPerfil().name())     // ex: ADMIN, OPERADOR
                .build();
    }
}
