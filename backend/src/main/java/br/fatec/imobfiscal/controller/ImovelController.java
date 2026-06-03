package br.fatec.imobfiscal.controller;

import br.fatec.imobfiscal.service.ImovelService;
import br.fatec.imobfiscal.view.imovel.ImovelRequest;
import br.fatec.imobfiscal.view.imovel.ImovelResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/imobiliarias/{imobiliariaId}/imoveis")
@RequiredArgsConstructor
public class ImovelController {

    private final ImovelService imovelService;

    @GetMapping
    public ResponseEntity<List<ImovelResponse>> listar(@PathVariable UUID imobiliariaId) {
        return ResponseEntity.ok(imovelService.listar(imobiliariaId).stream().map(ImovelResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImovelResponse> buscarPorId(@PathVariable UUID imobiliariaId, @PathVariable UUID id) {
        return ResponseEntity.ok(ImovelResponse.from(imovelService.buscar(imobiliariaId, id)));
    }

    @PostMapping
    public ResponseEntity<ImovelResponse> criar(@PathVariable UUID imobiliariaId, @Valid @RequestBody ImovelRequest request) {
        return ResponseEntity.status(201).body(ImovelResponse.from(imovelService.criar(imobiliariaId, request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ImovelResponse> atualizar(@PathVariable UUID imobiliariaId, @PathVariable UUID id, @Valid @RequestBody ImovelRequest request) {
        return ResponseEntity.ok(ImovelResponse.from(imovelService.atualizar(imobiliariaId, id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID imobiliariaId, @PathVariable UUID id) {
        imovelService.deletar(imobiliariaId, id);
        return ResponseEntity.noContent().build();
    }
}
