package br.fatec.imobfiscal.model;

import br.fatec.imobfiscal.enums.TipoImovel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "imoveis")
@Getter
@Setter
@NoArgsConstructor
public class Imovel extends BaseModel {

    @Column(name = "imobiliaria_id", nullable = false)
    private UUID imobiliariaId;

    @Column(name = "locador_id")
    private UUID locadorId;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoImovel tipo;

    @Column(length = 8)
    private String cep;

    private String logradouro;

    private String numero;

    private String complemento;

    private String bairro;

    private String cidade;

    @Column(length = 2)
    private String uf;

    @Column(name = "area_total", precision = 10, scale = 2)
    private BigDecimal areaTotal;

    private Integer quartos;

    private Integer vagas;

    @Column(name = "valor_compra", precision = 15, scale = 2)
    private BigDecimal valorCompra;

    @Column(name = "data_compra")
    private LocalDate dataCompra;

    @Column(name = "valor_venal", precision = 15, scale = 2)
    private BigDecimal valorVenal;
}
