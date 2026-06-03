package br.fatec.imobfiscal.model;

import br.fatec.imobfiscal.enums.RegimeTributario;
import br.fatec.imobfiscal.model.dao.BoletoDao;
import br.fatec.imobfiscal.model.dao.ContratoDao;
import br.fatec.imobfiscal.model.dao.ImovelDao;
import br.fatec.imobfiscal.model.dao.LocadorDao;
import br.fatec.imobfiscal.view.boleto.BoletoRequest;
import br.fatec.imobfiscal.view.motor.CalculoRequest;
import br.fatec.imobfiscal.view.motor.ResultadoCalculoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GeradorBoleto {

    private final ContratoDao contratoDao;
    private final ImovelDao imovelDao;
    private final LocadorDao locadorDao;
    private final MotorTributario motorTributario;
    private final BoletoDao boletoDao;

    public Boleto gerar(UUID imobiliariaId, BoletoRequest request) {
        ContratoLocacao contrato = contratoDao.buscar(imobiliariaId, request.contratoId());

        Imovel imovel = imovelDao.buscar(imobiliariaId, contrato.getImovelId());
        String tipoImovel = imovel.getTipo().name();

        String regime = RegimeTributario.PF.name();
        if (imovel.getLocadorId() != null) {
            Locador locador = locadorDao.buscar(imobiliariaId, imovel.getLocadorId());
            if (locador.getRegimeTributario() != null) {
                regime = locador.getRegimeTributario().name();
            }
        }

        ResultadoCalculoDTO resultado = motorTributario.calcular(
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

        return boletoDao.inserir(boleto);
    }
}
