package br.fatec.imobfiscal.service;

import br.fatec.imobfiscal.enums.StatusContrato;
import br.fatec.imobfiscal.model.ContratoLocacao;
import br.fatec.imobfiscal.repository.ContratoRepository;
import br.fatec.imobfiscal.model.ContratoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContratoService {

    private final ContratoRepository repository;
    private final ImovelService imovelService;

    public List<ContratoLocacao> listar(UUID imobiliariaId) {
        return repository.findByImobiliariaIdAndDeletedAtIsNullOrderByCreatedAtDesc(imobiliariaId);
    }

    public ContratoLocacao buscar(UUID imobiliariaId, UUID id) {
        return repository.findByIdAndImobiliariaIdAndDeletedAtIsNull(id, imobiliariaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrato não encontrado"));
    }

    public ContratoLocacao criar(UUID imobiliariaId, ContratoRequest request) {
        imovelService.buscar(imobiliariaId, request.imovelId());

        ContratoLocacao contrato = new ContratoLocacao();
        contrato.setImobiliariaId(imobiliariaId);
        contrato.setImovelId(request.imovelId());
        contrato.setTipoLocacao(request.tipoLocacao());
        contrato.setLocatarioTipo(request.locatarioTipo());
        contrato.setLocatarioCpfCnpj(request.locatarioCpfCnpj());
        contrato.setLocatarioNome(request.locatarioNome());
        contrato.setValorAluguel(request.valorAluguel());
        contrato.setDiaVencimento(request.diaVencimento());
        contrato.setDataInicio(request.dataInicio());
        contrato.setDataFim(request.dataFim());
        contrato.setPrazoMeses(request.prazoMeses());
        return repository.save(contrato);
    }

    public ContratoLocacao atualizarStatus(UUID imobiliariaId, UUID id, StatusContrato novoStatus) {
        ContratoLocacao contrato = buscar(imobiliariaId, id);
        contrato.setStatus(novoStatus);
        return repository.save(contrato);
    }

    public void deletar(UUID imobiliariaId, UUID id) {
        ContratoLocacao contrato = buscar(imobiliariaId, id);
        contrato.setDeletedAt(LocalDateTime.now());
        repository.save(contrato);
    }
}
