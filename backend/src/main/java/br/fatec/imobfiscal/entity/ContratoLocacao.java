package br.fatec.imobfiscal.entity;

import br.fatec.imobfiscal.enums.StatusContrato;
import br.fatec.imobfiscal.enums.TipoLocacao;
import br.fatec.imobfiscal.enums.TipoPessoa;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "contratos_locacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContratoLocacao extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imobiliaria_id", nullable = false)
    private Imobiliaria imobiliaria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imovel_id", nullable = false)
    private Imovel imovel;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_locacao", nullable = false)
    @NotNull
    private TipoLocacao tipoLocacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusContrato status = StatusContrato.RASCUNHO;

    // ─── Dados do locatário ──────────────────────────────────────────────────
    @Enumerated(EnumType.STRING)
    @Column(name = "locatario_tipo", nullable = false)
    private TipoPessoa locatarioTipo;

    @Column(name = "locatario_cpf_cnpj", nullable = false, length = 14)
    @NotBlank
    private String locatarioCpfCnpj;

    @Column(name = "locatario_nome", nullable = false)
    @NotBlank
    private String locatarioNome;

    // ─── Dados financeiros ───────────────────────────────────────────────────
    @Column(name = "valor_aluguel", nullable = false, precision = 15, scale = 2)
    @NotNull
    private BigDecimal valorAluguel;

    @Column(name = "dia_vencimento", nullable = false)
    @NotNull
    private Integer diaVencimento;

    // ─── Vigência ────────────────────────────────────────────────────────────
    @Column(name = "data_inicio", nullable = false)
    @NotNull
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "prazo_meses")
    private Integer prazoMeses;

    // Um contrato gera várias notas fiscais (uma por mês)
    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NotaFiscal> notasFiscais;
}
