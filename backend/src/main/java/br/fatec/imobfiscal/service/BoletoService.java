package br.fatec.imobfiscal.service;

import br.fatec.imobfiscal.dto.boleto.BoletoRequest;
import br.fatec.imobfiscal.dto.boleto.BoletoResponse;
import br.fatec.imobfiscal.dto.motor.CalculoRequest;
import br.fatec.imobfiscal.dto.motor.ResultadoCalculoDTO;
import br.fatec.imobfiscal.entity.Boleto;
import br.fatec.imobfiscal.entity.ContratoLocacao;
import br.fatec.imobfiscal.entity.Imovel;
import br.fatec.imobfiscal.entity.Locador;
import br.fatec.imobfiscal.enums.RegimeTributario;
import br.fatec.imobfiscal.repository.BoletoRepository;
import br.fatec.imobfiscal.repository.ContratoLocacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoletoService {

    private final BoletoRepository boletoRepository;
    private final ContratoLocacaoRepository contratoLocacaoRepository;
    private final MotorTributarioService motorTributarioService;

    public List<BoletoResponse> listar(UUID imobiliariaId) {
        return boletoRepository.findByImobiliariaIdAndDeletedAtIsNull(imobiliariaId)
                .stream()
                .map(BoletoResponse::from)
                .collect(Collectors.toList());
    }

    public BoletoResponse buscarPorId(UUID imobiliariaId, UUID id) {
        Boleto boleto = boletoRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Boleto não encontrado: " + id));
        return BoletoResponse.from(boleto);
    }

    public BoletoResponse gerar(UUID imobiliariaId, BoletoRequest request) {
        // Busca contrato com imóvel e locador carregados
        ContratoLocacao contrato = contratoLocacaoRepository.findById(request.contratoId())
                .orElseThrow(() -> new RuntimeException("Contrato não encontrado: " + request.contratoId()));

        Imovel imovel = contrato.getImovel();
        Locador locador = imovel.getLocador();

        // Regime tributário do locador (padrão PF se não configurado)
        String regime = locador.getRegimeTributario() != null
                ? locador.getRegimeTributario().name()
                : RegimeTributario.PF.name();

        String tipoImovel = imovel.getTipo().name();

        // Chama o Motor Tributário para calcular IBS/CBS
        ResultadoCalculoDTO resultado = motorTributarioService.calcular(
                new CalculoRequest(contrato.getValorAluguel(), regime, tipoImovel));

        // Persiste o boleto com o detalhamento fiscal
        Boleto boleto = Boleto.builder()
                .imobiliariaId(imobiliariaId)
                .contrato(contrato)
                .valorAluguel(resultado.valorBase())
                .aliquotaIbs(resultado.aliquotaIbs())
                .aliquotaCbs(resultado.aliquotaCbs())
                .valorIbs(resultado.valorIbs())
                .valorCbs(resultado.valorCbs())
                .valorLiquido(resultado.valorLiquido())
                .dataVencimento(request.dataVencimento())
                .status("GERADO")
                .regimeTributario(regime)
                .tipoImovel(tipoImovel)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return BoletoResponse.from(boletoRepository.save(boleto));
    }
}
