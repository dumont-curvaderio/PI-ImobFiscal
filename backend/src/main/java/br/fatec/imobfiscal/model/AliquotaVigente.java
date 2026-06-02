package br.fatec.imobfiscal.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

// Alíquota vigente. Espelha a tabela aliquotas_vigentes.
// NÃO estende BaseModel: a tabela não tem updated_at/deleted_at, só created_at.
@Getter
@Setter
@NoArgsConstructor
public class AliquotaVigente {

    private UUID id;

    // Regime tributário do locador (PF, SIMPLES_NACIONAL, etc.)
    private String regime;

    // Tipo do imóvel (RESIDENCIAL, COMERCIAL, etc.)
    private String tipoImovel;

    // IBS: Imposto sobre Bens e Serviços (estadual/municipal)
    private BigDecimal aliquotaIbs;

    // CBS: Contribuição sobre Bens e Serviços (federal)
    private BigDecimal aliquotaCbs;

    private Integer anoVigencia;

    private LocalDateTime createdAt;
}
