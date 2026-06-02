package br.fatec.imobfiscal.model;

import br.fatec.imobfiscal.model.dao.AliquotaVigenteDao;
import br.fatec.imobfiscal.view.motor.CalculoRequest;
import br.fatec.imobfiscal.view.motor.ResultadoCalculoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

// Motor Tributário — calcula IBS/CBS conforme a Reforma Tributária (LC 214/2025).
// No padrão MVC sem service, a lógica de negócio fiscal vive aqui, no model,
// como um @Component que o controller injeta. Busca as alíquotas do banco
// (NUNCA hardcoda valores).
@Component
@RequiredArgsConstructor
public class MotorTributario {

    private final AliquotaVigenteDao aliquotaVigenteDao;

    public ResultadoCalculoDTO calcular(CalculoRequest request) {
        int anoVigente = LocalDate.now().getYear();

        // Busca alíquota do banco — NUNCA hardcodar alíquotas no código (RN-003).
        AliquotaVigente aliquota = aliquotaVigenteDao
                .buscarVigente(request.regime(), request.tipoImovel(), anoVigente)
                .orElseThrow(() -> new RuntimeException(
                        "Alíquota não encontrada para: regime=" + request.regime()
                        + ", tipo=" + request.tipoImovel()
                        + ", ano=" + anoVigente));

        BigDecimal valorBase = request.valorBase();

        // Cálculo IBS e CBS (scale 4 para precisão fiscal).
        BigDecimal valorIbs = valorBase
                .multiply(aliquota.getAliquotaIbs())
                .setScale(4, RoundingMode.HALF_UP);

        BigDecimal valorCbs = valorBase
                .multiply(aliquota.getAliquotaCbs())
                .setScale(4, RoundingMode.HALF_UP);

        // Valor líquido que o locador recebe após o Split Payment (scale 2).
        BigDecimal valorLiquido = valorBase
                .subtract(valorIbs)
                .subtract(valorCbs)
                .setScale(2, RoundingMode.HALF_UP);

        return new ResultadoCalculoDTO(
                request.regime(),
                request.tipoImovel(),
                valorBase,
                aliquota.getAliquotaIbs(),
                aliquota.getAliquotaCbs(),
                valorIbs,
                valorCbs,
                valorLiquido,
                true  // splitPaymentRequerido = true a partir de 2026
        );
    }
}
