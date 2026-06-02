package br.fatec.imobfiscal.controller;

import br.fatec.imobfiscal.model.Locador;
import br.fatec.imobfiscal.model.dao.LocadorDao;
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

    private final LocadorDao locadorDao;

    @GetMapping
    public ResponseEntity<List<LocadorResponse>> listar(@PathVariable UUID imobiliariaId) {
        List<LocadorResponse> resposta = locadorDao.listar(imobiliariaId)
                .stream()
                .map(LocadorResponse::from)
                .toList();
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocadorResponse> buscarPorId(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(LocadorResponse.from(locadorDao.buscar(imobiliariaId, id)));
    }

    @PostMapping
    public ResponseEntity<LocadorResponse> criar(
            @PathVariable UUID imobiliariaId,
            @Valid @RequestBody LocadorRequest request) {
        Locador locador = new Locador();
        locador.setImobiliariaId(imobiliariaId);
        locador.setTipoPessoa(request.tipoPessoa());
        locador.setCpfCnpj(request.cpfCnpj());
        locador.setNome(request.nome());
        locador.setEmail(request.email());
        locador.setTelefone(request.telefone());
        locador.setRegimeTributario(request.regimeTributario());

        LocadorResponse response = LocadorResponse.from(locadorDao.inserir(locador));
        return ResponseEntity.status(201).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocadorResponse> atualizar(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID id,
            @Valid @RequestBody LocadorRequest request) {
        // Confirma que existe e pertence à imobiliária antes de atualizar.
        Locador locador = locadorDao.buscar(imobiliariaId, id);
        locador.setTipoPessoa(request.tipoPessoa());
        locador.setCpfCnpj(request.cpfCnpj());
        locador.setNome(request.nome());
        locador.setEmail(request.email());
        locador.setTelefone(request.telefone());
        locador.setRegimeTributario(request.regimeTributario());

        return ResponseEntity.ok(LocadorResponse.from(locadorDao.atualizar(locador)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable UUID imobiliariaId,
            @PathVariable UUID id) {
        locadorDao.buscar(imobiliariaId, id);
        locadorDao.softDelete(imobiliariaId, id);
        return ResponseEntity.noContent().build();
    }
}
