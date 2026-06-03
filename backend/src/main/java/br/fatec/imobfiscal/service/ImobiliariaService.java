package br.fatec.imobfiscal.service;

import br.fatec.imobfiscal.model.Imobiliaria;
import br.fatec.imobfiscal.repository.ImobiliariaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImobiliariaService {

    private final ImobiliariaRepository repository;

    public Optional<Imobiliaria> buscarPorId(UUID id) {
        return repository.findByIdAndDeletedAtIsNull(id);
    }
}
