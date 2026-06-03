package br.fatec.imobfiscal.controller;

import br.fatec.imobfiscal.enums.StatusContrato;
import br.fatec.imobfiscal.model.ContratoLocacao;
import br.fatec.imobfiscal.model.dao.ContratoDao;
import br.fatec.imobfiscal.model.dao.ImovelDao;
import br.fatec.imobfiscal.view.contrato.ContratoRequest;
import br.fatec.imobfiscal.view.contrato.ContratoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/imobiliarias/{imobiliariaId}/contratos")
@RequiredArgsConstructor
public class ContratoController {

    private final ContratoDao contratoDao;
    private final ImovelDao imovelDao;

    @GetMapping
    public ResponseEntity<List<ContratoResponse>> listar(@PathVariable UUID imobiliariaId) {
        List<ContratoResponse> resposta = contratoDao.listar(imobiliariaId)
                .stream()
                .map(ContratoResponse::from)
                .toList();
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratoResponse> buscarPorId(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(ContratoResponse.from(contratoDao.buscar(imobiliariaId, id)));
    }

    @PostMapping
    public ResponseEntity<ContratoResponse> criar(
            @PathVariable UUID imobiliariaId,
            @Valid @RequestBody ContratoRequest request) {
        imovelDao.buscar(imobiliariaId, request.imovelId());

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

        ContratoResponse response = ContratoResponse.from(contratoDao.inserir(contrato));
        return ResponseEntity.status(201).body(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ContratoResponse> atualizarStatus(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID id,
            @RequestParam StatusContrato status) {
        contratoDao.buscar(imobiliariaId, id);
        contratoDao.atualizarStatus(imobiliariaId, id, status);
        return ResponseEntity.ok(ContratoResponse.from(contratoDao.buscar(imobiliariaId, id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID id) {
        contratoDao.buscar(imobiliariaId, id);
        contratoDao.softDelete(imobiliariaId, id);
        return ResponseEntity.noContent().build();
    }
}
