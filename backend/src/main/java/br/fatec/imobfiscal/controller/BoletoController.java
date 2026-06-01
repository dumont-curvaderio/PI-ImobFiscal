package br.fatec.imobfiscal.controller;

import br.fatec.imobfiscal.dto.boleto.BoletoRequest;
import br.fatec.imobfiscal.dto.boleto.BoletoResponse;
import br.fatec.imobfiscal.service.BoletoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// UC-003: Geração de boletos com detalhamento fiscal IBS/CBS (Split Payment)
@RestController
@RequestMapping("/api/imobiliarias/{imobiliariaId}/boletos")
@RequiredArgsConstructor
public class BoletoController {

    private final BoletoService boletoService;

    @GetMapping
    public ResponseEntity<List<BoletoResponse>> listar(@PathVariable UUID imobiliariaId) {
        return ResponseEntity.ok(boletoService.listar(imobiliariaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoletoResponse> buscarPorId(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(boletoService.buscarPorId(imobiliariaId, id));
    }

    @PostMapping("/gerar")
    public ResponseEntity<BoletoResponse> gerar(
            @PathVariable UUID imobiliariaId,
            @Valid @RequestBody BoletoRequest request) {
        return ResponseEntity.status(201).body(boletoService.gerar(imobiliariaId, request));
    }
}
