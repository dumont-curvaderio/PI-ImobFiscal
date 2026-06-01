package br.fatec.imobfiscal.controller;

import br.fatec.imobfiscal.dto.imovel.ImovelRequest;
import br.fatec.imobfiscal.dto.imovel.ImovelResponse;
import br.fatec.imobfiscal.service.ImovelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// @RestController = @Controller + @ResponseBody (converte retorno para JSON automaticamente)
// @RequestMapping define o prefixo de todas as rotas desta classe
@RestController
@RequestMapping("/api/imobiliarias/{imobiliariaId}/imoveis")
@RequiredArgsConstructor
public class ImovelController {

    private final ImovelService imovelService;

    // GET /api/imobiliarias/{imobiliariaId}/imoveis → lista todos os imóveis
    @GetMapping
    public ResponseEntity<List<ImovelResponse>> listar(
            @PathVariable UUID imobiliariaId) {
        return ResponseEntity.ok(imovelService.listar(imobiliariaId));
    }

    // GET /api/imobiliarias/{imobiliariaId}/imoveis/{id} → busca um imóvel
    @GetMapping("/{id}")
    public ResponseEntity<ImovelResponse> buscarPorId(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(imovelService.buscarPorId(imobiliariaId, id));
    }

    // POST /api/imobiliarias/{imobiliariaId}/imoveis → cria um imóvel
    // @Valid dispara as validações anotadas no DTO (@NotBlank, @Size, etc.)
    @PostMapping
    public ResponseEntity<ImovelResponse> criar(
            @PathVariable UUID imobiliariaId,
            @Valid @RequestBody ImovelRequest request) {
        ImovelResponse response = imovelService.criar(imobiliariaId, request);
        return ResponseEntity.status(201).body(response);
    }

    // PUT /api/imobiliarias/{imobiliariaId}/imoveis/{id} → atualiza um imóvel
    @PutMapping("/{id}")
    public ResponseEntity<ImovelResponse> atualizar(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID id,
            @Valid @RequestBody ImovelRequest request) {
        return ResponseEntity.ok(imovelService.atualizar(imobiliariaId, id, request));
    }

    // DELETE /api/imobiliarias/{imobiliariaId}/imoveis/{id} → soft delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID id) {
        imovelService.deletar(imobiliariaId, id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
