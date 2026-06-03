package br.fatec.imobfiscal.model;

import br.fatec.imobfiscal.enums.StatusNFe;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class NotaFiscal extends BaseModel {

    private UUID imobiliariaId;
    private UUID contratoId;
    private String numero;
    private String serie;
    private String chaveAcesso;
    private StatusNFe status = StatusNFe.AGUARDANDO;
    private BigDecimal valorServico;
    private BigDecimal valorIbs = BigDecimal.ZERO;
    private BigDecimal valorCbs = BigDecimal.ZERO;
    private Boolean recolhimentoObrigatorio = false;
    private Integer tentativas = 0;
    private String erroSefaz;
}
