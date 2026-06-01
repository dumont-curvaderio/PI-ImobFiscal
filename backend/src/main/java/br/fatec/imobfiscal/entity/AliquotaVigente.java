package br.fatec.imobfiscal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "aliquotas_vigentes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AliquotaVigente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Regime tributário do locador
    @Column(nullable = false)
    private String regime;

    // Tipo do imóvel (RESIDENCIAL, COMERCIAL, etc.)
    @Column(name = "tipo_imovel", nullable = false)
    private String tipoImovel;

    // IBS: Imposto sobre Bens e Serviços (estadual/municipal)
    @Column(name = "aliquota_ibs", nullable = false, precision = 6, scale = 4)
    private BigDecimal aliquotaIbs;

    // CBS: Contribuição sobre Bens e Serviços (federal)
    @Column(name = "aliquota_cbs", nullable = false, precision = 6, scale = 4)
    private BigDecimal aliquotaCbs;

    @Column(name = "ano_vigencia", nullable = false)
    private Integer anoVigencia;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
