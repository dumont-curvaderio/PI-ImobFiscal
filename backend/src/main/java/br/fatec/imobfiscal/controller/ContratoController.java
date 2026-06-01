package br.fatec.imobfiscal.controller;

import br.fatec.imobfiscal.dto.contrato.ContratoRequest;
import br.fatec.imobfiscal.dto.contrato.ContratoResponse;
import br.fatec.imobfiscal.enums.StatusContrato;
import br.fatec.imobfiscal.service.ContratoService;
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

    private final ContratoService contratoService;

    @GetMapping
    public ResponseEntity<List<ContratoResponse>> listar(
            @PathVariable UUID imobiliariaId) {
        return ResponseEntity.ok(contratoService.listar(imobiliariaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContratoResponse> buscarPorId(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(contratoService.buscarPorId(imobiliariaId, id));
    }

    @PostMapping
    public ResponseEntity<ContratoResponse> criar(
            @PathVariable UUID imobiliariaId,
            @Valid @RequestBody ContratoRequest request) {
        ContratoResponse response = contratoService.criar(imobiliariaId, request);
        return ResponseEntity.status(201).body(response);
    }

    // PATCH /api/imobiliarias/{imobiliariaId}/contratos/{id}/status
    // Atualiza apenas o status (ex: RASCUNHO → ATIVO)
    @PatchMapping("/{id}/status")
    public ResponseEntity<ContratoResponse> atualizarStatus(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID id,
            @RequestParam StatusContrato status) {
        return ResponseEntity.ok(contratoService.atualizarStatus(imobiliariaId, id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID id) {
        contratoService.deletar(imobiliariaId, id);
        return ResponseEntity.noContent().build();
    }
}
