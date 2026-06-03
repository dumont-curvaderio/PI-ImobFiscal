package br.fatec.imobfiscal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "aliquotas_vigentes")
@Getter
@Setter
@NoArgsConstructor
public class AliquotaVigente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false, length = 30)
    private String regime;

    @Column(name = "tipo_imovel", nullable = false, length = 20)
    private String tipoImovel;

    @Column(name = "aliquota_ibs", nullable = false, precision = 6, scale = 4)
    private BigDecimal aliquotaIbs;

    @Column(name = "aliquota_cbs", nullable = false, precision = 6, scale = 4)
    private BigDecimal aliquotaCbs;

    @Column(name = "ano_vigencia", nullable = false)
    private Integer anoVigencia;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
