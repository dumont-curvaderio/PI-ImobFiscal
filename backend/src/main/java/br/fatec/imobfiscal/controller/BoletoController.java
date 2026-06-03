package br.fatec.imobfiscal.controller;

import br.fatec.imobfiscal.service.BoletoService;
import br.fatec.imobfiscal.view.boleto.BoletoRequest;
import br.fatec.imobfiscal.view.boleto.BoletoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/imobiliarias/{imobiliariaId}/boletos")
@RequiredArgsConstructor
public class BoletoController {

    private final BoletoService boletoService;

    @GetMapping
    public ResponseEntity<List<BoletoResponse>> listar(@PathVariable UUID imobiliariaId) {
        return ResponseEntity.ok(boletoService.listar(imobiliariaId).stream().map(BoletoResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoletoResponse> buscarPorId(@PathVariable UUID imobiliariaId, @PathVariable UUID id) {
        return ResponseEntity.ok(BoletoResponse.from(boletoService.buscar(imobiliariaId, id)));
    }

    @PostMapping("/gerar")
    public ResponseEntity<BoletoResponse> gerar(@PathVariable UUID imobiliariaId, @Valid @RequestBody BoletoRequest request) {
        return ResponseEntity.status(201).body(BoletoResponse.from(boletoService.gerar(imobiliariaId, request)));
    }
}
