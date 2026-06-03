package br.fatec.imobfiscal.controller;

import br.fatec.imobfiscal.service.LocadorService;
import br.fatec.imobfiscal.view.locador.LocadorRequest;
import br.fatec.imobfiscal.view.locador.LocadorResponse;
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
    public ResponseEntity<List<LocadorResponse>> listar(@PathVariable UUID imobiliariaId) {
        return ResponseEntity.ok(locadorService.listar(imobiliariaId).stream().map(LocadorResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocadorResponse> buscarPorId(@PathVariable UUID imobiliariaId, @PathVariable UUID id) {
        return ResponseEntity.ok(LocadorResponse.from(locadorService.buscar(imobiliariaId, id)));
    }

    @PostMapping
    public ResponseEntity<LocadorResponse> criar(@PathVariable UUID imobiliariaId, @Valid @RequestBody LocadorRequest request) {
        return ResponseEntity.status(201).body(LocadorResponse.from(locadorService.criar(imobiliariaId, request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocadorResponse> atualizar(@PathVariable UUID imobiliariaId, @PathVariable UUID id, @Valid @RequestBody LocadorRequest request) {
        return ResponseEntity.ok(LocadorResponse.from(locadorService.atualizar(imobiliariaId, id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID imobiliariaId, @PathVariable UUID id) {
        locadorService.deletar(imobiliariaId, id);
        return ResponseEntity.noContent().build();
    }
}
