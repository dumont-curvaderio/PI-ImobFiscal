package br.fatec.imobfiscal.model;

import br.fatec.imobfiscal.enums.PlanoAssinatura;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Imobiliaria extends BaseModel {

    private String cnpj;
    private String razao;
    private String nomeFantasia;
    private String email;
    private String telefone;
    private PlanoAssinatura plano = PlanoAssinatura.BASICO;
}
