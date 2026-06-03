package br.fatec.imobfiscal.service;

import br.fatec.imobfiscal.model.Imovel;
import br.fatec.imobfiscal.repository.ImovelRepository;
import br.fatec.imobfiscal.view.imovel.ImovelRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImovelService {

    private final ImovelRepository repository;

    public List<Imovel> listar(UUID imobiliariaId) {
        return repository.findByImobiliariaIdAndDeletedAtIsNullOrderByCreatedAtDesc(imobiliariaId);
    }

    public Imovel buscar(UUID imobiliariaId, UUID id) {
        return repository.findByIdAndImobiliariaIdAndDeletedAtIsNull(id, imobiliariaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Imóvel não encontrado"));
    }

    public Imovel criar(UUID imobiliariaId, ImovelRequest request) {
        Imovel imovel = new Imovel();
        imovel.setImobiliariaId(imobiliariaId);
        imovel.setLocadorId(request.locadorId());
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
        imovel.setValorVenal(request.valorVenal());
        return repository.save(imovel);
    }

    public Imovel atualizar(UUID imobiliariaId, UUID id, ImovelRequest request) {
        Imovel imovel = buscar(imobiliariaId, id);
        imovel.setLocadorId(request.locadorId());
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
        imovel.setValorVenal(request.valorVenal());
        return repository.save(imovel);
    }

    public void deletar(UUID imobiliariaId, UUID id) {
        Imovel imovel = buscar(imobiliariaId, id);
        imovel.setDeletedAt(LocalDateTime.now());
        repository.save(imovel);
    }
}
