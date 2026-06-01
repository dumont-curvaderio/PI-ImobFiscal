package br.fatec.imobfiscal.dto.locador;

import br.fatec.imobfiscal.entity.Locador;
import br.fatec.imobfiscal.enums.TipoPessoa;

import java.util.UUID;

// DTO de saída — nunca expõe campos internos desnecessários
public record LocadorResponse(
        UUID id,
        UUID imobiliariaId,
        TipoPessoa tipoPessoa,
        String cpfCnpj,
        String nome,
        String email,
        String telefone
) {
    // Método factory: converte a entidade JPA para o DTO de resposta
    public static LocadorResponse from(Locador locador) {
        return new LocadorResponse(
                locador.getId(),
                locador.getImobiliariaId(),
                locador.getTipoPessoa(),
                locador.getCpfCnpj(),
                locador.getNome(),
                locador.getEmail(),
                locador.getTelefone()
        );
    }
}
