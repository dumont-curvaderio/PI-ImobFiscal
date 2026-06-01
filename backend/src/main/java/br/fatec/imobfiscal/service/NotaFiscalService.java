package br.fatec.imobfiscal.service;

import br.fatec.imobfiscal.dto.notafiscal.NotaFiscalRequest;
import br.fatec.imobfiscal.dto.notafiscal.NotaFiscalResponse;
import br.fatec.imobfiscal.entity.ContratoLocacao;
import br.fatec.imobfiscal.entity.NotaFiscal;
import br.fatec.imobfiscal.enums.StatusNFe;
import br.fatec.imobfiscal.repository.ContratoLocacaoRepository;
import br.fatec.imobfiscal.repository.NotaFiscalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotaFiscalService {

    private final NotaFiscalRepository notaFiscalRepository;
    private final ContratoLocacaoRepository contratoRepository;

    public List<NotaFiscalResponse> listar(UUID imobiliariaId) {
        return notaFiscalRepository
                .findAllByImobiliariaIdAndDeletedAtIsNull(imobiliariaId)
                .stream()
                .map(NotaFiscalResponse::from)
                .toList();
    }

    // Lista todas as notas fiscais de um contrato específico
    public List<NotaFiscalResponse> listarPorContrato(UUID imobiliariaId, UUID contratoId) {
        // Verifica se o contrato pertence à imobiliária antes de listar
        contratoRepository.findById(contratoId)
                .filter(c -> c.getImobiliaria().getId().equals(imobiliariaId))
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado"));

        return notaFiscalRepository.findAllByContratoId(contratoId)
                .stream()
                .map(NotaFiscalResponse::from)
                .toList();
    }

    public NotaFiscalResponse buscarPorId(UUID imobiliariaId, UUID id) {
        NotaFiscal nf = notaFiscalRepository.findById(id)
                .filter(n -> n.getImobiliariaId().equals(imobiliariaId))
                .filter(n -> n.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Nota fiscal não encontrada"));
        return NotaFiscalResponse.from(nf);
    }

    // Cria a nota no status AGUARDANDO — a transmissão para SEFAZ é assíncrona
    @Transactional
    public NotaFiscalResponse criar(UUID imobiliariaId, NotaFiscalRequest request) {
        ContratoLocacao contrato = contratoRepository.findById(request.contratoId())
                .filter(c -> c.getImobiliaria().getId().equals(imobiliariaId))
                .filter(c -> c.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado"));

        NotaFiscal nf = NotaFiscal.builder()
                .imobiliariaId(imobiliariaId)
                .contrato(contrato)
                .valorServico(request.valorServico())
                // IBS e CBS são informativos em 2026 (recolhimento obrigatório a partir de 2027)
                .build();

        return NotaFiscalResponse.from(notaFiscalRepository.save(nf));
    }

    // Atualiza apenas o status (ex: AGUARDANDO → AUTORIZADA após retorno da SEFAZ)
    @Transactional
    public NotaFiscalResponse atualizarStatus(UUID imobiliariaId, UUID id, StatusNFe novoStatus) {
        NotaFiscal nf = notaFiscalRepository.findById(id)
                .filter(n -> n.getImobiliariaId().equals(imobiliariaId))
                .filter(n -> n.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Nota fiscal não encontrada"));

        nf.setStatus(novoStatus);
        return NotaFiscalResponse.from(notaFiscalRepository.save(nf));
    }
}
