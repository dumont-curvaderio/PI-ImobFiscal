package br.fatec.imobfiscal.repository;

import br.fatec.imobfiscal.entity.NotaFiscal;
import br.fatec.imobfiscal.enums.StatusNFe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotaFiscalRepository extends JpaRepository<NotaFiscal, UUID> {

    List<NotaFiscal> findAllByImobiliariaIdAndDeletedAtIsNull(UUID imobiliariaId);

    List<NotaFiscal> findAllByContratoId(UUID contratoId);

    // Notas pendentes de transmissão (usadas pelo job de retry SEFAZ)
    List<NotaFiscal> findAllByStatusAndTentativasLessThan(StatusNFe status, int maxTentativas);
}
