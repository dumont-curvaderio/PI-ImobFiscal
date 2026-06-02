package br.fatec.imobfiscal.model;

import br.fatec.imobfiscal.enums.StatusNFe;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

// Nota fiscal (simulada no PI). Espelha a tabela notas_fiscais.
// As FKs (imobiliaria_id, contrato_id) ficam como UUID direto.
@Getter
@Setter
@NoArgsConstructor
public class NotaFiscal extends BaseModel {

    // Multi-tenancy (coluna imobiliaria_id)
    private UUID imobiliariaId;

    // Contrato de origem (coluna contrato_id)
    private UUID contratoId;

    // Identificação na SEFAZ — preenchidos após emissão
    private String numero;
    private String serie;
    private String chaveAcesso;

    // Status: AGUARDANDO | PROCESSANDO | AUTORIZADA | REJEITADA | CANCELADA
    private StatusNFe status = StatusNFe.AGUARDANDO;

    // ─── Valores fiscais ─────────────────────────────────────────────────────
    private BigDecimal valorServico;

    // IBS e CBS: informativos em 2026, recolhimento obrigatório a partir de 2027
    private BigDecimal valorIbs = BigDecimal.ZERO;
    private BigDecimal valorCbs = BigDecimal.ZERO;

    // false em 2026 (fase de testes), true a partir de 2027
    private Boolean recolhimentoObrigatorio = false;

    // ─── Controle de retry SEFAZ ─────────────────────────────────────────────
    private Integer tentativas = 0;
    private String erroSefaz;
}
