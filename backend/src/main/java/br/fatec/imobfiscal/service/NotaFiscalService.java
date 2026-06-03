package br.fatec.imobfiscal.service;

import br.fatec.imobfiscal.enums.StatusNFe;
import br.fatec.imobfiscal.model.NotaFiscal;
import br.fatec.imobfiscal.repository.NotaFiscalRepository;
import br.fatec.imobfiscal.model.NotaFiscalRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotaFiscalService {

    private final NotaFiscalRepository repository;
    private final ContratoService contratoService;

    public List<NotaFiscal> listar(UUID imobiliariaId) {
        return repository.findByImobiliariaIdAndDeletedAtIsNullOrderByCreatedAtDesc(imobiliariaId);
    }

    public List<NotaFiscal> listarPorContrato(UUID imobiliariaId, UUID contratoId) {
        contratoService.buscar(imobiliariaId, contratoId);
        return repository.findByContratoIdAndDeletedAtIsNullOrderByCreatedAtDesc(contratoId);
    }

    public NotaFiscal buscar(UUID imobiliariaId, UUID id) {
        return repository.findByIdAndImobiliariaIdAndDeletedAtIsNull(id, imobiliariaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nota fiscal não encontrada"));
    }

    public NotaFiscal criar(UUID imobiliariaId, NotaFiscalRequest request) {
        contratoService.buscar(imobiliariaId, request.contratoId());

        NotaFiscal nf = new NotaFiscal();
        nf.setImobiliariaId(imobiliariaId);
        nf.setContratoId(request.contratoId());
        nf.setValorServico(request.valorServico());
        return repository.save(nf);
    }

    public NotaFiscal atualizarStatus(UUID imobiliariaId, UUID id, StatusNFe novoStatus) {
        NotaFiscal nf = buscar(imobiliariaId, id);
        nf.setStatus(novoStatus);
        return repository.save(nf);
    }
}
