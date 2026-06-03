package br.fatec.imobfiscal.model;

import br.fatec.imobfiscal.enums.RegimeTributario;
import br.fatec.imobfiscal.enums.TipoPessoa;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "locadores")
@Getter
@Setter
@NoArgsConstructor
public class Locador extends BaseModel {

    @Column(name = "imobiliaria_id", nullable = false)
    private UUID imobiliariaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pessoa", length = 2)
    private TipoPessoa tipoPessoa;

    @Column(name = "cpf_cnpj", length = 14)
    private String cpfCnpj;

    @Column(nullable = false)
    private String nome;

    private String email;

    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(name = "regime_tributario", length = 30)
    private RegimeTributario regimeTributario;
}
