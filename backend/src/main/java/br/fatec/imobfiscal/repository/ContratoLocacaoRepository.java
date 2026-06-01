package br.fatec.imobfiscal.repository;

import br.fatec.imobfiscal.entity.ContratoLocacao;
import br.fatec.imobfiscal.enums.StatusContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContratoLocacaoRepository extends JpaRepository<ContratoLocacao, UUID> {

    List<ContratoLocacao> findAllByImobiliariaIdAndDeletedAtIsNull(UUID imobiliariaId);

    List<ContratoLocacao> findAllByImovelIdAndDeletedAtIsNull(UUID imovelId);

    // Contratos ativos de uma imobiliária (útil para relatórios e alertas)
    List<ContratoLocacao> findAllByImobiliariaIdAndStatusAndDeletedAtIsNull(
            UUID imobiliariaId, StatusContrato status);
}
