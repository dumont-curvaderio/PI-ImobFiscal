package br.fatec.imobfiscal.model;

import br.fatec.imobfiscal.enums.PlanoAssinatura;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "imobiliarias")
@Getter
@Setter
@NoArgsConstructor
public class Imobiliaria extends BaseModel {

    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    @Column(nullable = false)
    private String razao;

    @Column(name = "nome_fantasia")
    private String nomeFantasia;

    private String email;

    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanoAssinatura plano = PlanoAssinatura.BASICO;
}
