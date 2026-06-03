package br.fatec.imobfiscal.model;

import br.fatec.imobfiscal.model.dao.AliquotaVigenteDao;
import br.fatec.imobfiscal.view.motor.CalculoRequest;
import br.fatec.imobfiscal.view.motor.ResultadoCalculoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class MotorTributario {

    private final AliquotaVigenteDao aliquotaVigenteDao;

    public ResultadoCalculoDTO calcular(CalculoRequest request) {
        int anoVigente = LocalDate.now().getYear();

        AliquotaVigente aliquota = aliquotaVigenteDao
                .buscarVigente(request.regime(), request.tipoImovel(), anoVigente)
                .orElseThrow(() -> new RuntimeException(
                        "Alíquota não encontrada para: regime=" + request.regime()
                        + ", tipo=" + request.tipoImovel()
                        + ", ano=" + anoVigente));

        BigDecimal valorBase = request.valorBase();

        BigDecimal valorIbs = valorBase
                .multiply(aliquota.getAliquotaIbs())
                .setScale(4, RoundingMode.HALF_UP);

        BigDecimal valorCbs = valorBase
                .multiply(aliquota.getAliquotaCbs())
                .setScale(4, RoundingMode.HALF_UP);

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
                true
        );
    }
}
