package br.fatec.imobfiscal.config;

import br.fatec.imobfiscal.model.AliquotaVigente;
import br.fatec.imobfiscal.repository.AliquotaVigenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AliquotaVigenteRepository aliquotaRepo;

    @Override
    public void run(String... args) {
        if (aliquotaRepo.count() > 0) return;

        List<AliquotaVigente> aliquotas = List.of(
            aliquota("PF",               "RESIDENCIAL", "0.0145", "0.0076"),
            aliquota("PF",               "COMERCIAL",   "0.0290", "0.0153"),
            aliquota("SIMPLES_NACIONAL", "RESIDENCIAL", "0.0145", "0.0076"),
            aliquota("SIMPLES_NACIONAL", "COMERCIAL",   "0.0290", "0.0153"),
            aliquota("LUCRO_PRESUMIDO",  "RESIDENCIAL", "0.0200", "0.0100"),
            aliquota("LUCRO_PRESUMIDO",  "COMERCIAL",   "0.0400", "0.0200"),
            aliquota("LUCRO_REAL",       "RESIDENCIAL", "0.0250", "0.0125"),
            aliquota("LUCRO_REAL",       "COMERCIAL",   "0.0500", "0.0250")
        );

        aliquotaRepo.saveAll(aliquotas);
    }

    private AliquotaVigente aliquota(String regime, String tipoImovel, String ibs, String cbs) {
        AliquotaVigente a = new AliquotaVigente();
        a.setRegime(regime);
        a.setTipoImovel(tipoImovel);
        a.setAliquotaIbs(new BigDecimal(ibs));
        a.setAliquotaCbs(new BigDecimal(cbs));
        a.setAnoVigencia(2026);
        a.setCreatedAt(LocalDateTime.now());
        return a;
    }
}
