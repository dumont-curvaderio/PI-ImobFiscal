package br.fatec.imobfiscal.controller;

import br.fatec.imobfiscal.dto.locador.LocadorRequest;
import br.fatec.imobfiscal.dto.locador.LocadorResponse;
import br.fatec.imobfiscal.service.LocadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/imobiliarias/{imobiliariaId}/locadores")
@RequiredArgsConstructor
public class LocadorController {

    private final LocadorService locadorService;

    @GetMapping
    public ResponseEntity<List<LocadorResponse>> listar(
            @PathVariable UUID imobiliariaId) {
        return ResponseEntity.ok(locadorService.listar(imobiliariaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocadorResponse> buscarPorId(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(locadorService.buscarPorId(imobiliariaId, id));
    }

    @PostMapping
    public ResponseEntity<LocadorResponse> criar(
            @PathVariable UUID imobiliariaId,
            @Valid @RequestBody LocadorRequest request) {
        LocadorResponse response = locadorService.criar(imobiliariaId, request);
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocadorResponse> atualizar(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID id,
            @Valid @RequestBody LocadorRequest request) {
        return ResponseEntity.ok(locadorService.atualizar(imobiliariaId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID id) {
        locadorService.deletar(imobiliariaId, id);
        return ResponseEntity.noContent().build();
    }
}
