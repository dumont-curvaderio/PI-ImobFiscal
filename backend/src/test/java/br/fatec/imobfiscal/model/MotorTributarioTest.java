package br.fatec.imobfiscal.model;

// ─── JUnit 5 ─────────────────────────────────────────────────────────────────
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

// ─── Mockito ──────────────────────────────────────────────────────────────────
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// ─── Projeto ──────────────────────────────────────────────────────────────────
import br.fatec.imobfiscal.model.dao.AliquotaVigenteDao;
import br.fatec.imobfiscal.view.motor.CalculoRequest;
import br.fatec.imobfiscal.view.motor.ResultadoCalculoDTO;

// ─── Java ─────────────────────────────────────────────────────────────────────
import java.math.BigDecimal;
import java.util.Optional;

// ─── Assertions e verificações ────────────────────────────────────────────────
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do MotorTributario.
 *
 * Estratégia: usamos Mockito para simular o DAO de alíquotas (sem banco real).
 * Assim testamos apenas a lógica de cálculo IBS/CBS isoladamente.
 *
 * @see MotorTributario
 */
@ExtendWith(MockitoExtension.class)
class MotorTributarioTest {

    // Mock do DAO: o Mockito cria uma versão falsa que devolve o que mandarmos.
    @Mock
    private AliquotaVigenteDao aliquotaVigenteDao;

    // @InjectMocks cria o MotorTributario e injeta o mock acima no construtor.
    @InjectMocks
    private MotorTributario motorTributario;

    // ──────────────────────────────────────────────────────────────────────────
    // TESTE 1 — Caso base: cálculo correto de IBS, CBS e valor líquido
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve calcular IBS, CBS e valor líquido corretamente (caso base)")
    void deveCalcularTributosCorretamente() {
        // ── Arrange ───────────────────────────────────────────────────────────
        // Alíquota PF/RESIDENCIAL: IBS=1,45% e CBS=0,76% (igual ao seed do banco).
        AliquotaVigente aliquota = criarAliquota("0.0145", "0.0076");

        // Ensinamos o mock: qualquer ano, regime PF, tipo RESIDENCIAL → devolve essa alíquota.
        when(aliquotaVigenteDao.buscarVigente(eq("PF"), eq("RESIDENCIAL"), anyInt()))
                .thenReturn(Optional.of(aliquota));

        // ── Act ───────────────────────────────────────────────────────────────
        // Valor base R$ 2.000,00. IBS = 2000 * 0,0145 = 29,0000 | CBS = 2000 * 0,0076 = 15,2000
        // Líquido = 2000 - 29 - 15,20 = 1955,80
        ResultadoCalculoDTO resultado = motorTributario.calcular(
                new CalculoRequest(new BigDecimal("2000.00"), "PF", "RESIDENCIAL"));

        // ── Assert ────────────────────────────────────────────────────────────
        // Comparamos com compareTo == 0 para ignorar diferenças de escala (29 vs 29.0000).
        assertAll(
                () -> assertEquals(0, resultado.valorIbs().compareTo(new BigDecimal("29.0000")),
                        "IBS deve ser 29,0000"),
                () -> assertEquals(0, resultado.valorCbs().compareTo(new BigDecimal("15.2000")),
                        "CBS deve ser 15,2000"),
                () -> assertEquals(0, resultado.valorLiquido().compareTo(new BigDecimal("1955.80")),
                        "Valor líquido deve ser 1955,80")
        );
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TESTE 2 — Alíquota não encontrada → exceção
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar RuntimeException quando a alíquota não existe no banco")
    void deveLancarExcecao_quandoAliquotaNaoEncontrada() {
        // ── Arrange ───────────────────────────────────────────────────────────
        // Mock devolve Optional vazio — simula "não há alíquota cadastrada".
        when(aliquotaVigenteDao.buscarVigente(eq("PF"), eq("RESIDENCIAL"), anyInt()))
                .thenReturn(Optional.empty());

        // ── Act + Assert ──────────────────────────────────────────────────────
        RuntimeException excecao = assertThrows(
                RuntimeException.class,
                () -> motorTributario.calcular(
                        new CalculoRequest(new BigDecimal("2000.00"), "PF", "RESIDENCIAL"))
        );

        // A mensagem deve começar com o texto definido no MotorTributario.
        org.junit.jupiter.api.Assertions.assertTrue(
                excecao.getMessage().startsWith("Alíquota não encontrada"),
                "A mensagem deve indicar que a alíquota não foi encontrada");
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TESTE 3 — Alíquota zero (isenção) → valor líquido = valor base
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve devolver valor líquido igual ao valor base quando há isenção (alíquota 0)")
    void deveTratarIsencao_quandoAliquotaZero() {
        // ── Arrange ───────────────────────────────────────────────────────────
        // Alíquota zerada simula uma hipótese de isenção (ex: residencial ≤ R$2.500).
        AliquotaVigente aliquota = criarAliquota("0.0000", "0.0000");

        when(aliquotaVigenteDao.buscarVigente(eq("PF"), eq("RESIDENCIAL"), anyInt()))
                .thenReturn(Optional.of(aliquota));

        // ── Act ───────────────────────────────────────────────────────────────
        ResultadoCalculoDTO resultado = motorTributario.calcular(
                new CalculoRequest(new BigDecimal("2000.00"), "PF", "RESIDENCIAL"));

        // ── Assert ────────────────────────────────────────────────────────────
        assertAll(
                () -> assertEquals(0, resultado.valorIbs().compareTo(BigDecimal.ZERO),
                        "IBS deve ser zero na isenção"),
                () -> assertEquals(0, resultado.valorCbs().compareTo(BigDecimal.ZERO),
                        "CBS deve ser zero na isenção"),
                // Sem tributos, o líquido é igual ao valor base.
                () -> assertEquals(0, resultado.valorLiquido().compareTo(new BigDecimal("2000.00")),
                        "Valor líquido deve ser igual ao valor base na isenção")
        );
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Método auxiliar — cria uma AliquotaVigente com as alíquotas informadas
    // ──────────────────────────────────────────────────────────────────────────

    private AliquotaVigente criarAliquota(String ibs, String cbs) {
        AliquotaVigente a = new AliquotaVigente();
        a.setRegime("PF");
        a.setTipoImovel("RESIDENCIAL");
        a.setAliquotaIbs(new BigDecimal(ibs));
        a.setAliquotaCbs(new BigDecimal(cbs));
        a.setAnoVigencia(2026);
        return a;
    }
}
