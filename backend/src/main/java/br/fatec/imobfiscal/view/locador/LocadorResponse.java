package br.fatec.imobfiscal.view.locador;

import br.fatec.imobfiscal.enums.RegimeTributario;
import br.fatec.imobfiscal.enums.TipoPessoa;
import br.fatec.imobfiscal.model.Locador;

import java.util.UUID;

// DTO de saída — nunca expõe campos internos desnecessários.
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
    // Método factory: converte o model para o DTO de resposta.
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
