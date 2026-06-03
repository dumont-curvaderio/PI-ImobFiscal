package br.fatec.imobfiscal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "boletos")
@Getter
@Setter
@NoArgsConstructor
public class Boleto extends BaseModel {

    @Column(name = "imobiliaria_id", nullable = false)
    private UUID imobiliariaId;

    @Column(name = "contrato_id", nullable = false)
    private UUID contratoId;

    @Column(name = "valor_aluguel", precision = 15, scale = 2)
    private BigDecimal valorAluguel;

    @Column(name = "aliquota_ibs", precision = 6, scale = 4)
    private BigDecimal aliquotaIbs;

    @Column(name = "aliquota_cbs", precision = 6, scale = 4)
    private BigDecimal aliquotaCbs;

    @Column(name = "valor_ibs", precision = 15, scale = 4)
    private BigDecimal valorIbs;

    @Column(name = "valor_cbs", precision = 15, scale = 4)
    private BigDecimal valorCbs;

    @Column(name = "valor_liquido", precision = 15, scale = 2)
    private BigDecimal valorLiquido;

    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    @Column(nullable = false, length = 20)
    private String status = "GERADO";

    @Column(name = "regime_tributario", length = 30)
    private String regimeTributario;

    @Column(name = "tipo_imovel", length = 20)
    private String tipoImovel;
}
