package br.fatec.imobfiscal.service;

// ─── JUnit 5 ─────────────────────────────────────────────────────────────────
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

// ─── Mockito ──────────────────────────────────────────────────────────────────
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// ─── Projeto ──────────────────────────────────────────────────────────────────
import br.fatec.imobfiscal.dto.imovel.ImovelResponse;
import br.fatec.imobfiscal.entity.Imobiliaria;
import br.fatec.imobfiscal.entity.Imovel;
import br.fatec.imobfiscal.entity.Locador;
import br.fatec.imobfiscal.enums.TipoImovel;
import br.fatec.imobfiscal.repository.ImovelRepository;
import br.fatec.imobfiscal.repository.ImobiliariaRepository;
import br.fatec.imobfiscal.repository.LocadorRepository;

// ─── Java ─────────────────────────────────────────────────────────────────────
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// ─── Assertions e verificações ────────────────────────────────────────────────
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do ImovelService.
 *
 * Estratégia: usamos Mockito para simular o banco de dados (repositórios).
 * Isso permite testar apenas a lógica do Service, sem precisar de um banco real.
 *
 * @see ImovelService
 */
@ExtendWith(MockitoExtension.class) // Ativa o Mockito para esta classe de teste
class ImovelServiceTest {

    // ──────────────────────────────────────────────────────────────────────────
    // Mocks: objetos "falsos" que simulam o comportamento dos repositórios
    // O Mockito cria versões simuladas dessas interfaces automaticamente
    // ──────────────────────────────────────────────────────────────────────────

    @Mock
    private ImovelRepository imovelRepository;

    @Mock
    private ImobiliariaRepository imobiliariaRepository;

    @Mock
    private LocadorRepository locadorRepository;

    // @InjectMocks: o Mockito cria o ImovelService e injeta os mocks acima
    // como se fossem as dependências reais (@RequiredArgsConstructor do Lombok)
    @InjectMocks
    private ImovelService imovelService;

    // ──────────────────────────────────────────────────────────────────────────
    // TESTE 1 — Listagem
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar lista de imóveis ativos da imobiliária")
    void deveListarImoveisDaImobiliaria() {
        // ── Arrange (preparação) ──────────────────────────────────────────────
        // Criamos um UUID fixo que representa a imobiliária dona dos imóveis
        UUID imobiliariaId = UUID.randomUUID();

        // Montamos dois imóveis fictícios que o repositório vai "devolver"
        Imovel imovel1 = criarImovelFake(imobiliariaId, "AP-001");
        Imovel imovel2 = criarImovelFake(imobiliariaId, "AP-002");

        // Ensinamos o mock: "quando alguém chamar findAllByImobiliariaIdAndDeletedAtIsNull
        // com esse UUID, devolva essa lista"
        when(imovelRepository.findAllByImobiliariaIdAndDeletedAtIsNull(imobiliariaId))
                .thenReturn(List.of(imovel1, imovel2));

        // ── Act (ação) ────────────────────────────────────────────────────────
        // Chamamos o método que queremos testar
        List<ImovelResponse> resultado = imovelService.listar(imobiliariaId);

        // ── Assert (verificação) ──────────────────────────────────────────────
        assertAll(
                // Verifica que a lista retornada não é nula
                () -> assertNotNull(resultado),
                // Verifica que a lista tem exatamente 2 imóveis (os que montamos)
                () -> assertEquals(2, resultado.size()),
                // Verifica que o código do primeiro imóvel foi mapeado corretamente
                () -> assertEquals("AP-001", resultado.get(0).codigo())
        );

        // Verifica que o repositório foi chamado UMA vez com o UUID correto
        // Isso garante que o Service não está ignorando o filtro por imobiliária
        verify(imovelRepository, times(1))
                .findAllByImobiliariaIdAndDeletedAtIsNull(imobiliariaId);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TESTE 2 — Busca por ID (caminho feliz)
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve retornar o imóvel quando ele existe e pertence à imobiliária")
    void deveBuscarImovelPorId_quandoExiste() {
        // ── Arrange ───────────────────────────────────────────────────────────
        UUID imobiliariaId = UUID.randomUUID();
        UUID imovelId      = UUID.randomUUID();

        // Criamos um imóvel cujo campo imobiliaria.getId() retorna o mesmo UUID
        Imovel imovel = criarImovelFake(imobiliariaId, "CS-001");

        // Definimos o ID do imóvel manualmente para poder comparar depois
        imovel.setId(imovelId);

        // Mock: findById devolve o imóvel embrulhado em Optional.of(...)
        when(imovelRepository.findById(imovelId))
                .thenReturn(Optional.of(imovel));

        // ── Act ───────────────────────────────────────────────────────────────
        ImovelResponse resultado = imovelService.buscarPorId(imobiliariaId, imovelId);

        // ── Assert ────────────────────────────────────────────────────────────
        assertAll(
                // Verifica que o DTO não é nulo
                () -> assertNotNull(resultado),
                // Verifica que o código retornado é o do imóvel que montamos
                () -> assertEquals("CS-001", resultado.codigo()),
                // Verifica que o ID no DTO bate com o UUID que passamos
                () -> assertEquals(imovelId, resultado.id())
        );

        // Verifica que o repositório foi consultado com o UUID correto do imóvel
        verify(imovelRepository, times(1)).findById(imovelId);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TESTE 3 — Busca por ID (imóvel não encontrado)
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve lançar RuntimeException quando o imóvel não for encontrado")
    void deveLancarExcecao_quandoImovelNaoEncontrado() {
        // ── Arrange ───────────────────────────────────────────────────────────
        UUID imobiliariaId = UUID.randomUUID();
        UUID idInexistente = UUID.randomUUID(); // ID que não existe no banco

        // Mock: findById devolve Optional vazio — simula "não existe no banco"
        when(imovelRepository.findById(idInexistente))
                .thenReturn(Optional.empty());

        // ── Act + Assert (combinados porque o Act deve lançar exceção) ─────────
        // assertThrows verifica que a exceção correta é lançada
        RuntimeException excecao = assertThrows(
                RuntimeException.class,
                () -> imovelService.buscarPorId(imobiliariaId, idInexistente)
        );

        // Verifica que a mensagem da exceção é exatamente a definida no Service
        assertEquals("Imóvel não encontrado", excecao.getMessage());

        // Verifica que o repositório foi consultado (o Service não falhou antes)
        verify(imovelRepository, times(1)).findById(idInexistente);
    }

    // ──────────────────────────────────────────────────────────────────────────
    // TESTE 4 — Soft delete
    // ──────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Deve marcar deletedAt no imóvel (soft delete) e salvar sem excluir fisicamente")
    void deveDeletarImovelComSoftDelete() {
        // ── Arrange ───────────────────────────────────────────────────────────
        UUID imobiliariaId = UUID.randomUUID();
        UUID imovelId      = UUID.randomUUID();

        // Criamos um imóvel ativo (deletedAt == null)
        Imovel imovel = criarImovelFake(imobiliariaId, "GR-001");
        imovel.setId(imovelId);

        // Garantimos que o imóvel está ativo antes do delete
        assertNotNull(imovel.getId()); // sanity check do Arrange

        // Mock: findById encontra o imóvel
        when(imovelRepository.findById(imovelId))
                .thenReturn(Optional.of(imovel));

        // Mock: save devolve o próprio imóvel (comportamento padrão do JPA)
        when(imovelRepository.save(imovel))
                .thenReturn(imovel);

        // ── Act ───────────────────────────────────────────────────────────────
        imovelService.deletar(imobiliariaId, imovelId);

        // ── Assert ────────────────────────────────────────────────────────────
        // Verifica que deletedAt foi preenchido — isso é o soft delete
        assertNotNull(imovel.getDeletedAt(),
                "deletedAt deve ser preenchido após deletar — soft delete obrigatório (regra fiscal)");

        // Verifica que save() foi chamado (persiste o deletedAt no banco)
        verify(imovelRepository, times(1)).save(imovel);

        // CRÍTICO: verifica que deleteById NUNCA foi chamado
        // Hard delete é proibido — dados fiscais têm guarda mínima de 5 anos
        verify(imovelRepository, never()).deleteById(any());
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Método auxiliar — cria um Imovel com dados mínimos para os testes
    // Evita repetição de código (princípio DRY)
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Cria um imóvel de teste com dados fictícios mas válidos.
     *
     * @param imobiliariaId UUID da imobiliária proprietária
     * @param codigo        código do imóvel (ex: "AP-001")
     * @return entidade Imovel pronta para uso nos testes
     */
    private Imovel criarImovelFake(UUID imobiliariaId, String codigo) {
        // Criamos uma Imobiliaria fake com o ID que precisamos filtrar
        Imobiliaria imobiliaria = new Imobiliaria();
        imobiliaria.setId(imobiliariaId);

        // Criamos um Locador fake (proprietário do imóvel)
        Locador locador = new Locador();
        locador.setId(UUID.randomUUID());

        // Usamos o builder gerado pelo @Builder do Lombok
        return Imovel.builder()
                .imobiliaria(imobiliaria)
                .locador(locador)
                .codigo(codigo)
                .tipo(TipoImovel.RESIDENCIAL)
                .cep("01310100")
                .logradouro("Av. Paulista")
                .numero("1000")
                .bairro("Bela Vista")
                .cidade("São Paulo")
                .uf("SP")
                .areaTotal(new BigDecimal("80.00"))
                .quartos(2)
                .vagas(1)
                .valorCompra(new BigDecimal("350000.00"))
                .dataCompra(LocalDate.of(2020, 1, 15))
                .build();
    }
}
