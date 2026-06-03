package br.fatec.imobfiscal.model;

import br.fatec.imobfiscal.enums.StatusNFe;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "notas_fiscais")
@Getter
@Setter
@NoArgsConstructor
public class NotaFiscal extends BaseModel {

    @Column(name = "imobiliaria_id", nullable = false, columnDefinition = "uuid")
    private UUID imobiliariaId;

    @Column(name = "contrato_id", columnDefinition = "uuid")
    private UUID contratoId;

    private String numero;

    private String serie;

    @Column(name = "chave_acesso")
    private String chaveAcesso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusNFe status = StatusNFe.AGUARDANDO;

    @Column(name = "valor_servico", precision = 15, scale = 2)
    private BigDecimal valorServico;

    @Column(name = "valor_ibs", precision = 15, scale = 4)
    private BigDecimal valorIbs = BigDecimal.ZERO;

    @Column(name = "valor_cbs", precision = 15, scale = 4)
    private BigDecimal valorCbs = BigDecimal.ZERO;

    @Column(name = "recolhimento_obrigatorio")
    private Boolean recolhimentoObrigatorio = false;

    private Integer tentativas = 0;

    @Column(name = "erro_sefaz", columnDefinition = "text")
    private String erroSefaz;
}
