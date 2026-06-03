package br.fatec.imobfiscal.model.dao;

import br.fatec.imobfiscal.enums.RegimeTributario;
import br.fatec.imobfiscal.enums.TipoPessoa;
import br.fatec.imobfiscal.model.Locador;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class LocadorDao {

    private final JdbcTemplate jdbcTemplate;

    public List<Locador> listar(UUID imobiliariaId) {
        String sql = """
                SELECT id, imobiliaria_id, tipo_pessoa, cpf_cnpj, nome, email, telefone,
                       regime_tributario, created_at, updated_at, deleted_at
                FROM locadores
                WHERE imobiliaria_id = ? AND deleted_at IS NULL
                ORDER BY created_at DESC
                """;
        return jdbcTemplate.query(sql, this::mapRow, imobiliariaId);
    }

    public Locador buscar(UUID imobiliariaId, UUID id) {
        String sql = """
                SELECT id, imobiliaria_id, tipo_pessoa, cpf_cnpj, nome, email, telefone,
                       regime_tributario, created_at, updated_at, deleted_at
                FROM locadores
                WHERE id = ? AND imobiliaria_id = ? AND deleted_at IS NULL
                """;
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRow, id, imobiliariaId);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Locador não encontrado");
        }
    }

    public Locador inserir(Locador locador) {
        locador.setId(UUID.randomUUID());
        LocalDateTime agora = LocalDateTime.now();
        locador.setCreatedAt(agora);
        locador.setUpdatedAt(agora);

        String sql = """
                INSERT INTO locadores
                    (id, imobiliaria_id, tipo_pessoa, cpf_cnpj, nome, email, telefone,
                     regime_tributario, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql,
                locador.getId(),
                locador.getImobiliariaId(),
                locador.getTipoPessoa() != null ? locador.getTipoPessoa().name() : null,
                locador.getCpfCnpj(),
                locador.getNome(),
                locador.getEmail(),
                locador.getTelefone(),
                locador.getRegimeTributario() != null ? locador.getRegimeTributario().name() : null,
                locador.getCreatedAt(),
                locador.getUpdatedAt());
        return locador;
    }

    public Locador atualizar(Locador locador) {
        locador.setUpdatedAt(LocalDateTime.now());

        String sql = """
                UPDATE locadores
                SET tipo_pessoa = ?, cpf_cnpj = ?, nome = ?, email = ?, telefone = ?,
                    regime_tributario = ?, updated_at = ?
                WHERE id = ? AND imobiliaria_id = ? AND deleted_at IS NULL
                """;
        jdbcTemplate.update(sql,
                locador.getTipoPessoa() != null ? locador.getTipoPessoa().name() : null,
                locador.getCpfCnpj(),
                locador.getNome(),
                locador.getEmail(),
                locador.getTelefone(),
                locador.getRegimeTributario() != null ? locador.getRegimeTributario().name() : null,
                locador.getUpdatedAt(),
                locador.getId(),
                locador.getImobiliariaId());
        return locador;
    }

    public void softDelete(UUID imobiliariaId, UUID id) {
        LocalDateTime agora = LocalDateTime.now();
        String sql = """
                UPDATE locadores
                SET deleted_at = ?, updated_at = ?
                WHERE id = ? AND imobiliaria_id = ? AND deleted_at IS NULL
                """;
        jdbcTemplate.update(sql, agora, agora, id, imobiliariaId);
    }

    private Locador mapRow(ResultSet rs, int rowNum) throws SQLException {
        Locador l = new Locador();
        l.setId(rs.getObject("id", UUID.class));
        l.setImobiliariaId(rs.getObject("imobiliaria_id", UUID.class));
        String tipoPessoa = rs.getString("tipo_pessoa");
        l.setTipoPessoa(tipoPessoa != null ? TipoPessoa.valueOf(tipoPessoa) : null);
        l.setCpfCnpj(rs.getString("cpf_cnpj"));
        l.setNome(rs.getString("nome"));
        l.setEmail(rs.getString("email"));
        l.setTelefone(rs.getString("telefone"));
        String regime = rs.getString("regime_tributario");
        l.setRegimeTributario(regime != null ? RegimeTributario.valueOf(regime) : null);
        l.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        l.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
        l.setDeletedAt(rs.getObject("deleted_at", LocalDateTime.class));
        return l;
    }
}
