package br.fatec.imobfiscal.model.dao;

import br.fatec.imobfiscal.enums.TipoImovel;
import br.fatec.imobfiscal.model.Imovel;
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
public class ImovelDao {

    private final JdbcTemplate jdbcTemplate;

    private static final String COLUNAS = """
            id, imobiliaria_id, locador_id, codigo, tipo, cep, logradouro, numero,
            complemento, bairro, cidade, uf, area_total, quartos, vagas,
            valor_compra, data_compra, valor_venal, created_at, updated_at, deleted_at
            """;

    public List<Imovel> listar(UUID imobiliariaId) {
        String sql = "SELECT " + COLUNAS + """
                FROM imoveis
                WHERE imobiliaria_id = ? AND deleted_at IS NULL
                ORDER BY created_at DESC
                """;
        return jdbcTemplate.query(sql, this::mapRow, imobiliariaId);
    }

    public Imovel buscar(UUID imobiliariaId, UUID id) {
        String sql = "SELECT " + COLUNAS + """
                FROM imoveis
                WHERE id = ? AND imobiliaria_id = ? AND deleted_at IS NULL
                """;
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRow, id, imobiliariaId);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Imóvel não encontrado");
        }
    }

    public Imovel inserir(Imovel imovel) {
        imovel.setId(UUID.randomUUID());
        LocalDateTime agora = LocalDateTime.now();
        imovel.setCreatedAt(agora);
        imovel.setUpdatedAt(agora);

        String sql = """
                INSERT INTO imoveis
                    (id, imobiliaria_id, locador_id, codigo, tipo, cep, logradouro, numero,
                     complemento, bairro, cidade, uf, area_total, quartos, vagas,
                     valor_compra, data_compra, valor_venal, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql,
                imovel.getId(),
                imovel.getImobiliariaId(),
                imovel.getLocadorId(),
                imovel.getCodigo(),
                imovel.getTipo() != null ? imovel.getTipo().name() : null,
                imovel.getCep(),
                imovel.getLogradouro(),
                imovel.getNumero(),
                imovel.getComplemento(),
                imovel.getBairro(),
                imovel.getCidade(),
                imovel.getUf(),
                imovel.getAreaTotal(),
                imovel.getQuartos(),
                imovel.getVagas(),
                imovel.getValorCompra(),
                imovel.getDataCompra(),
                imovel.getValorVenal(),
                imovel.getCreatedAt(),
                imovel.getUpdatedAt());
        return imovel;
    }

    public Imovel atualizar(Imovel imovel) {
        imovel.setUpdatedAt(LocalDateTime.now());

        String sql = """
                UPDATE imoveis
                SET locador_id = ?, codigo = ?, tipo = ?, cep = ?, logradouro = ?,
                    numero = ?, complemento = ?, bairro = ?, cidade = ?, uf = ?,
                    area_total = ?, quartos = ?, vagas = ?, valor_compra = ?,
                    data_compra = ?, valor_venal = ?, updated_at = ?
                WHERE id = ? AND imobiliaria_id = ? AND deleted_at IS NULL
                """;
        jdbcTemplate.update(sql,
                imovel.getLocadorId(),
                imovel.getCodigo(),
                imovel.getTipo() != null ? imovel.getTipo().name() : null,
                imovel.getCep(),
                imovel.getLogradouro(),
                imovel.getNumero(),
                imovel.getComplemento(),
                imovel.getBairro(),
                imovel.getCidade(),
                imovel.getUf(),
                imovel.getAreaTotal(),
                imovel.getQuartos(),
                imovel.getVagas(),
                imovel.getValorCompra(),
                imovel.getDataCompra(),
                imovel.getValorVenal(),
                imovel.getUpdatedAt(),
                imovel.getId(),
                imovel.getImobiliariaId());
        return imovel;
    }

    public void softDelete(UUID imobiliariaId, UUID id) {
        LocalDateTime agora = LocalDateTime.now();
        String sql = """
                UPDATE imoveis
                SET deleted_at = ?, updated_at = ?
                WHERE id = ? AND imobiliaria_id = ? AND deleted_at IS NULL
                """;
        jdbcTemplate.update(sql, agora, agora, id, imobiliariaId);
    }

    private Imovel mapRow(ResultSet rs, int rowNum) throws SQLException {
        Imovel i = new Imovel();
        i.setId(rs.getObject("id", UUID.class));
        i.setImobiliariaId(rs.getObject("imobiliaria_id", UUID.class));
        i.setLocadorId(rs.getObject("locador_id", UUID.class));
        i.setCodigo(rs.getString("codigo"));
        String tipo = rs.getString("tipo");
        i.setTipo(tipo != null ? TipoImovel.valueOf(tipo) : null);
        i.setCep(rs.getString("cep"));
        i.setLogradouro(rs.getString("logradouro"));
        i.setNumero(rs.getString("numero"));
        i.setComplemento(rs.getString("complemento"));
        i.setBairro(rs.getString("bairro"));
        i.setCidade(rs.getString("cidade"));
        i.setUf(rs.getString("uf"));
        i.setAreaTotal(rs.getObject("area_total", BigDecimal.class));
        i.setQuartos(rs.getObject("quartos", Integer.class));
        i.setVagas(rs.getObject("vagas", Integer.class));
        i.setValorCompra(rs.getObject("valor_compra", BigDecimal.class));
        i.setDataCompra(rs.getObject("data_compra", LocalDate.class));
        i.setValorVenal(rs.getObject("valor_venal", BigDecimal.class));
        i.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        i.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
        i.setDeletedAt(rs.getObject("deleted_at", LocalDateTime.class));
        return i;
    }
}
