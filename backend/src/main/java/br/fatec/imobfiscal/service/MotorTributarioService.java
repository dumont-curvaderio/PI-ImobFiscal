package br.fatec.imobfiscal.service;

import br.fatec.imobfiscal.dto.motor.CalculoRequest;
import br.fatec.imobfiscal.dto.motor.ResultadoCalculoDTO;
import br.fatec.imobfiscal.entity.AliquotaVigente;
import br.fatec.imobfiscal.repository.AliquotaVigenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

// Motor Tributário — calcula IBS/CBS conforme a Reforma Tributária (LC 214/2025)
// Padrão Strategy: busca as alíquotas do banco, nunca hardcoda valores
@Service
@RequiredArgsConstructor
public class MotorTributarioService {

    private final AliquotaVigenteRepository aliquotaVigenteRepository;

    public ResultadoCalculoDTO calcular(CalculoRequest request) {
        int anoVigente = LocalDate.now().getYear();

        // Busca alíquota do banco — NUNCA hardcodar alíquotas no código
        AliquotaVigente aliquota = aliquotaVigenteRepository
                .findByRegimeAndTipoImovelAndAnoVigencia(
                        request.regime(),
                        request.tipoImovel(),
                        anoVigente)
                .orElseThrow(() -> new RuntimeException(
                        "Alíquota não encontrada para: regime=" + request.regime()
                        + ", tipo=" + request.tipoImovel()
                        + ", ano=" + anoVigente));

        BigDecimal valorBase = request.valorBase();

        // Cálculo IBS e CBS (Scale 4 para precisão fiscal)
        BigDecimal valorIbs = valorBase
                .multiply(aliquota.getAliquotaIbs())
                .setScale(4, RoundingMode.HALF_UP);

        BigDecimal valorCbs = valorBase
                .multiply(aliquota.getAliquotaCbs())
                .setScale(4, RoundingMode.HALF_UP);

        // Valor líquido que o locador recebe após o Split Payment
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
