package br.fatec.imobfiscal.repository;

import br.fatec.imobfiscal.model.Locador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocadorRepository extends JpaRepository<Locador, UUID> {
    List<Locador> findByImobiliariaIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID imobiliariaId);
    Optional<Locador> findByIdAndImobiliariaIdAndDeletedAtIsNull(UUID id, UUID imobiliariaId);
}
