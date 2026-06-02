package br.fatec.imobfiscal.model;

import br.fatec.imobfiscal.enums.PlanoAssinatura;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Model = a "M" do MVC. POJO simples que espelha a tabela imobiliarias.
// Sem anotações JPA: o mapeamento tabela↔objeto é feito no DAO (RowMapper).
@Getter
@Setter
@NoArgsConstructor
public class Imobiliaria extends BaseModel {

    private String cnpj;
    private String razao;
    private String nomeFantasia;
    private String email;
    private String telefone;

    // Plano de assinatura: BASICO | PROFISSIONAL | ENTERPRISE
    private PlanoAssinatura plano = PlanoAssinatura.BASICO;
}
