package br.fatec.imobfiscal.repository;

import br.fatec.imobfiscal.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    // Usado pelo Spring Security para autenticar o usuário pelo e-mail
    Optional<Usuario> findByEmail(String email);

    // Verifica se o e-mail já está cadastrado
    boolean existsByEmail(String email);
}
