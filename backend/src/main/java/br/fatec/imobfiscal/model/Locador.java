package br.fatec.imobfiscal.model;

import br.fatec.imobfiscal.enums.RegimeTributario;
import br.fatec.imobfiscal.enums.TipoPessoa;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

// Locador = proprietário do imóvel (pessoa física ou jurídica).
// Espelha a tabela locadores. A FK da imobiliária fica como UUID direto.
@Getter
@Setter
@NoArgsConstructor
public class Locador extends BaseModel {

    // Multi-tenancy (coluna imobiliaria_id)
    private UUID imobiliariaId;

    // Tipo de pessoa: PF | PJ
    private TipoPessoa tipoPessoa;

    // CPF (11 dígitos) ou CNPJ (14 dígitos)
    private String cpfCnpj;

    private String nome;
    private String email;
    private String telefone;

    // Regime tributário — usado pelo Motor Tributário para achar a alíquota
    private RegimeTributario regimeTributario;
}
