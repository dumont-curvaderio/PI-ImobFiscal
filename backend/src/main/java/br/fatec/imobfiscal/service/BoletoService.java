package br.fatec.imobfiscal.service;

import br.fatec.imobfiscal.enums.RegimeTributario;
import br.fatec.imobfiscal.model.Boleto;
import br.fatec.imobfiscal.model.ContratoLocacao;
import br.fatec.imobfiscal.model.Imovel;
import br.fatec.imobfiscal.model.Locador;
import br.fatec.imobfiscal.repository.BoletoRepository;
import br.fatec.imobfiscal.model.BoletoRequest;
import br.fatec.imobfiscal.model.CalculoRequest;
import br.fatec.imobfiscal.model.ResultadoCalculoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoletoService {

    private final BoletoRepository repository;
    private final ContratoService contratoService;
    private final ImovelService imovelService;
    private final LocadorService locadorService;
    private final MotorTributarioService motorTributarioService;

    public List<Boleto> listar(UUID imobiliariaId) {
        return repository.findByImobiliariaIdAndDeletedAtIsNullOrderByCreatedAtDesc(imobiliariaId);
    }

    public Boleto buscar(UUID imobiliariaId, UUID id) {
        return repository.findByIdAndImobiliariaIdAndDeletedAtIsNull(id, imobiliariaId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Boleto não encontrado"));
    }

    public Boleto gerar(UUID imobiliariaId, BoletoRequest request) {
        ContratoLocacao contrato = contratoService.buscar(imobiliariaId, request.contratoId());

        Imovel imovel = imovelService.buscar(imobiliariaId, contrato.getImovelId());
        String tipoImovel = imovel.getTipo().name();

        String regime = RegimeTributario.PF.name();
        if (imovel.getLocadorId() != null) {
            Locador locador = locadorService.buscar(imobiliariaId, imovel.getLocadorId());
            if (locador.getRegimeTributario() != null) {
                regime = locador.getRegimeTributario().name();
            }
        }

        ResultadoCalculoDTO resultado = motorTributarioService.calcular(
                new CalculoRequest(contrato.getValorAluguel(), regime, tipoImovel));

        Boleto boleto = new Boleto();
        boleto.setImobiliariaId(imobiliariaId);
        boleto.setContratoId(contrato.getId());
        boleto.setValorAluguel(resultado.valorBase());
        boleto.setAliquotaIbs(resultado.aliquotaIbs());
        boleto.setAliquotaCbs(resultado.aliquotaCbs());
        boleto.setValorIbs(resultado.valorIbs());
        boleto.setValorCbs(resultado.valorCbs());
        boleto.setValorLiquido(resultado.valorLiquido());
        boleto.setDataVencimento(request.dataVencimento());
        boleto.setStatus("GERADO");
        boleto.setRegimeTributario(regime);
        boleto.setTipoImovel(tipoImovel);
        return repository.save(boleto);
    }
}
