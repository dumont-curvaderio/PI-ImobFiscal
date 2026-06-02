package br.fatec.imobfiscal.model;

import br.fatec.imobfiscal.enums.PerfilUsuario;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

// Usuário autenticado. Espelha a tabela usuarios.
// REGRA-CHAVE do novo padrão: a FK fica como UUID direto (imobiliariaId),
// não como objeto Imobiliaria aninhado. Isso simplifica o RowMapper do DAO.
@Getter
@Setter
@NoArgsConstructor
public class Usuario extends BaseModel {

    // Multi-tenancy: id da imobiliária dona deste usuário (coluna imobiliaria_id)
    private UUID imobiliariaId;

    private String email;

    // Senha armazenada como hash BCrypt — nunca em texto puro
    private String senha;

    private String nome;

    // Perfil: ADMIN | GERENTE | OPERADOR | FINANCEIRO | READONLY
    private PerfilUsuario perfil = PerfilUsuario.OPERADOR;
}
