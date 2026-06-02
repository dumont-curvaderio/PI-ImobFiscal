package br.fatec.imobfiscal.controller;

import br.fatec.imobfiscal.model.Imovel;
import br.fatec.imobfiscal.model.dao.ImovelDao;
import br.fatec.imobfiscal.view.imovel.ImovelRequest;
import br.fatec.imobfiscal.view.imovel.ImovelResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

// @RestController = @Controller + @ResponseBody (converte retorno para JSON automaticamente).
// No padrão MVC sem service, o controller chama o DAO diretamente.
@RestController
@RequestMapping("/api/imobiliarias/{imobiliariaId}/imoveis")
@RequiredArgsConstructor
public class ImovelController {

    private final ImovelDao imovelDao;

    // GET /api/imobiliarias/{imobiliariaId}/imoveis → lista todos os imóveis
    @GetMapping
    public ResponseEntity<List<ImovelResponse>> listar(@PathVariable UUID imobiliariaId) {
        List<ImovelResponse> resposta = imovelDao.listar(imobiliariaId)
                .stream()
                .map(ImovelResponse::from)
                .toList();
        return ResponseEntity.ok(resposta);
    }

    // GET /api/imobiliarias/{imobiliariaId}/imoveis/{id} → busca um imóvel
    @GetMapping("/{id}")
    public ResponseEntity<ImovelResponse> buscarPorId(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(ImovelResponse.from(imovelDao.buscar(imobiliariaId, id)));
    }

    // POST /api/imobiliarias/{imobiliariaId}/imoveis → cria um imóvel
    // @Valid dispara as validações anotadas no DTO (@NotBlank, @Size, etc.)
    @PostMapping
    public ResponseEntity<ImovelResponse> criar(
            @PathVariable UUID imobiliariaId,
            @Valid @RequestBody ImovelRequest request) {
        // Monta o model a partir do request (via setters — sem builder).
        Imovel imovel = new Imovel();
        imovel.setImobiliariaId(imobiliariaId);
        imovel.setLocadorId(request.locadorId());   // opcional
        imovel.setCodigo(request.codigo());
        imovel.setTipo(request.tipo());
        imovel.setCep(request.cep());
        imovel.setLogradouro(request.logradouro());
        imovel.setNumero(request.numero());
        imovel.setComplemento(request.complemento());
        imovel.setBairro(request.bairro());
        imovel.setCidade(request.cidade());
        imovel.setUf(request.uf());
        imovel.setAreaTotal(request.areaTotal());
        imovel.setQuartos(request.quartos());
        imovel.setVagas(request.vagas());
        imovel.setValorCompra(request.valorCompra());
        imovel.setDataCompra(request.dataCompra());
        imovel.setValorVenal(request.valorVenal());

        ImovelResponse response = ImovelResponse.from(imovelDao.inserir(imovel));
        return ResponseEntity.status(201).body(response);
    }

    // PUT /api/imobiliarias/{imobiliariaId}/imoveis/{id} → atualiza um imóvel
    @PutMapping("/{id}")
    public ResponseEntity<ImovelResponse> atualizar(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID id,
            @Valid @RequestBody ImovelRequest request) {
        // Garante que o imóvel existe e pertence à imobiliária antes de atualizar.
        Imovel imovel = imovelDao.buscar(imobiliariaId, id);
        imovel.setLocadorId(request.locadorId());
        imovel.setCodigo(request.codigo());
        imovel.setTipo(request.tipo());
        imovel.setCep(request.cep());
        imovel.setLogradouro(request.logradouro());
        imovel.setNumero(request.numero());
        imovel.setComplemento(request.complemento());
        imovel.setBairro(request.bairro());
        imovel.setCidade(request.cidade());
        imovel.setUf(request.uf());
        imovel.setAreaTotal(request.areaTotal());
        imovel.setQuartos(request.quartos());
        imovel.setVagas(request.vagas());
        imovel.setValorCompra(request.valorCompra());
        imovel.setDataCompra(request.dataCompra());
        imovel.setValorVenal(request.valorVenal());

        return ResponseEntity.ok(ImovelResponse.from(imovelDao.atualizar(imovel)));
    }

    // DELETE /api/imobiliarias/{imobiliariaId}/imoveis/{id} → soft delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID id) {
        // Confirma que existe (e é da imobiliária) antes de marcar deleted_at.
        imovelDao.buscar(imobiliariaId, id);
        imovelDao.softDelete(imobiliariaId, id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
