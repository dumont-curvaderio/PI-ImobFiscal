package br.fatec.imobfiscal.controller;

import br.fatec.imobfiscal.enums.StatusNFe;
import br.fatec.imobfiscal.model.NotaFiscal;
import br.fatec.imobfiscal.model.dao.ContratoDao;
import br.fatec.imobfiscal.model.dao.NotaFiscalDao;
import br.fatec.imobfiscal.view.notafiscal.NotaFiscalRequest;
import br.fatec.imobfiscal.view.notafiscal.NotaFiscalResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/imobiliarias/{imobiliariaId}/notas-fiscais")
@RequiredArgsConstructor
public class NotaFiscalController {

    private final NotaFiscalDao notaFiscalDao;
    private final ContratoDao contratoDao;

    @GetMapping
    public ResponseEntity<List<NotaFiscalResponse>> listar(@PathVariable UUID imobiliariaId) {
        List<NotaFiscalResponse> resposta = notaFiscalDao.listar(imobiliariaId)
                .stream()
                .map(NotaFiscalResponse::from)
                .toList();
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/por-contrato/{contratoId}")
    public ResponseEntity<List<NotaFiscalResponse>> listarPorContrato(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID contratoId) {
        contratoDao.buscar(imobiliariaId, contratoId);

        List<NotaFiscalResponse> resposta = notaFiscalDao.listarPorContrato(contratoId)
                .stream()
                .map(NotaFiscalResponse::from)
                .toList();
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotaFiscalResponse> buscarPorId(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(NotaFiscalResponse.from(notaFiscalDao.buscar(imobiliariaId, id)));
    }

    @PostMapping
    public ResponseEntity<NotaFiscalResponse> criar(
            @PathVariable UUID imobiliariaId,
            @Valid @RequestBody NotaFiscalRequest request) {
        contratoDao.buscar(imobiliariaId, request.contratoId());

        NotaFiscal nf = new NotaFiscal();
        nf.setImobiliariaId(imobiliariaId);
        nf.setContratoId(request.contratoId());
        nf.setValorServico(request.valorServico());
        NotaFiscalResponse response = NotaFiscalResponse.from(notaFiscalDao.inserir(nf));
        return ResponseEntity.status(201).body(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<NotaFiscalResponse> atualizarStatus(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID id,
            @RequestParam StatusNFe status) {
        notaFiscalDao.buscar(imobiliariaId, id);
        notaFiscalDao.atualizarStatus(imobiliariaId, id, status);
        return ResponseEntity.ok(NotaFiscalResponse.from(notaFiscalDao.buscar(imobiliariaId, id)));
    }
}
