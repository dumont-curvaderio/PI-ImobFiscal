package br.fatec.imobfiscal.model.dao;

import br.fatec.imobfiscal.enums.PerfilUsuario;
import br.fatec.imobfiscal.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

// DAO do Usuario — mantém o login/JWT funcionando.
// Substitui o antigo UsuarioRepository (JPA).
@Repository
@RequiredArgsConstructor
public class UsuarioDao {

    private final JdbcTemplate jdbcTemplate;

    // Usado pelo Spring Security (UserDetailsServiceImpl) e pelo login.
    public Optional<Usuario> buscarPorEmail(String email) {
        String sql = """
                SELECT id, imobiliaria_id, email, senha, nome, perfil,
                       created_at, updated_at, deleted_at
                FROM usuarios
                WHERE email = ? AND deleted_at IS NULL
                """;
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, this::mapRow, email));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // Verifica se o e-mail já está cadastrado (usado no cadastro).
    public boolean existePorEmail(String email) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ? AND deleted_at IS NULL";
        Integer total = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return total != null && total > 0;
    }

    // Insere um novo usuário. O id e as datas são gerados aqui, em Java.
    public void inserir(Usuario usuario) {
        usuario.setId(UUID.randomUUID());
        LocalDateTime agora = LocalDateTime.now();
        usuario.setCreatedAt(agora);
        usuario.setUpdatedAt(agora);

        String sql = """
                INSERT INTO usuarios
                    (id, imobiliaria_id, email, senha, nome, perfil, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql,
                usuario.getId(),
                usuario.getImobiliariaId(),
                usuario.getEmail(),
                usuario.getSenha(),
                usuario.getNome(),
                usuario.getPerfil().name(),   // enum gravado como string
                usuario.getCreatedAt(),
                usuario.getUpdatedAt());
    }

    private Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getObject("id", UUID.class));
        u.setImobiliariaId(rs.getObject("imobiliaria_id", UUID.class));
        u.setEmail(rs.getString("email"));
        u.setSenha(rs.getString("senha"));
        u.setNome(rs.getString("nome"));
        u.setPerfil(PerfilUsuario.valueOf(rs.getString("perfil")));
        u.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        u.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
        u.setDeletedAt(rs.getObject("deleted_at", LocalDateTime.class));
        return u;
    }
}
