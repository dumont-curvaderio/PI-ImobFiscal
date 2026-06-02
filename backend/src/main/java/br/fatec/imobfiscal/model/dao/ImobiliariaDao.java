package br.fatec.imobfiscal.model.dao;

import br.fatec.imobfiscal.enums.PlanoAssinatura;
import br.fatec.imobfiscal.model.Imobiliaria;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

// DAO (Data Access Object) da Imobiliaria.
// Substitui o antigo ImobiliariaRepository (JPA). Agora escrevemos o SQL na mão
// e o JdbcTemplate executa contra o PostgreSQL.
@Repository
@RequiredArgsConstructor
public class ImobiliariaDao {

    // JdbcTemplate é a ferramenta do Spring para rodar SQL puro com segurança.
    private final JdbcTemplate jdbcTemplate;

    // Busca uma imobiliária pelo id (apenas se não estiver soft-deletada).
    public Optional<Imobiliaria> buscarPorId(UUID id) {
        String sql = """
                SELECT id, cnpj, razao, nome_fantasia, email, telefone, plano,
                       created_at, updated_at, deleted_at
                FROM imobiliarias
                WHERE id = ? AND deleted_at IS NULL
                """;
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, this::mapRow, id));
        } catch (EmptyResultDataAccessException e) {
            // Nenhuma linha encontrada — devolvemos Optional vazio
            return Optional.empty();
        }
    }

    // Converte uma linha do ResultSet em um objeto Imobiliaria (model).
    private Imobiliaria mapRow(ResultSet rs, int rowNum) throws SQLException {
        Imobiliaria i = new Imobiliaria();
        i.setId(rs.getObject("id", UUID.class));
        i.setCnpj(rs.getString("cnpj"));
        i.setRazao(rs.getString("razao"));
        i.setNomeFantasia(rs.getString("nome_fantasia"));
        i.setEmail(rs.getString("email"));
        i.setTelefone(rs.getString("telefone"));
        i.setPlano(PlanoAssinatura.valueOf(rs.getString("plano")));
        i.setCreatedAt(rs.getObject("created_at", java.time.LocalDateTime.class));
        i.setUpdatedAt(rs.getObject("updated_at", java.time.LocalDateTime.class));
        i.setDeletedAt(rs.getObject("deleted_at", java.time.LocalDateTime.class));
        return i;
    }
}
