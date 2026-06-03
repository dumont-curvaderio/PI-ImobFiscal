package br.fatec.imobfiscal.model;

import br.fatec.imobfiscal.enums.TipoImovel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Imovel extends BaseModel {

    private UUID imobiliariaId;
    private UUID locadorId;
    private String codigo;
    private TipoImovel tipo;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;
    private BigDecimal areaTotal;
    private Integer quartos;
    private Integer vagas;
    private BigDecimal valorCompra;
    private LocalDate dataCompra;
    private BigDecimal valorVenal;
}
