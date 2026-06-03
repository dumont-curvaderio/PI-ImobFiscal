package br.fatec.imobfiscal.controller;

import br.fatec.imobfiscal.model.GeradorBoleto;
import br.fatec.imobfiscal.model.dao.BoletoDao;
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

    private final BoletoDao boletoDao;
    private final GeradorBoleto geradorBoleto;

    @GetMapping
    public ResponseEntity<List<BoletoResponse>> listar(@PathVariable UUID imobiliariaId) {
        List<BoletoResponse> resposta = boletoDao.listar(imobiliariaId)
                .stream()
                .map(BoletoResponse::from)
                .toList();
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoletoResponse> buscarPorId(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(BoletoResponse.from(boletoDao.buscar(imobiliariaId, id)));
    }

    @PostMapping("/gerar")
    public ResponseEntity<BoletoResponse> gerar(
            @PathVariable UUID imobiliariaId,
            @Valid @RequestBody BoletoRequest request) {
        BoletoResponse response = BoletoResponse.from(geradorBoleto.gerar(imobiliariaId, request));
        return ResponseEntity.status(201).body(response);
    }
}
