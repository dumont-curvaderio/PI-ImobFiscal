package br.fatec.imobfiscal.repository;

import br.fatec.imobfiscal.model.ContratoLocacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContratoRepository extends JpaRepository<ContratoLocacao, UUID> {
    List<ContratoLocacao> findByImobiliariaIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID imobiliariaId);
    Optional<ContratoLocacao> findByIdAndImobiliariaIdAndDeletedAtIsNull(UUID id, UUID imobiliariaId);
}
