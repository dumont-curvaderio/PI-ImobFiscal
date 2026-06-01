package br.fatec.imobfiscal.repository;

import br.fatec.imobfiscal.entity.Locador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocadorRepository extends JpaRepository<Locador, UUID> {

    // Filtra apenas registros ativos (não deletados) da imobiliária
    List<Locador> findAllByImobiliariaIdAndDeletedAtIsNull(UUID imobiliariaId);

    Optional<Locador> findByCpfCnpj(String cpfCnpj);
}
