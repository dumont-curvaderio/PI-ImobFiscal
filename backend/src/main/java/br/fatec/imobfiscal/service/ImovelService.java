package br.fatec.imobfiscal.service;

import br.fatec.imobfiscal.dto.imovel.ImovelRequest;
import br.fatec.imobfiscal.dto.imovel.ImovelResponse;
import br.fatec.imobfiscal.entity.Imobiliaria;
import br.fatec.imobfiscal.entity.Imovel;
import br.fatec.imobfiscal.entity.Locador;
import br.fatec.imobfiscal.repository.ImobiliariaRepository;
import br.fatec.imobfiscal.repository.ImovelRepository;
import br.fatec.imobfiscal.repository.LocadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImovelService {

    private final ImovelRepository imovelRepository;
    private final ImobiliariaRepository imobiliariaRepository;
    private final LocadorRepository locadorRepository;

    public List<ImovelResponse> listar(UUID imobiliariaId) {
        return imovelRepository
                .findAllByImobiliariaIdAndDeletedAtIsNull(imobiliariaId)
                .stream()
                .map(ImovelResponse::from)
                .toList();
    }

    public ImovelResponse buscarPorId(UUID imobiliariaId, UUID id) {
        Imovel imovel = imovelRepository.findById(id)
                .filter(i -> i.getImobiliaria().getId().equals(imobiliariaId))
                .filter(i -> i.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Imóvel não encontrado"));
        return ImovelResponse.from(imovel);
    }

    @Transactional
    public ImovelResponse criar(UUID imobiliariaId, ImovelRequest request) {
        Imobiliaria imobiliaria = imobiliariaRepository.findById(imobiliariaId)
                .orElseThrow(() -> new RuntimeException("Imobiliária não encontrada"));

        Locador locador = locadorRepository.findById(request.locadorId())
                .orElseThrow(() -> new RuntimeException("Locador não encontrado"));

        Imovel imovel = Imovel.builder()
                .imobiliaria(imobiliaria)
                .locador(locador)
                .codigo(request.codigo())
                .tipo(request.tipo())
                .cep(request.cep())
                .logradouro(request.logradouro())
                .numero(request.numero())
                .complemento(request.complemento())
                .bairro(request.bairro())
                .cidade(request.cidade())
                .uf(request.uf())
                .areaTotal(request.areaTotal())
                .quartos(request.quartos())
                .vagas(request.vagas())
                .valorCompra(request.valorCompra())
                .dataCompra(request.dataCompra())
                .build();

        return ImovelResponse.from(imovelRepository.save(imovel));
    }

    @Transactional
    public ImovelResponse atualizar(UUID imobiliariaId, UUID id, ImovelRequest request) {
        Imovel imovel = imovelRepository.findById(id)
                .filter(i -> i.getImobiliaria().getId().equals(imobiliariaId))
                .filter(i -> i.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Imóvel não encontrado"));

        imovel.setCodigo(request.codigo());
        imovel.setTipo(request.tipo());
        imovel.setCep(request.cep());
        imovel.setLogradouro(request.logradouro());
        imovel.setNumero(request.numero());
        imovel.setComplemento(request.complemento());
        imovel.setBairro(request.bairro());
        imovel.setCidade(request.cidade());
        imovel.setUf(request.uf());
        imovel.setAreaTotal(request.areaTotal());
        imovel.setQuartos(request.quartos());
        imovel.setVagas(request.vagas());
        imovel.setValorCompra(request.valorCompra());
        imovel.setDataCompra(request.dataCompra());

        return ImovelResponse.from(imovelRepository.save(imovel));
    }

    // Soft delete: preenche deleted_at ao invés de apagar o registro
    @Transactional
    public void deletar(UUID imobiliariaId, UUID id) {
        Imovel imovel = imovelRepository.findById(id)
                .filter(i -> i.getImobiliaria().getId().equals(imobiliariaId))
                .filter(i -> i.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Imóvel não encontrado"));

        imovel.setDeletedAt(LocalDateTime.now());
        imovelRepository.save(imovel);
    }
}
