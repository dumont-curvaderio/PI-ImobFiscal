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

@Getter
@Setter
@NoArgsConstructor
public class ContratoLocacao extends BaseModel {

    private UUID imobiliariaId;
    private UUID imovelId;
    private TipoLocacao tipoLocacao;
    private StatusContrato status = StatusContrato.RASCUNHO;
    private TipoPessoa locatarioTipo;
    private String locatarioCpfCnpj;
    private String locatarioNome;
    private BigDecimal valorAluguel;
    private Integer diaVencimento;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Integer prazoMeses;
}
