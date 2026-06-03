package br.fatec.imobfiscal.repository;

import br.fatec.imobfiscal.model.NotaFiscal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotaFiscalRepository extends JpaRepository<NotaFiscal, UUID> {
    List<NotaFiscal> findByImobiliariaIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID imobiliariaId);
    Optional<NotaFiscal> findByIdAndImobiliariaIdAndDeletedAtIsNull(UUID id, UUID imobiliariaId);
    List<NotaFiscal> findByContratoIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID contratoId);
}
