package br.fatec.imobfiscal.model.dao;

import br.fatec.imobfiscal.model.AliquotaVigente;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

// DAO das alíquotas vigentes.
// REGRA CRÍTICA (RN do projeto): NUNCA hardcodar alíquotas. Sempre buscar aqui,
// filtrando por regime + tipo de imóvel + ano de vigência.
@Repository
@RequiredArgsConstructor
public class AliquotaVigenteDao {

    private final JdbcTemplate jdbcTemplate;

    // Busca a alíquota vigente para o trio (regime, tipoImovel, ano).
    // Devolve Optional vazio se não existir — quem chama decide o que fazer.
    public Optional<AliquotaVigente> buscarVigente(String regime, String tipoImovel, int ano) {
        String sql = """
                SELECT id, regime, tipo_imovel, aliquota_ibs, aliquota_cbs,
                       ano_vigencia, created_at
                FROM aliquotas_vigentes
                WHERE regime = ? AND tipo_imovel = ? AND ano_vigencia = ?
                """;
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, this::mapRow, regime, tipoImovel, ano));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private AliquotaVigente mapRow(ResultSet rs, int rowNum) throws SQLException {
        AliquotaVigente a = new AliquotaVigente();
        a.setId(rs.getObject("id", UUID.class));
        a.setRegime(rs.getString("regime"));
        a.setTipoImovel(rs.getString("tipo_imovel"));
        a.setAliquotaIbs(rs.getObject("aliquota_ibs", BigDecimal.class));
        a.setAliquotaCbs(rs.getObject("aliquota_cbs", BigDecimal.class));
        a.setAnoVigencia(rs.getObject("ano_vigencia", Integer.class));
        a.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        return a;
    }
}
