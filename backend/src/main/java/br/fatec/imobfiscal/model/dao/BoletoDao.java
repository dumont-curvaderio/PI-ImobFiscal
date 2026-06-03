package br.fatec.imobfiscal.model.dao;

import br.fatec.imobfiscal.model.Boleto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BoletoDao {

    private final JdbcTemplate jdbcTemplate;

    private static final String COLUNAS = """
            id, imobiliaria_id, contrato_id, valor_aluguel, aliquota_ibs, aliquota_cbs,
            valor_ibs, valor_cbs, valor_liquido, data_vencimento, status,
            regime_tributario, tipo_imovel, created_at, updated_at, deleted_at
            """;

    public List<Boleto> listar(UUID imobiliariaId) {
        String sql = "SELECT " + COLUNAS + """
                FROM boletos
                WHERE imobiliaria_id = ? AND deleted_at IS NULL
                ORDER BY created_at DESC
                """;
        return jdbcTemplate.query(sql, this::mapRow, imobiliariaId);
    }

    public Boleto buscar(UUID imobiliariaId, UUID id) {
        String sql = "SELECT " + COLUNAS + """
                FROM boletos
                WHERE id = ? AND imobiliaria_id = ? AND deleted_at IS NULL
                """;
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRow, id, imobiliariaId);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Boleto não encontrado: " + id);
        }
    }

    public Boleto inserir(Boleto boleto) {
        boleto.setId(UUID.randomUUID());
        LocalDateTime agora = LocalDateTime.now();
        boleto.setCreatedAt(agora);
        boleto.setUpdatedAt(agora);
        if (boleto.getStatus() == null) {
            boleto.setStatus("GERADO");
        }

        String sql = """
                INSERT INTO boletos
                    (id, imobiliaria_id, contrato_id, valor_aluguel, aliquota_ibs, aliquota_cbs,
                     valor_ibs, valor_cbs, valor_liquido, data_vencimento, status,
                     regime_tributario, tipo_imovel, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql,
                boleto.getId(),
                boleto.getImobiliariaId(),
                boleto.getContratoId(),
                boleto.getValorAluguel(),
                boleto.getAliquotaIbs(),
                boleto.getAliquotaCbs(),
                boleto.getValorIbs(),
                boleto.getValorCbs(),
                boleto.getValorLiquido(),
                boleto.getDataVencimento(),
                boleto.getStatus(),
                boleto.getRegimeTributario(),
                boleto.getTipoImovel(),
                boleto.getCreatedAt(),
                boleto.getUpdatedAt());
        return boleto;
    }

    private Boleto mapRow(ResultSet rs, int rowNum) throws SQLException {
        Boleto b = new Boleto();
        b.setId(rs.getObject("id", UUID.class));
        b.setImobiliariaId(rs.getObject("imobiliaria_id", UUID.class));
        b.setContratoId(rs.getObject("contrato_id", UUID.class));
        b.setValorAluguel(rs.getObject("valor_aluguel", BigDecimal.class));
        b.setAliquotaIbs(rs.getObject("aliquota_ibs", BigDecimal.class));
        b.setAliquotaCbs(rs.getObject("aliquota_cbs", BigDecimal.class));
        b.setValorIbs(rs.getObject("valor_ibs", BigDecimal.class));
        b.setValorCbs(rs.getObject("valor_cbs", BigDecimal.class));
        b.setValorLiquido(rs.getObject("valor_liquido", BigDecimal.class));
        b.setDataVencimento(rs.getObject("data_vencimento", LocalDate.class));
        b.setStatus(rs.getString("status"));
        b.setRegimeTributario(rs.getString("regime_tributario"));
        b.setTipoImovel(rs.getString("tipo_imovel"));
        b.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        b.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
        b.setDeletedAt(rs.getObject("deleted_at", LocalDateTime.class));
        return b;
    }
}
