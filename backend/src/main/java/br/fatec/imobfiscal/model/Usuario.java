package br.fatec.imobfiscal.model;

import br.fatec.imobfiscal.enums.PerfilUsuario;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Usuario extends BaseModel {

    private UUID imobiliariaId;
    private String email;
    private String senha;
    private String nome;
    private PerfilUsuario perfil = PerfilUsuario.OPERADOR;
}
