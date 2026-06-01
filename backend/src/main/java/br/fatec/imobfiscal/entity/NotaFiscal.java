package br.fatec.imobfiscal.entity;

import br.fatec.imobfiscal.enums.StatusNFe;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "notas_fiscais")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotaFiscal extends BaseEntity {

    @Column(name = "imobiliaria_id", nullable = false)
    private UUID imobiliariaId;

    // Relacionamento N:1 com ContratoLocacao
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", nullable = false)
    private ContratoLocacao contrato;

    // Número da NF — preenchido após emissão
    private String numero;

    private String serie;

    // Chave de acesso da SEFAZ (44 dígitos)
    @Column(name = "chave_acesso", unique = true)
    private String chaveAcesso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusNFe status = StatusNFe.AGUARDANDO;

    // ─── Valores fiscais ─────────────────────────────────────────────────────
    @Column(name = "valor_servico", nullable = false, precision = 15, scale = 2)
    @NotNull
    private BigDecimal valorServico;

    // IBS e CBS: informativos em 2026, recolhimento obrigatório a partir de 2027
    @Column(name = "valor_ibs", nullable = false, precision = 15, scale = 4)
    @Builder.Default
    private BigDecimal valorIbs = BigDecimal.ZERO;

    @Column(name = "valor_cbs", nullable = false, precision = 15, scale = 4)
    @Builder.Default
    private BigDecimal valorCbs = BigDecimal.ZERO;

    // false em 2026 (fase de testes), true a partir de 2027
    @Column(name = "recolhimento_obrigatorio", nullable = false)
    @Builder.Default
    private Boolean recolhimentoObrigatorio = false;

    // ─── Controle de retry SEFAZ ─────────────────────────────────────────────
    @Column(nullable = false)
    @Builder.Default
    private Integer tentativas = 0;

    @Column(name = "erro_sefaz")
    private String erroSefaz;
}
