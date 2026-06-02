package br.fatec.imobfiscal.model.dao;

import br.fatec.imobfiscal.enums.StatusContrato;
import br.fatec.imobfiscal.enums.TipoLocacao;
import br.fatec.imobfiscal.enums.TipoPessoa;
import br.fatec.imobfiscal.model.ContratoLocacao;
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

// DAO do ContratoLocacao — CRUD com SQL puro.
@Repository
@RequiredArgsConstructor
public class ContratoDao {

    private final JdbcTemplate jdbcTemplate;

    private static final String COLUNAS = """
            id, imobiliaria_id, imovel_id, tipo_locacao, status, locatario_tipo,
            locatario_cpf_cnpj, locatario_nome, valor_aluguel, dia_vencimento,
            data_inicio, data_fim, prazo_meses, created_at, updated_at, deleted_at
            """;

    public List<ContratoLocacao> listar(UUID imobiliariaId) {
        String sql = "SELECT " + COLUNAS + """
                FROM contratos_locacao
                WHERE imobiliaria_id = ? AND deleted_at IS NULL
                ORDER BY created_at DESC
                """;
        return jdbcTemplate.query(sql, this::mapRow, imobiliariaId);
    }

    public ContratoLocacao buscar(UUID imobiliariaId, UUID id) {
        String sql = "SELECT " + COLUNAS + """
                FROM contratos_locacao
                WHERE id = ? AND imobiliaria_id = ? AND deleted_at IS NULL
                """;
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRow, id, imobiliariaId);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Contrato não encontrado");
        }
    }

    public ContratoLocacao inserir(ContratoLocacao contrato) {
        contrato.setId(UUID.randomUUID());
        LocalDateTime agora = LocalDateTime.now();
        contrato.setCreatedAt(agora);
        contrato.setUpdatedAt(agora);
        if (contrato.getStatus() == null) {
            contrato.setStatus(StatusContrato.RASCUNHO);
        }

        String sql = """
                INSERT INTO contratos_locacao
                    (id, imobiliaria_id, imovel_id, tipo_locacao, status, locatario_tipo,
                     locatario_cpf_cnpj, locatario_nome, valor_aluguel, dia_vencimento,
                     data_inicio, data_fim, prazo_meses, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql,
                contrato.getId(),
                contrato.getImobiliariaId(),
                contrato.getImovelId(),
                contrato.getTipoLocacao() != null ? contrato.getTipoLocacao().name() : null,
                contrato.getStatus().name(),
                contrato.getLocatarioTipo() != null ? contrato.getLocatarioTipo().name() : null,
                contrato.getLocatarioCpfCnpj(),
                contrato.getLocatarioNome(),
                contrato.getValorAluguel(),
                contrato.getDiaVencimento(),
                contrato.getDataInicio(),
                contrato.getDataFim(),
                contrato.getPrazoMeses(),
                contrato.getCreatedAt(),
                contrato.getUpdatedAt());
        return contrato;
    }

    // Atualiza apenas o status (ex: RASCUNHO → ATIVO).
    public void atualizarStatus(UUID imobiliariaId, UUID id, StatusContrato novoStatus) {
        LocalDateTime agora = LocalDateTime.now();
        String sql = """
                UPDATE contratos_locacao
                SET status = ?, updated_at = ?
                WHERE id = ? AND imobiliaria_id = ? AND deleted_at IS NULL
                """;
        jdbcTemplate.update(sql, novoStatus.name(), agora, id, imobiliariaId);
    }

    // Soft delete via UPDATE — nunca DELETE físico.
    public void softDelete(UUID imobiliariaId, UUID id) {
        LocalDateTime agora = LocalDateTime.now();
        String sql = """
                UPDATE contratos_locacao
                SET deleted_at = ?, updated_at = ?
                WHERE id = ? AND imobiliaria_id = ? AND deleted_at IS NULL
                """;
        jdbcTemplate.update(sql, agora, agora, id, imobiliariaId);
    }

    private ContratoLocacao mapRow(ResultSet rs, int rowNum) throws SQLException {
        ContratoLocacao c = new ContratoLocacao();
        c.setId(rs.getObject("id", UUID.class));
        c.setImobiliariaId(rs.getObject("imobiliaria_id", UUID.class));
        c.setImovelId(rs.getObject("imovel_id", UUID.class));
        String tipoLoc = rs.getString("tipo_locacao");
        c.setTipoLocacao(tipoLoc != null ? TipoLocacao.valueOf(tipoLoc) : null);
        String status = rs.getString("status");
        c.setStatus(status != null ? StatusContrato.valueOf(status) : null);
        String locTipo = rs.getString("locatario_tipo");
        c.setLocatarioTipo(locTipo != null ? TipoPessoa.valueOf(locTipo) : null);
        c.setLocatarioCpfCnpj(rs.getString("locatario_cpf_cnpj"));
        c.setLocatarioNome(rs.getString("locatario_nome"));
        c.setValorAluguel(rs.getObject("valor_aluguel", BigDecimal.class));
        c.setDiaVencimento(rs.getObject("dia_vencimento", Integer.class));
        c.setDataInicio(rs.getObject("data_inicio", LocalDate.class));
        c.setDataFim(rs.getObject("data_fim", LocalDate.class));
        c.setPrazoMeses(rs.getObject("prazo_meses", Integer.class));
        c.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        c.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
        c.setDeletedAt(rs.getObject("deleted_at", LocalDateTime.class));
        return c;
    }
}
