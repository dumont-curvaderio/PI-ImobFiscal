package br.fatec.imobfiscal.repository;

import br.fatec.imobfiscal.entity.Boleto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoletoRepository extends JpaRepository<Boleto, UUID> {

    List<Boleto> findByImobiliariaIdAndDeletedAtIsNull(UUID imobiliariaId);

    List<Boleto> findByContrato_IdAndDeletedAtIsNull(UUID contratoId);

    Optional<Boleto> findByIdAndDeletedAtIsNull(UUID id);
}
