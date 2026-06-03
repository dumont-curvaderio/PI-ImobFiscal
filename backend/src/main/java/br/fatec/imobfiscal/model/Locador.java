package br.fatec.imobfiscal.model;

import br.fatec.imobfiscal.enums.RegimeTributario;
import br.fatec.imobfiscal.enums.TipoPessoa;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Locador extends BaseModel {

    private UUID imobiliariaId;
    private TipoPessoa tipoPessoa;
    private String cpfCnpj;
    private String nome;
    private String email;
    private String telefone;
    private RegimeTributario regimeTributario;
}
