package br.fatec.imobfiscal.config;

import br.fatec.imobfiscal.enums.PerfilUsuario;
import br.fatec.imobfiscal.model.AliquotaVigente;
import br.fatec.imobfiscal.model.Imobiliaria;
import br.fatec.imobfiscal.model.Usuario;
import br.fatec.imobfiscal.repository.AliquotaVigenteRepository;
import br.fatec.imobfiscal.repository.ImobiliariaRepository;
import br.fatec.imobfiscal.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    static final UUID IMOBILIARIA_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    private final AliquotaVigenteRepository aliquotaRepo;
    private final ImobiliariaRepository imobiliariaRepo;
    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedImobiliaria();
        seedAliquotas();
        seedAdmin();
    }

    private void seedImobiliaria() {
        if (imobiliariaRepo.findById(IMOBILIARIA_ID).isPresent()) return;

        Imobiliaria imob = new Imobiliaria();
        imob.setId(IMOBILIARIA_ID);
        imob.setCnpj("00000000000191");
        imob.setRazao("ImobFiscal Demo Ltda");
        imob.setNomeFantasia("ImobFiscal");
        imob.setEmail("contato@imobfiscal.com.br");
        imob.setTelefone("(11) 99999-9999");
        imobiliariaRepo.save(imob);
    }

    private void seedAliquotas() {
        if (aliquotaRepo.count() > 0) return;

        List<AliquotaVigente> aliquotas = List.of(
            aliquota("PF",               "RESIDENCIAL", "0.0145", "0.0076"),
            aliquota("PF",               "COMERCIAL",   "0.0290", "0.0153"),
            aliquota("SIMPLES_NACIONAL", "RESIDENCIAL", "0.0145", "0.0076"),
            aliquota("SIMPLES_NACIONAL", "COMERCIAL",   "0.0290", "0.0153"),
            aliquota("LUCRO_PRESUMIDO",  "RESIDENCIAL", "0.0200", "0.0100"),
            aliquota("LUCRO_PRESUMIDO",  "COMERCIAL",   "0.0400", "0.0200"),
            aliquota("LUCRO_REAL",       "RESIDENCIAL", "0.0250", "0.0125"),
            aliquota("LUCRO_REAL",       "COMERCIAL",   "0.0500", "0.0250")
        );

        aliquotaRepo.saveAll(aliquotas);
    }

    private void seedAdmin() {
        usuarioRepo.findByEmailAndDeletedAtIsNull("admin@imobfiscal.com.br")
            .ifPresentOrElse(
                admin -> {
                    admin.setSenha(passwordEncoder.encode("admin123"));
                    usuarioRepo.save(admin);
                },
                () -> {
                    Usuario admin = new Usuario();
                    admin.setImobiliariaId(IMOBILIARIA_ID);
                    admin.setEmail("admin@imobfiscal.com.br");
                    admin.setSenha(passwordEncoder.encode("admin123"));
                    admin.setNome("Admin ImobFiscal");
                    admin.setPerfil(PerfilUsuario.ADMIN);
                    usuarioRepo.save(admin);
                }
            );
    }

    private AliquotaVigente aliquota(String regime, String tipoImovel, String ibs, String cbs) {
        AliquotaVigente a = new AliquotaVigente();
        a.setRegime(regime);
        a.setTipoImovel(tipoImovel);
        a.setAliquotaIbs(new BigDecimal(ibs));
        a.setAliquotaCbs(new BigDecimal(cbs));
        a.setAnoVigencia(2026);
        a.setCreatedAt(LocalDateTime.now());
        return a;
    }
}
