package br.fatec.imobfiscal.model;

import br.fatec.imobfiscal.enums.PerfilUsuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario extends BaseModel {

    @Column(name = "imobiliaria_id", nullable = false)
    private UUID imobiliariaId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PerfilUsuario perfil = PerfilUsuario.OPERADOR;
}
