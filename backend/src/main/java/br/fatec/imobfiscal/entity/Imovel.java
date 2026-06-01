package br.fatec.imobfiscal.entity;

import br.fatec.imobfiscal.enums.TipoImovel;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "imoveis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Imovel extends BaseEntity {

    // Relacionamento N:1 com Imobiliaria
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imobiliaria_id", nullable = false)
    private Imobiliaria imobiliaria;

    // Relacionamento N:1 com Locador (proprietário) — opcional no cadastro inicial
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locador_id", nullable = true)
    private Locador locador;

    @Column(nullable = false)
    @NotBlank
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private TipoImovel tipo;

    // ─── Endereço ────────────────────────────────────────────────────────────
    @Column(nullable = false, length = 8)
    @Size(min = 8, max = 8)
    private String cep;

    @Column(nullable = false)
    @NotBlank
    private String logradouro;

    @Column(nullable = false)
    @NotBlank
    private String numero;

    private String complemento;

    @Column(nullable = false)
    @NotBlank
    private String bairro;

    @Column(nullable = false)
    @NotBlank
    private String cidade;

    @Column(nullable = false, length = 2)
    @Size(min = 2, max = 2)
    private String uf;

    // ─── Dados físicos ───────────────────────────────────────────────────────
    @Column(name = "area_total", precision = 12, scale = 2)
    private BigDecimal areaTotal;

    private Integer quartos;

    private Integer vagas;

    // ─── Dados fiscais (para GCAP — Ganho de Capital) ────────────────────────
    @Column(name = "valor_compra", precision = 15, scale = 2)
    private BigDecimal valorCompra;

    @Column(name = "data_compra")
    private LocalDate dataCompra;

    // Valor de referência municipal — usado pelo Motor Tributário para IBS/CBS
    @Column(name = "valor_venal", precision = 15, scale = 2)
    private BigDecimal valorVenal;

    // Um imóvel pode ter vários contratos (histórico)
    @OneToMany(mappedBy = "imovel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ContratoLocacao> contratos;
}
