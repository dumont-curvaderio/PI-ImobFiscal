package br.fatec.imobfiscal.model;

import br.fatec.imobfiscal.enums.StatusContrato;
import br.fatec.imobfiscal.enums.TipoLocacao;
import br.fatec.imobfiscal.enums.TipoPessoa;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

// Contrato de locação. Espelha a tabela contratos_locacao.
// As FKs (imobiliaria_id, imovel_id) ficam como UUID direto.
@Getter
@Setter
@NoArgsConstructor
public class ContratoLocacao extends BaseModel {

    // Multi-tenancy (coluna imobiliaria_id)
    private UUID imobiliariaId;

    // Imóvel objeto do contrato (coluna imovel_id)
    private UUID imovelId;

    // Tipo: RESIDENCIAL_LONGA | COMERCIAL | SHORT_STAY | RURAL
    private TipoLocacao tipoLocacao;

    // Status: RASCUNHO | ATIVO | RESCINDIDO | ENCERRADO
    private StatusContrato status = StatusContrato.RASCUNHO;

    // ─── Dados do locatário ──────────────────────────────────────────────────
    private TipoPessoa locatarioTipo;
    private String locatarioCpfCnpj;
    private String locatarioNome;

    // ─── Dados financeiros ───────────────────────────────────────────────────
    private BigDecimal valorAluguel;
    private Integer diaVencimento;

    // ─── Vigência ────────────────────────────────────────────────────────────
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Integer prazoMeses;
}
