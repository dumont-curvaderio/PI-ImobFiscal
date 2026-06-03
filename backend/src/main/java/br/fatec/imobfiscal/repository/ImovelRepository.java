package br.fatec.imobfiscal.repository;

import br.fatec.imobfiscal.model.Imovel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImovelRepository extends JpaRepository<Imovel, UUID> {
    List<Imovel> findByImobiliariaIdAndDeletedAtIsNullOrderByCreatedAtDesc(UUID imobiliariaId);
    Optional<Imovel> findByIdAndImobiliariaIdAndDeletedAtIsNull(UUID id, UUID imobiliariaId);
}
