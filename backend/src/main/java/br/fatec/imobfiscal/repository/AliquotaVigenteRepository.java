package br.fatec.imobfiscal.repository;

import br.fatec.imobfiscal.entity.AliquotaVigente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AliquotaVigenteRepository extends JpaRepository<AliquotaVigente, UUID> {
    Optional<AliquotaVigente> findByRegimeAndTipoImovelAndAnoVigencia(
            String regime, String tipoImovel, Integer anoVigencia);
}
