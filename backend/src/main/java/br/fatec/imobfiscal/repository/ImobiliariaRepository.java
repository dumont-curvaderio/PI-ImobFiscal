package br.fatec.imobfiscal.repository;

import br.fatec.imobfiscal.model.Imobiliaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImobiliariaRepository extends JpaRepository<Imobiliaria, UUID> {
    Optional<Imobiliaria> findByIdAndDeletedAtIsNull(UUID id);
    boolean existsByCnpj(String cnpj);
}
