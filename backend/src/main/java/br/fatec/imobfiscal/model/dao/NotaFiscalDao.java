package br.fatec.imobfiscal.model.dao;

import br.fatec.imobfiscal.enums.StatusNFe;
import br.fatec.imobfiscal.model.NotaFiscal;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

// DAO da NotaFiscal — leitura, criação e troca de status com SQL puro.
@Repository
@RequiredArgsConstructor
public class NotaFiscalDao {

    private final JdbcTemplate jdbcTemplate;

    private static final String COLUNAS = """
            id, imobiliaria_id, contrato_id, numero, serie, chave_acesso, status,
            valor_servico, valor_ibs, valor_cbs, recolhimento_obrigatorio,
            tentativas, erro_sefaz, created_at, updated_at, deleted_at
            """;

    public List<NotaFiscal> listar(UUID imobiliariaId) {
        String sql = "SELECT " + COLUNAS + """
                FROM notas_fiscais
                WHERE imobiliaria_id = ? AND deleted_at IS NULL
                ORDER BY created_at DESC
                """;
        return jdbcTemplate.query(sql, this::mapRow, imobiliariaId);
    }

    // Lista todas as notas de um contrato (já filtrado por imobiliária no controller).
    public List<NotaFiscal> listarPorContrato(UUID contratoId) {
        String sql = "SELECT " + COLUNAS + """
                FROM notas_fiscais
                WHERE contrato_id = ? AND deleted_at IS NULL
                ORDER BY created_at DESC
                """;
        return jdbcTemplate.query(sql, this::mapRow, contratoId);
    }

    public NotaFiscal buscar(UUID imobiliariaId, UUID id) {
        String sql = "SELECT " + COLUNAS + """
                FROM notas_fiscais
                WHERE id = ? AND imobiliaria_id = ? AND deleted_at IS NULL
                """;
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRow, id, imobiliariaId);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Nota fiscal não encontrada");
        }
    }

    public NotaFiscal inserir(NotaFiscal nf) {
        nf.setId(UUID.randomUUID());
        LocalDateTime agora = LocalDateTime.now();
        nf.setCreatedAt(agora);
        nf.setUpdatedAt(agora);
        if (nf.getStatus() == null) {
            nf.setStatus(StatusNFe.AGUARDANDO);
        }

        String sql = """
                INSERT INTO notas_fiscais
                    (id, imobiliaria_id, contrato_id, numero, serie, chave_acesso, status,
                     valor_servico, valor_ibs, valor_cbs, recolhimento_obrigatorio,
                     tentativas, erro_sefaz, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql,
                nf.getId(),
                nf.getImobiliariaId(),
                nf.getContratoId(),
                nf.getNumero(),
                nf.getSerie(),
                nf.getChaveAcesso(),
                nf.getStatus().name(),
                nf.getValorServico(),
                nf.getValorIbs(),
                nf.getValorCbs(),
                nf.getRecolhimentoObrigatorio(),
                nf.getTentativas(),
                nf.getErroSefaz(),
                nf.getCreatedAt(),
                nf.getUpdatedAt());
        return nf;
    }

    // Atualiza apenas o status (ex: AGUARDANDO → AUTORIZADA).
    public void atualizarStatus(UUID imobiliariaId, UUID id, StatusNFe novoStatus) {
        LocalDateTime agora = LocalDateTime.now();
        String sql = """
                UPDATE notas_fiscais
                SET status = ?, updated_at = ?
                WHERE id = ? AND imobiliaria_id = ? AND deleted_at IS NULL
                """;
        jdbcTemplate.update(sql, novoStatus.name(), agora, id, imobiliariaId);
    }

    private NotaFiscal mapRow(ResultSet rs, int rowNum) throws SQLException {
        NotaFiscal nf = new NotaFiscal();
        nf.setId(rs.getObject("id", UUID.class));
        nf.setImobiliariaId(rs.getObject("imobiliaria_id", UUID.class));
        nf.setContratoId(rs.getObject("contrato_id", UUID.class));
        nf.setNumero(rs.getString("numero"));
        nf.setSerie(rs.getString("serie"));
        nf.setChaveAcesso(rs.getString("chave_acesso"));
        String status = rs.getString("status");
        nf.setStatus(status != null ? StatusNFe.valueOf(status) : null);
        nf.setValorServico(rs.getObject("valor_servico", BigDecimal.class));
        nf.setValorIbs(rs.getObject("valor_ibs", BigDecimal.class));
        nf.setValorCbs(rs.getObject("valor_cbs", BigDecimal.class));
        nf.setRecolhimentoObrigatorio(rs.getObject("recolhimento_obrigatorio", Boolean.class));
        nf.setTentativas(rs.getObject("tentativas", Integer.class));
        nf.setErroSefaz(rs.getString("erro_sefaz"));
        nf.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        nf.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
        nf.setDeletedAt(rs.getObject("deleted_at", LocalDateTime.class));
        return nf;
    }
}
