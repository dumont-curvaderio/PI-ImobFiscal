package br.fatec.imobfiscal.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

// UC-003: Boleto de aluguel com detalhamento fiscal (IBS/CBS/Split Payment).
// Simulado no PI. Espelha a tabela boletos.
// Estende BaseModel (id, createdAt, updatedAt, deletedAt). A FK do contrato
// fica como UUID direto (contratoId).
@Getter
@Setter
@NoArgsConstructor
public class Boleto extends BaseModel {

    // Multi-tenancy (coluna imobiliaria_id)
    private UUID imobiliariaId;

    // Contrato que originou este boleto (coluna contrato_id)
    private UUID contratoId;

    private BigDecimal valorAluguel;

    // Alíquotas registradas no momento da geração (imutável historicamente)
    private BigDecimal aliquotaIbs;
    private BigDecimal aliquotaCbs;

    // Valores calculados pelo Motor Tributário
    private BigDecimal valorIbs;
    private BigDecimal valorCbs;

    // Valor líquido: o que o locador realmente recebe
    private BigDecimal valorLiquido;

    private LocalDate dataVencimento;

    // GERADO → PAGO ou VENCIDO
    private String status = "GERADO";

    // Contexto fiscal registrado no momento (para auditoria)
    private String regimeTributario;
    private String tipoImovel;
}
