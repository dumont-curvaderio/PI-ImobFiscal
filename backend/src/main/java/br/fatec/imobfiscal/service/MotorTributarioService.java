package br.fatec.imobfiscal.service;

import br.fatec.imobfiscal.model.AliquotaVigente;
import br.fatec.imobfiscal.repository.AliquotaVigenteRepository;
import br.fatec.imobfiscal.view.motor.CalculoRequest;
import br.fatec.imobfiscal.view.motor.ResultadoCalculoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MotorTributarioService {

    private final AliquotaVigenteRepository repository;

    public ResultadoCalculoDTO calcular(CalculoRequest request) {
        int anoVigente = LocalDate.now().getYear();

        AliquotaVigente aliquota = repository
                .findByRegimeAndTipoImovelAndAnoVigencia(request.regime(), request.tipoImovel(), anoVigente)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
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
