package br.fatec.imobfiscal.controller;

import br.fatec.imobfiscal.dto.notafiscal.NotaFiscalRequest;
import br.fatec.imobfiscal.dto.notafiscal.NotaFiscalResponse;
import br.fatec.imobfiscal.enums.StatusNFe;
import br.fatec.imobfiscal.service.NotaFiscalService;
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

    private final NotaFiscalService notaFiscalService;

    // GET /api/imobiliarias/{imobiliariaId}/notas-fiscais
    @GetMapping
    public ResponseEntity<List<NotaFiscalResponse>> listar(
            @PathVariable UUID imobiliariaId) {
        return ResponseEntity.ok(notaFiscalService.listar(imobiliariaId));
    }

    // GET /api/imobiliarias/{imobiliariaId}/notas-fiscais/por-contrato/{contratoId}
    @GetMapping("/por-contrato/{contratoId}")
    public ResponseEntity<List<NotaFiscalResponse>> listarPorContrato(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID contratoId) {
        return ResponseEntity.ok(notaFiscalService.listarPorContrato(imobiliariaId, contratoId));
    }

    // GET /api/imobiliarias/{imobiliariaId}/notas-fiscais/{id}
    @GetMapping("/{id}")
    public ResponseEntity<NotaFiscalResponse> buscarPorId(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(notaFiscalService.buscarPorId(imobiliariaId, id));
    }

    // POST /api/imobiliarias/{imobiliariaId}/notas-fiscais
    // Cria a nota com status AGUARDANDO — transmissão para SEFAZ é assíncrona
    @PostMapping
    public ResponseEntity<NotaFiscalResponse> criar(
            @PathVariable UUID imobiliariaId,
            @Valid @RequestBody NotaFiscalRequest request) {
        NotaFiscalResponse response = notaFiscalService.criar(imobiliariaId, request);
        return ResponseEntity.status(201).body(response);
    }

    // PATCH /api/imobiliarias/{imobiliariaId}/notas-fiscais/{id}/status?status=AUTORIZADA
    @PatchMapping("/{id}/status")
    public ResponseEntity<NotaFiscalResponse> atualizarStatus(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID id,
            @RequestParam StatusNFe status) {
        return ResponseEntity.ok(notaFiscalService.atualizarStatus(imobiliariaId, id, status));
    }
}
