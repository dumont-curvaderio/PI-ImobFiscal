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

// Gera o boleto de aluguel com detalhamento fiscal (UC-003).
// No padrão MVC sem service, esta lógica de negócio fica no model como @Component.
// Antes os relacionamentos eram objetos aninhados (contrato.getImovel().getLocador());
// agora seguimos a cadeia de UUIDs buscando cada peça em seu DAO.
@Component
@RequiredArgsConstructor
public class GeradorBoleto {

    private final ContratoDao contratoDao;
    private final ImovelDao imovelDao;
    private final LocadorDao locadorDao;
    private final MotorTributario motorTributario;
    private final BoletoDao boletoDao;

    public Boleto gerar(UUID imobiliariaId, BoletoRequest request) {
        // 1. Busca o contrato (já filtrado por imobiliária — multi-tenancy).
        ContratoLocacao contrato = contratoDao.buscar(imobiliariaId, request.contratoId());

        // 2. Busca o imóvel do contrato para descobrir o tipo (RESIDENCIAL, etc.).
        Imovel imovel = imovelDao.buscar(imobiliariaId, contrato.getImovelId());
        String tipoImovel = imovel.getTipo().name();

        // 3. Descobre o regime tributário do locador (padrão PF se não houver locador
        //    ou se o regime não estiver configurado).
        String regime = RegimeTributario.PF.name();
        if (imovel.getLocadorId() != null) {
            Locador locador = locadorDao.buscar(imobiliariaId, imovel.getLocadorId());
            if (locador.getRegimeTributario() != null) {
                regime = locador.getRegimeTributario().name();
            }
        }

        // 4. Chama o Motor Tributário para calcular IBS/CBS/valor líquido.
        ResultadoCalculoDTO resultado = motorTributario.calcular(
                new CalculoRequest(contrato.getValorAluguel(), regime, tipoImovel));

        // 5. Monta o boleto com o snapshot fiscal imutável e persiste.
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
