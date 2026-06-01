package br.fatec.imobfiscal.repository;

import br.fatec.imobfiscal.entity.Imobiliaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// JpaRepository já traz: save, findById, findAll, delete, count, etc.
// Nós só adicionamos os métodos específicos que precisamos
@Repository
public interface ImobiliariaRepository extends JpaRepository<Imobiliaria, UUID> {

    Optional<Imobiliaria> findByCnpj(String cnpj);

    // Spring Data converte este nome de método em SQL automaticamente:
    // WHERE deleted_at IS NULL
    List<Imobiliaria> findAllByDeletedAtIsNull();
}
