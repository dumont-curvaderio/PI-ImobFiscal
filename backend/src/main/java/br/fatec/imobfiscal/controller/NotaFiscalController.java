package br.fatec.imobfiscal.controller;

import br.fatec.imobfiscal.enums.StatusNFe;
import br.fatec.imobfiscal.service.NotaFiscalService;
import br.fatec.imobfiscal.model.NotaFiscalRequest;
import br.fatec.imobfiscal.model.NotaFiscalResponse;
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

    @GetMapping
    public ResponseEntity<List<NotaFiscalResponse>> listar(@PathVariable UUID imobiliariaId) {
        return ResponseEntity.ok(notaFiscalService.listar(imobiliariaId).stream().map(NotaFiscalResponse::from).toList());
    }

    @GetMapping("/por-contrato/{contratoId}")
    public ResponseEntity<List<NotaFiscalResponse>> listarPorContrato(@PathVariable UUID imobiliariaId, @PathVariable UUID contratoId) {
        return ResponseEntity.ok(notaFiscalService.listarPorContrato(imobiliariaId, contratoId).stream().map(NotaFiscalResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotaFiscalResponse> buscarPorId(@PathVariable UUID imobiliariaId, @PathVariable UUID id) {
        return ResponseEntity.ok(NotaFiscalResponse.from(notaFiscalService.buscar(imobiliariaId, id)));
    }

    @PostMapping
    public ResponseEntity<NotaFiscalResponse> criar(@PathVariable UUID imobiliariaId, @Valid @RequestBody NotaFiscalRequest request) {
        return ResponseEntity.status(201).body(NotaFiscalResponse.from(notaFiscalService.criar(imobiliariaId, request)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<NotaFiscalResponse> atualizarStatus(@PathVariable UUID imobiliariaId, @PathVariable UUID id, @RequestParam StatusNFe status) {
        return ResponseEntity.ok(NotaFiscalResponse.from(notaFiscalService.atualizarStatus(imobiliariaId, id, status)));
    }
}
