package br.fatec.imobfiscal.model;

import br.fatec.imobfiscal.enums.StatusContrato;
import br.fatec.imobfiscal.enums.TipoLocacao;
import br.fatec.imobfiscal.enums.TipoPessoa;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "contratos_locacao")
@Getter
@Setter
@NoArgsConstructor
public class ContratoLocacao extends BaseModel {

    @Column(name = "imobiliaria_id", nullable = false, columnDefinition = "uuid")
    private UUID imobiliariaId;

    @Column(name = "imovel_id", nullable = false, columnDefinition = "uuid")
    private UUID imovelId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_locacao", length = 30)
    private TipoLocacao tipoLocacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusContrato status = StatusContrato.RASCUNHO;

    @Enumerated(EnumType.STRING)
    @Column(name = "locatario_tipo", length = 2)
    private TipoPessoa locatarioTipo;

    @Column(name = "locatario_cpf_cnpj", length = 14)
    private String locatarioCpfCnpj;

    @Column(name = "locatario_nome")
    private String locatarioNome;

    @Column(name = "valor_aluguel", precision = 15, scale = 2)
    private BigDecimal valorAluguel;

    @Column(name = "dia_vencimento")
    private Integer diaVencimento;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "prazo_meses")
    private Integer prazoMeses;
}
