package br.fatec.imobfiscal.repository;

import br.fatec.imobfiscal.model.Boleto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BoletoRepository extends JpaRepository<Boleto, UUID> {
    List<Boleto> findByImobiliariaIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID imobiliariaId);
    Optional<Boleto> findByIdAndImobiliariaIdAndDeletedAtIsNull(UUID id, UUID imobiliariaId);
}
