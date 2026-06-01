package br.fatec.imobfiscal.entity;

import br.fatec.imobfiscal.enums.RegimeTributario;
import br.fatec.imobfiscal.enums.TipoPessoa;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.UUID;

// Locador = proprietário do imóvel (pessoa física ou jurídica)
@Entity
@Table(name = "locadores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Locador extends BaseEntity {

    // Toda entidade está ligada a uma imobiliária (multi-tenancy)
    @Column(name = "imobiliaria_id", nullable = false)
    private UUID imobiliariaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pessoa", nullable = false)
    private TipoPessoa tipoPessoa;

    // CPF (11 dígitos) ou CNPJ (14 dígitos)
    @Column(name = "cpf_cnpj", nullable = false, length = 14)
    @NotBlank
    @Size(min = 11, max = 14)
    private String cpfCnpj;

    @Column(nullable = false)
    @NotBlank
    private String nome;

    private String email;

    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(name = "regime_tributario")
    private RegimeTributario regimeTributario;

    // Um locador pode ter vários imóveis
    @OneToMany(mappedBy = "locador", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Imovel> imoveis;
}
