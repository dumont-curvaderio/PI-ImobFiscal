package br.fatec.imobfiscal.model.dao;

// ─── JUnit 5 ─────────────────────────────────────────────────────────────────
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

// ─── Mockito ──────────────────────────────────────────────────────────────────
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.UUID;

// ─── Assertions e verificações ────────────────────────────────────────────────
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Testes unitários do ImovelDao.
 *
 * Estratégia: usamos Mockito para simular o JdbcTemplate (a ferramenta que
 * conversa com o banco). Assim testamos a lógica do DAO sem precisar de um
 * banco de dados real.
 *
 * O teste mais importante garante a regra fiscal: o "delete" do sistema é um
 * SOFT delete (UPDATE marcando deleted_at), NUNCA um DELETE físico — porque
 * dados fiscais têm guarda mínima de 5 anos.
 *
 * @see ImovelDao
 */
@ExtendWith(MockitoExtension.class)
class ImovelDaoTest {

    // Mock: um JdbcTemplate "falso" que não toca em banco nenhum.
    @Mock
    private JdbcTemplate jdbcTemplate;

    // @InjectMocks cria o ImovelDao e injeta o mock acima no construtor.
    @InjectMocks
    private ImovelDao imovelDao;

    @Test
    @DisplayName("softDelete deve executar um UPDATE (nunca um DELETE físico) — regra fiscal")
    void softDeleteDeveUsarUpdateNuncaDelete() {
        // ── Arrange ───────────────────────────────────────────────────────────
        UUID imobiliariaId = UUID.randomUUID();
        UUID imovelId      = UUID.randomUUID();

        // ── Act ───────────────────────────────────────────────────────────────
        imovelDao.softDelete(imobiliariaId, imovelId);

        // ── Assert ────────────────────────────────────────────────────────────
        // Captura o SQL que o DAO mandou o JdbcTemplate executar.
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate, times(1)).update(sqlCaptor.capture(), any(), any(), any(), any());

        String sqlExecutado = sqlCaptor.getValue().trim().toUpperCase();

        // CRÍTICO: o comando precisa ser um UPDATE...
        assertTrue(sqlExecutado.startsWith("UPDATE"),
                "O soft delete deve usar UPDATE, e o SQL começou com: " + sqlExecutado);

        // ...marcando a coluna deleted_at...
        assertTrue(sqlExecutado.contains("DELETED_AT"),
                "O UPDATE do soft delete deve preencher a coluna deleted_at");

        // ...e NUNCA pode ser um DELETE físico.
        assertTrue(!sqlExecutado.contains("DELETE FROM"),
                "Hard delete é proibido — dados fiscais têm guarda mínima de 5 anos");
    }

    @Test
    @DisplayName("buscar deve lançar 'Imóvel não encontrado' quando não houver linha")
    void buscarDeveLancarExcecaoQuandoNaoEncontra() {
        UUID imobiliariaId = UUID.randomUUID();
        UUID idInexistente = UUID.randomUUID();

        // O JdbcTemplate lança EmptyResultDataAccessException quando não acha nada;
        // o DAO deve converter isso na mensagem de negócio esperada.
        org.mockito.Mockito.when(jdbcTemplate.queryForObject(
                        anyString(), any(org.springframework.jdbc.core.RowMapper.class), any(), any()))
                .thenThrow(new org.springframework.dao.EmptyResultDataAccessException(1));

        RuntimeException ex = org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> imovelDao.buscar(imobiliariaId, idInexistente));

        org.junit.jupiter.api.Assertions.assertEquals("Imóvel não encontrado", ex.getMessage());
    }
}
