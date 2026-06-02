package br.fatec.imobfiscal.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

// Classe base do padrão MVC: campos comuns que todo model de domínio possui.
// Antes isto era um @MappedSuperclass do JPA. Agora é só um POJO simples —
// sem nenhuma anotação de banco. O DAO é quem preenche estes campos no SQL.
@Getter
@Setter
public abstract class BaseModel {

    // Identificador único (gerado em Java com UUID.randomUUID() no INSERT)
    private UUID id;

    // Data de criação do registro
    private LocalDateTime createdAt;

    // Data da última atualização
    private LocalDateTime updatedAt;

    // Soft delete: ao invés de apagar o registro, preenchemos esta data.
    // Regra do projeto: NUNCA hard delete de dados com histórico fiscal.
    private LocalDateTime deletedAt;
}
