package br.fatec.imobfiscal.service;

import br.fatec.imobfiscal.dto.locador.LocadorRequest;
import br.fatec.imobfiscal.dto.locador.LocadorResponse;
import br.fatec.imobfiscal.entity.Locador;
import br.fatec.imobfiscal.repository.LocadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocadorService {

    private final LocadorRepository locadorRepository;

    // Lista todos os locadores ativos de uma imobiliária
    public List<LocadorResponse> listar(UUID imobiliariaId) {
        return locadorRepository
                .findAllByImobiliariaIdAndDeletedAtIsNull(imobiliariaId)
                .stream()
                .map(LocadorResponse::from)
                .toList();
    }

    // Busca um locador pelo id, garantindo que pertence à imobiliária (multi-tenancy)
    public LocadorResponse buscarPorId(UUID imobiliariaId, UUID id) {
        Locador locador = locadorRepository.findById(id)
                .filter(l -> l.getImobiliariaId().equals(imobiliariaId))
                .filter(l -> l.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Locador não encontrado"));
        return LocadorResponse.from(locador);
    }

    @Transactional
    public LocadorResponse criar(UUID imobiliariaId, LocadorRequest request) {
        Locador locador = Locador.builder()
                .imobiliariaId(imobiliariaId)
                .tipoPessoa(request.tipoPessoa())
                .cpfCnpj(request.cpfCnpj())
                .nome(request.nome())
                .email(request.email())
                .telefone(request.telefone())
                .build();

        return LocadorResponse.from(locadorRepository.save(locador));
    }

    @Transactional
    public LocadorResponse atualizar(UUID imobiliariaId, UUID id, LocadorRequest request) {
        Locador locador = locadorRepository.findById(id)
                .filter(l -> l.getImobiliariaId().equals(imobiliariaId))
                .filter(l -> l.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Locador não encontrado"));

        locador.setTipoPessoa(request.tipoPessoa());
        locador.setCpfCnpj(request.cpfCnpj());
        locador.setNome(request.nome());
        locador.setEmail(request.email());
        locador.setTelefone(request.telefone());

        return LocadorResponse.from(locadorRepository.save(locador));
    }

    // Soft delete: marca deleted_at em vez de remover o registro
    @Transactional
    public void deletar(UUID imobiliariaId, UUID id) {
        Locador locador = locadorRepository.findById(id)
                .filter(l -> l.getImobiliariaId().equals(imobiliariaId))
                .filter(l -> l.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Locador não encontrado"));

        locador.setDeletedAt(LocalDateTime.now());
        locadorRepository.save(locador);
    }
}
