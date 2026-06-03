package br.fatec.imobfiscal.model;

import br.fatec.imobfiscal.enums.RegimeTributario;
import br.fatec.imobfiscal.enums.TipoPessoa;

import java.util.UUID;

public record LocadorResponse(
        UUID id,
        UUID imobiliariaId,
        TipoPessoa tipoPessoa,
        String cpfCnpj,
        String nome,
        String email,
        String telefone,
        RegimeTributario regimeTributario
) {
    public static LocadorResponse from(Locador locador) {
        return new LocadorResponse(
                locador.getId(),
                locador.getImobiliariaId(),
                locador.getTipoPessoa(),
                locador.getCpfCnpj(),
                locador.getNome(),
                locador.getEmail(),
                locador.getTelefone(),
                locador.getRegimeTributario()
        );
    }
}
