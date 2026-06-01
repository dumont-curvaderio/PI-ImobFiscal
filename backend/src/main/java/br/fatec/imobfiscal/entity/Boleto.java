package br.fatec.imobfiscal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

// UC-003: Boleto de aluguel com detalhamento fiscal (IBS/CBS/Split Payment)
// Simulado no PI — sem integração com gateway real
@Entity
@Table(name = "boletos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Boleto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "imobiliaria_id", nullable = false)
    private UUID imobiliariaId;

    // Contrato que originou este boleto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", nullable = false)
    private ContratoLocacao contrato;

    @Column(name = "valor_aluguel", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorAluguel;

    // Alíquotas registradas no momento da geração (imutável historicamente)
    @Column(name = "aliquota_ibs", nullable = false, precision = 6, scale = 4)
    private BigDecimal aliquotaIbs;

    @Column(name = "aliquota_cbs", nullable = false, precision = 6, scale = 4)
    private BigDecimal aliquotaCbs;

    // Valores calculados pelo Motor Tributário
    @Column(name = "valor_ibs", nullable = false, precision = 15, scale = 4)
    private BigDecimal valorIbs;

    @Column(name = "valor_cbs", nullable = false, precision = 15, scale = 4)
    private BigDecimal valorCbs;

    // Valor líquido: o que o locador realmente recebe
    @Column(name = "valor_liquido", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorLiquido;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    // GERADO → PAGO ou VENCIDO
    @Column(nullable = false)
    @Builder.Default
    private String status = "GERADO";

    // Contexto fiscal registrado no momento (para auditoria)
    @Column(name = "regime_tributario", nullable = false)
    private String regimeTributario;

    @Column(name = "tipo_imovel", nullable = false)
    private String tipoImovel;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
