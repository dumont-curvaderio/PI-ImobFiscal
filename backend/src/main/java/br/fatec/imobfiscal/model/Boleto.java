package br.fatec.imobfiscal.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Boleto extends BaseModel {

    private UUID imobiliariaId;
    private UUID contratoId;
    private BigDecimal valorAluguel;
    private BigDecimal aliquotaIbs;
    private BigDecimal aliquotaCbs;
    private BigDecimal valorIbs;
    private BigDecimal valorCbs;
    private BigDecimal valorLiquido;
    private LocalDate dataVencimento;
    private String status = "GERADO";
    private String regimeTributario;
    private String tipoImovel;
}
