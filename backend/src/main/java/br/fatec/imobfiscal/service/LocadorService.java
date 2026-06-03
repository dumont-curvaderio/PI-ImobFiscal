package br.fatec.imobfiscal.service;

import br.fatec.imobfiscal.model.Locador;
import br.fatec.imobfiscal.repository.LocadorRepository;
import br.fatec.imobfiscal.view.locador.LocadorRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocadorService {

    private final LocadorRepository repository;

    public List<Locador> listar(UUID imobiliariaId) {
        return repository.findByImobiliariaIdAndDeletedAtIsNullOrderByCreatedAtDesc(imobiliariaId);
    }

    public Locador buscar(UUID imobiliariaId, UUID id) {
        return repository.findByIdAndImobiliariaIdAndDeletedAtIsNull(id, imobiliariaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Locador não encontrado"));
    }

    public Locador criar(UUID imobiliariaId, LocadorRequest request) {
        Locador locador = new Locador();
        locador.setImobiliariaId(imobiliariaId);
        locador.setTipoPessoa(request.tipoPessoa());
        locador.setCpfCnpj(request.cpfCnpj());
        locador.setNome(request.nome());
        locador.setEmail(request.email());
        locador.setTelefone(request.telefone());
        locador.setRegimeTributario(request.regimeTributario());
        return repository.save(locador);
    }

    public Locador atualizar(UUID imobiliariaId, UUID id, LocadorRequest request) {
        Locador locador = buscar(imobiliariaId, id);
        locador.setTipoPessoa(request.tipoPessoa());
        locador.setCpfCnpj(request.cpfCnpj());
        locador.setNome(request.nome());
        locador.setEmail(request.email());
        locador.setTelefone(request.telefone());
        locador.setRegimeTributario(request.regimeTributario());
        return repository.save(locador);
    }

    public void deletar(UUID imobiliariaId, UUID id) {
        Locador locador = buscar(imobiliariaId, id);
        locador.setDeletedAt(LocalDateTime.now());
        repository.save(locador);
    }
}
