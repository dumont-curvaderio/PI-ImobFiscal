package br.fatec.imobfiscal.repository;

import br.fatec.imobfiscal.entity.Imovel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ImovelRepository extends JpaRepository<Imovel, UUID> {

    // Listagem padrão: só imóveis ativos da imobiliária
    List<Imovel> findAllByImobiliariaIdAndDeletedAtIsNull(UUID imobiliariaId);

    // Busca por código único dentro da imobiliária
    Optional<Imovel> findByImobiliariaIdAndCodigo(UUID imobiliariaId, String codigo);

    // Imóveis de um locador específico
    List<Imovel> findAllByLocadorIdAndDeletedAtIsNull(UUID locadorId);
}
