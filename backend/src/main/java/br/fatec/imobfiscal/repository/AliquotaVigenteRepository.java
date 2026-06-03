package br.fatec.imobfiscal.repository;

import br.fatec.imobfiscal.model.AliquotaVigente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AliquotaVigenteRepository extends JpaRepository<AliquotaVigente, UUID> {
    Optional<AliquotaVigente> findByRegimeAndTipoImovelAndAnoVigencia(String regime, String tipoImovel, Integer anoVigencia);
}
