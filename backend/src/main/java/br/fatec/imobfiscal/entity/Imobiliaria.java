package br.fatec.imobfiscal.entity;

import br.fatec.imobfiscal.enums.PlanoAssinatura;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "imobiliarias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Imobiliaria extends BaseEntity {

    @Column(nullable = false, unique = true, length = 14)
    @NotBlank
    private String cnpj;

    @Column(nullable = false)
    @NotBlank
    private String razao;

    @Column(name = "nome_fantasia")
    private String nomeFantasia;

    @Column(nullable = false)
    @Email
    @NotBlank
    private String email;

    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PlanoAssinatura plano = PlanoAssinatura.BASICO;

    // mappedBy = nome do campo em Imovel que referencia Imobiliaria
    @OneToMany(mappedBy = "imobiliaria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Imovel> imoveis;

    @OneToMany(mappedBy = "imobiliaria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ContratoLocacao> contratos;

    @OneToMany(mappedBy = "imobiliaria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Usuario> usuarios;
}
