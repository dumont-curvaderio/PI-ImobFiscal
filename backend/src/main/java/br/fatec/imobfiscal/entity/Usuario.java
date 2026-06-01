package br.fatec.imobfiscal.entity;

import br.fatec.imobfiscal.enums.PerfilUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario extends BaseEntity {

    // Relacionamento N:1 — muitos usuários pertencem a uma imobiliária
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "imobiliaria_id", nullable = false)
    private Imobiliaria imobiliaria;

    @Column(nullable = false, unique = true)
    @Email
    @NotBlank
    private String email;

    // Senha armazenada como hash BCrypt — nunca em texto puro
    @Column(nullable = false)
    @NotBlank
    private String senha;

    @Column(nullable = false)
    @NotBlank
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PerfilUsuario perfil = PerfilUsuario.OPERADOR;
}
