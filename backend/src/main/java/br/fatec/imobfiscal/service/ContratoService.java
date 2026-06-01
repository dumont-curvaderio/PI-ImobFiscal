package br.fatec.imobfiscal.service;

import br.fatec.imobfiscal.dto.contrato.ContratoRequest;
import br.fatec.imobfiscal.dto.contrato.ContratoResponse;
import br.fatec.imobfiscal.entity.ContratoLocacao;
import br.fatec.imobfiscal.entity.Imobiliaria;
import br.fatec.imobfiscal.entity.Imovel;
import br.fatec.imobfiscal.enums.StatusContrato;
import br.fatec.imobfiscal.repository.ContratoLocacaoRepository;
import br.fatec.imobfiscal.repository.ImobiliariaRepository;
import br.fatec.imobfiscal.repository.ImovelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContratoService {

    private final ContratoLocacaoRepository contratoRepository;
    private final ImobiliariaRepository imobiliariaRepository;
    private final ImovelRepository imovelRepository;

    public List<ContratoResponse> listar(UUID imobiliariaId) {
        return contratoRepository
                .findAllByImobiliariaIdAndDeletedAtIsNull(imobiliariaId)
                .stream()
                .map(ContratoResponse::from)
                .toList();
    }

    public ContratoResponse buscarPorId(UUID imobiliariaId, UUID id) {
        ContratoLocacao contrato = contratoRepository.findById(id)
                .filter(c -> c.getImobiliaria().getId().equals(imobiliariaId))
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado"));
        return ContratoResponse.from(contrato);
    }

    @Transactional
    public ContratoResponse criar(UUID imobiliariaId, ContratoRequest request) {
        Imobiliaria imobiliaria = imobiliariaRepository.findById(imobiliariaId)
                .orElseThrow(() -> new RuntimeException("Imobiliária não encontrada"));

        Imovel imovel = imovelRepository.findById(request.imovelId())
                .filter(i -> i.getImobiliaria().getId().equals(imobiliariaId))
                .orElseThrow(() -> new RuntimeException("Imóvel não encontrado"));

        ContratoLocacao contrato = ContratoLocacao.builder()
                .imobiliaria(imobiliaria)
                .imovel(imovel)
                .tipoLocacao(request.tipoLocacao())
                .locatarioTipo(request.locatarioTipo())
                .locatarioCpfCnpj(request.locatarioCpfCnpj())
                .locatarioNome(request.locatarioNome())
                .valorAluguel(request.valorAluguel())
                .diaVencimento(request.diaVencimento())
                .dataInicio(request.dataInicio())
                .dataFim(request.dataFim())
                .prazoMeses(request.prazoMeses())
                .build();

        return ContratoResponse.from(contratoRepository.save(contrato));
    }

    @Transactional
    public ContratoResponse atualizarStatus(UUID imobiliariaId, UUID id, StatusContrato novoStatus) {
        ContratoLocacao contrato = contratoRepository.findById(id)
                .filter(c -> c.getImobiliaria().getId().equals(imobiliariaId))
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado"));

        contrato.setStatus(novoStatus);
        return ContratoResponse.from(contratoRepository.save(contrato));
    }

    // Soft delete
    @Transactional
    public void deletar(UUID imobiliariaId, UUID id) {
        ContratoLocacao contrato = contratoRepository.findById(id)
                .filter(c -> c.getImobiliaria().getId().equals(imobiliariaId))
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado"));

        contrato.setDeletedAt(LocalDateTime.now());
        contratoRepository.save(contrato);
    }
}
