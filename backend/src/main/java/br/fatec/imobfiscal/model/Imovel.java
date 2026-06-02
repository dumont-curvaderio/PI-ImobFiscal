package br.fatec.imobfiscal.model;

import br.fatec.imobfiscal.enums.TipoImovel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

// Imóvel. Espelha a tabela imoveis.
// As FKs (imobiliaria_id, locador_id) ficam como UUID direto — não objetos.
@Getter
@Setter
@NoArgsConstructor
public class Imovel extends BaseModel {

    // Multi-tenancy (coluna imobiliaria_id)
    private UUID imobiliariaId;

    // Proprietário (coluna locador_id) — opcional no cadastro inicial
    private UUID locadorId;

    private String codigo;

    // Tipo: RESIDENCIAL | COMERCIAL | RURAL | MISTO
    private TipoImovel tipo;

    // ─── Endereço ────────────────────────────────────────────────────────────
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String uf;

    // ─── Dados físicos ───────────────────────────────────────────────────────
    private BigDecimal areaTotal;
    private Integer quartos;
    private Integer vagas;

    // ─── Dados fiscais (para GCAP — Ganho de Capital) ────────────────────────
    private BigDecimal valorCompra;
    private LocalDate dataCompra;

    // Valor de referência municipal — usado pelo Motor Tributário
    private BigDecimal valorVenal;
}
