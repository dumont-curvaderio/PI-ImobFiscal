package br.fatec.imobfiscal.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.fatec.imobfiscal.repository.AliquotaVigenteRepository;
import br.fatec.imobfiscal.service.MotorTributarioService;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MotorTributarioTest {

    @Mock
    private AliquotaVigenteRepository repository;

    @InjectMocks
    private MotorTributarioService motorTributarioService;

    @Test
    @DisplayName("Deve calcular IBS, CBS e valor líquido corretamente (caso base)")
    void deveCalcularTributosCorretamente() {
        AliquotaVigente aliquota = criarAliquota("0.0145", "0.0076");

        when(repository.findByRegimeAndTipoImovelAndAnoVigencia(eq("PF"), eq("RESIDENCIAL"), anyInt()))
                .thenReturn(Optional.of(aliquota));

        ResultadoCalculoDTO resultado = motorTributarioService.calcular(
                new CalculoRequest(new BigDecimal("2000.00"), "PF", "RESIDENCIAL"));

        assertAll(
                () -> assertEquals(0, resultado.valorIbs().compareTo(new BigDecimal("29.0000")),
                        "IBS deve ser 29,0000"),
                () -> assertEquals(0, resultado.valorCbs().compareTo(new BigDecimal("15.2000")),
                        "CBS deve ser 15,2000"),
                () -> assertEquals(0, resultado.valorLiquido().compareTo(new BigDecimal("1955.80")),
                        "Valor líquido deve ser 1955,80")
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando a alíquota não existe no banco")
    void deveLancarExcecao_quandoAliquotaNaoEncontrada() {
        when(repository.findByRegimeAndTipoImovelAndAnoVigencia(eq("PF"), eq("RESIDENCIAL"), anyInt()))
                .thenReturn(Optional.empty());

        RuntimeException excecao = assertThrows(
                RuntimeException.class,
                () -> motorTributarioService.calcular(
                        new CalculoRequest(new BigDecimal("2000.00"), "PF", "RESIDENCIAL"))
        );

        assertTrue(excecao.getMessage().startsWith("Alíquota não encontrada"),
                "A mensagem deve indicar que a alíquota não foi encontrada");
    }

    @Test
    @DisplayName("Deve devolver valor líquido igual ao valor base quando alíquota é zero")
    void deveTratarIsencao_quandoAliquotaZero() {
        AliquotaVigente aliquota = criarAliquota("0.0000", "0.0000");

        when(repository.findByRegimeAndTipoImovelAndAnoVigencia(eq("PF"), eq("RESIDENCIAL"), anyInt()))
                .thenReturn(Optional.of(aliquota));

        ResultadoCalculoDTO resultado = motorTributarioService.calcular(
                new CalculoRequest(new BigDecimal("2000.00"), "PF", "RESIDENCIAL"));

        assertAll(
                () -> assertEquals(0, resultado.valorIbs().compareTo(BigDecimal.ZERO), "IBS deve ser zero"),
                () -> assertEquals(0, resultado.valorCbs().compareTo(BigDecimal.ZERO), "CBS deve ser zero"),
                () -> assertEquals(0, resultado.valorLiquido().compareTo(new BigDecimal("2000.00")),
                        "Valor líquido deve ser igual ao valor base")
        );
    }

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
