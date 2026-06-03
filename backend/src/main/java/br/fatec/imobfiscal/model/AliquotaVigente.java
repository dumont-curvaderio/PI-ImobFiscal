package br.fatec.imobfiscal.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class AliquotaVigente {

    private UUID id;
    private String regime;
    private String tipoImovel;
    private BigDecimal aliquotaIbs;
    private BigDecimal aliquotaCbs;
    private Integer anoVigencia;
    private LocalDateTime createdAt;
}
