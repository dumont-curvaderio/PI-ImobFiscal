# Guia de Preparação para a Banca — ImobFiscal

> Documento de revisão rápida para a defesa do PI 2 · FATEC DSM 2026-1.
> Formato: pergunta da banca → resposta curta e memorável.
> Leia junto com [01-frontend.md](01-frontend.md), [02-backend.md](02-backend.md) e [03-banco-de-dados.md](03-banco-de-dados.md).

---

## 1. Pitch de 60 segundos

Decore este parágrafo para abrir a apresentação:

> "O ImobFiscal é um sistema SaaS para imobiliárias que automatiza o cálculo
> e a emissão de boletos de aluguel com os novos tributos da Reforma Tributária
> brasileira — o IBS e o CBS, criados pela Lei Complementar 214 de 2025.
> O sistema gerencia imóveis, proprietários e contratos, e ao gerar um boleto
> aplica automaticamente as alíquotas vigentes, calcula o valor retido pelo
> Split Payment e registra o resultado para fins de auditoria fiscal.
> Foi construído com React no frontend, Spring Boot no backend e PostgreSQL
> como banco de dados, com deploy na Vercel e no Railway."

---

## 2. Perguntas de negócio

**Qual problema o ImobFiscal resolve?**

Imobiliárias precisam emitir boletos de aluguel com os novos tributos IBS e
CBS da Reforma Tributária. Calcular esses valores à mão é propenso a erro e
as alíquotas mudam todo ano até 2033. O ImobFiscal automatiza esse cálculo e
mantém o histórico imutável de cada boleto para auditoria fiscal.

---

**Quem é o usuário do sistema?**

O funcionário de uma imobiliária — gerente, operador financeiro ou
administrador. Cada imobiliária é um cliente independente (tenant) com seus
próprios imóveis, proprietários e contratos.

---

**Por que escolher a Reforma Tributária como tema?**

A LC 214/2025 é a maior mudança tributária do Brasil em décadas. Ela está em
vigor desde 2026, afeta todas as empresas que prestam serviços e ainda é pouco
conhecida na prática. Desenvolver um sistema que aplica essas regras é
relevante, atual e diferenciado para um PI acadêmico.

---

**O que é o IBS?**

IBS é o Imposto sobre Bens e Serviços. Ele substitui o ICMS (estadual) e o
ISS (municipal). Na locação de imóveis, incide sobre o valor do aluguel. A
alíquota varia conforme o regime tributário do proprietário e o tipo do imóvel.

---

**O que é o CBS?**

CBS é a Contribuição sobre Bens e Serviços. Ela substitui o PIS e o COFINS,
que eram tributos federais. Assim como o IBS, incide sobre o valor do aluguel
e é calculada na mesma operação.

---

**O que é Split Payment?**

É o mecanismo pelo qual o IBS e o CBS são retidos automaticamente no momento
do pagamento — o banco separa o valor do tributo antes de depositar o líquido
para o proprietário. O ImobFiscal calcula os valores e sinaliza
`splitPaymentRequerido = true` no boleto. Em 2026 os valores são informativos;
o recolhimento obrigatório começa em 2027.

---

**Por que isso é urgente em 2026?**

Porque 2026 é o primeiro ano da transição. As imobiliárias já precisam exibir
IBS e CBS nos documentos, mesmo que o recolhimento ainda não seja obrigatório.
Quem não se preparar agora vai correr em 2027.

---

## 3. Perguntas de arquitetura

**Por que separar frontend e backend?**

Separar responsabilidades. O frontend cuida da interface e da experiência do
usuário; o backend cuida das regras de negócio e da segurança dos dados. Com
essa separação, qualquer cliente — navegador, aplicativo mobile, integração
de terceiros — pode consumir a mesma API. Também facilita o deploy independente
de cada parte.

---

**O que é uma API REST?**

É um conjunto de endpoints HTTP com convenções padronizadas. Cada recurso tem
um endereço fixo (ex: `/api/imobiliarias/{id}/imoveis`), a ação é indicada
pelo verbo HTTP (`GET`, `POST`, `PUT`, `DELETE`) e os dados trafegam em JSON.
O servidor não guarda memória entre requisições — cada chamada carrega tudo
que precisa. Isso se chama arquitetura stateless.

---

**Qual é a estrutura interna do backend (MVC)?**

O backend segue o padrão MVC clássico com três papéis principais. O
**Controller** recebe a requisição HTTP, extrai os dados e chama o Model —
não tem lógica de negócio. O **Model** contém os POJOs (regras de negócio
como `MotorTributario` e `GeradorBoleto`) e os **DAOs** (`model/dao/`), que
executam o SQL puro diretamente via JdbcTemplate. A **View** são os DTOs que
definem o que entra e sai da API. Não há camada de Service separada — a
lógica de negócio fica nos próprios objetos do Model.

---

**O que é um DTO e por que usar?**

DTO (Data Transfer Object) é uma classe que define exatamente quais campos
entram e saem da API, separados da entidade do banco. Serve para três coisas:
segurança (impede que campos como `senha` ou `deletedAt` apareçam na resposta),
validação (as regras `@NotBlank`, `@Email` ficam no DTO de entrada) e
estabilidade (a entidade pode mudar internamente sem quebrar o contrato da API).

---

**O que é stateless e por que é relevante para APIs?**

Stateless significa que o servidor não guarda memória entre uma requisição e
a próxima — cada chamada carrega tudo o que precisa. Isso facilita escalar
horizontalmente: qualquer instância do servidor pode atender qualquer
requisição sem consultar um banco de sessões compartilhado. O ImobFiscal é
stateless: o backend não mantém sessão. O frontend reenvia o marcador de
sessão em cada chamada.

---

**O que é multi-tenancy?**

É quando um único sistema serve múltiplos clientes com dados completamente
isolados. No ImobFiscal, cada imobiliária é um tenant. Toda tabela tem a
coluna `imobiliaria_id`, e todas as consultas filtram por esse valor. A
imobiliária A nunca vê os dados da imobiliária B.

---

**Quais são as 3 camadas físicas do sistema?**

O cliente (SPA React rodando no navegador), a API REST (Spring Boot no Railway)
e o banco de dados (PostgreSQL no Railway). A SPA nunca acessa o banco
diretamente — toda comunicação passa pela API.

---

## 4. Perguntas de frontend (React)

**O que é uma SPA?**

SPA (Single Page Application) é uma aplicação web que carrega uma única página
HTML e, a partir daí, troca o conteúdo usando JavaScript — sem recarregar o
navegador a cada navegação. O ImobFiscal é uma SPA: o servidor entrega o
`index.html` uma vez e o React Router controla qual tela exibir conforme a URL.

---

**O que é um componente React?**

É uma função JavaScript que retorna o que deve ser exibido na tela. O React
compõe a interface como uma árvore de componentes — cada peça tem sua
responsabilidade. O `RotaPrivada`, por exemplo, é um componente com 12 linhas
que verifica se há usuário logado antes de renderizar a página protegida.

---

**O que é estado (`state`) e para que serve o `useState`?**

Estado é a memória interna de um componente. Quando o estado muda, o React
redesenha a tela automaticamente. O `useState` é o hook que cria esse estado —
ele retorna o valor atual e uma função para alterá-lo. Na `SimuladorFiscalPage`,
por exemplo, `valorBase`, `resultado` e `calculando` são estados que controlam
o formulário e o spinner.

---

**O que é um hook?**

Hook é qualquer função do React cujo nome começa com `use`. É a forma moderna
de adicionar estado, efeitos e contexto a componentes funcionais. O projeto usa
`useState`, `useEffect`, `useContext`, `useNavigate`, `useParams`,
`useCallback` e `useSearchParams`.

---

**Como o frontend protege rotas privadas?**

Com o componente `RotaPrivada`. Toda rota que exige login é envolvida por ele
no `App.jsx`. Se o estado `usuario` for `null` (sem login), o componente
redireciona para `/login`. Caso contrário, renderiza normalmente. Como a API
hoje é aberta (sem JWT), essa proteção é apenas de UX no frontend — o backend
não bloqueia requisições.

---

**Como o "token" de sessão fica guardado no frontend?**

No `localStorage` do navegador, com as chaves `imobfiscal_token` e
`imobfiscal_email`. (Esse "token" não é um JWT — é só um marcador de sessão; a
API não o valida.) O `localStorage` é persistente — sobrevive ao F5.
Ao recarregar a página, o `AuthContext` lê essas chaves e restaura o estado de
autenticação automaticamente.

---

**Como o frontend envia o token em todas as requisições?**

Via um interceptor do Axios, configurado no `api.js`. Antes de enviar qualquer
requisição, o interceptor lê o token do `localStorage` e injeta o cabeçalho
`Authorization: Bearer <token>`. Os componentes não precisam se preocupar com
isso — o interceptor faz automaticamente.

---

**Como o frontend se comunica com o backend?**

Através da biblioteca Axios, configurada com a `baseURL` apontando para o
backend. Em desenvolvimento, o Vite usa um proxy que redireciona chamadas
`/api` para `localhost:8080`, evitando bloqueio de CORS. Em produção, a
variável de ambiente `VITE_API_URL` aponta diretamente para o Railway.

---

**O que é o preview fiscal ao vivo no formulário de contrato?**

Ao preencher o valor do aluguel e sair do campo, a `ContratoFormPage` chama o
Motor Tributário e exibe em tempo real o IBS, o CBS e o valor líquido no painel
lateral. Usa `useCallback` para evitar que a função seja recriada a cada
renderização, e `onBlur` para disparar o cálculo só quando o usuário sai do
campo.

---

## 5. Perguntas de backend (Spring Boot)

**O que é Spring Boot?**

Spring Boot é um framework Java que elimina configurações repetitivas e deixa o
desenvolvedor focar na lógica de negócio. Ele configura automaticamente o
servidor HTTP, o acesso ao banco e a segurança. No ImobFiscal, usa a versão
3.3.0 com Java 17.

---

**Qual a diferença entre Controller, Service e Repository?**

O Controller recebe a requisição HTTP, extrai os dados e chama o Service — não
tem lógica de negócio. O Service aplica as regras ("imóvel não pode ser criado
sem imobiliária", "calcular IBS/CBS") — não sabe como os dados são salvos. O
Repository é uma interface que abstrai o banco — o Spring gera o SQL a partir
do nome do método. Cada camada tem uma responsabilidade única.

---

**O que é JdbcTemplate e como o ImobFiscal acessa o banco?**

O `JdbcTemplate` é uma classe do Spring que executa SQL escrito à mão de
forma segura (com parâmetros tipados, sem concatenação). No ImobFiscal não
há Hibernate nem JPA — cada DAO em `model/dao/` escreve explicitamente os
`SELECT`, `INSERT`, `UPDATE` que precisa. Os POJOs guardam as FKs como UUID;
o mapeamento entre linha e objeto é feito manualmente no DAO. Isso torna o
código mais direto e didático, sem a "mágica" do ORM.

---

**Como funciona o login no backend?**

O `AuthController` recebe email e senha. O DAO busca o usuário pelo email
no banco; se não encontrar, retorna 401. Se encontrar, usa a biblioteca
`spring-security-crypto` para comparar a senha informada com o hash BCrypt
armazenado. Se a senha bater, devolve um marcador de sessão (string simples)
junto com o email. Não há JWT nem Spring Security — a API é aberta e não
valida esse marcador em requisições subsequentes.

---

**Por que vocês não usam autenticação/JWT?**

Foi uma decisão consciente de escopo para o MVP acadêmico. A API só se
comunica com o próprio frontend, em um ambiente de demonstração controlado.
Implementar JWT + Spring Security acrescentaria complexidade (filtros, chaves
HMAC, contexto de segurança) sem benefício prático neste contexto. Em um
produto de produção, reintroduziríamos autenticação stateless com JWT e
restringiríamos todos os endpoints.

---

**Como o Motor Tributário calcula o imposto?**

A classe `MotorTributario` (em `model/`) busca na tabela `aliquotas_vigentes`
a linha correspondente ao regime do locador, ao tipo do imóvel e ao ano atual
— via JdbcTemplate no DAO correspondente. Com as alíquotas em mãos, multiplica
o valor base de cada uma e arredonda com 4 casas decimais usando
`RoundingMode.HALF_UP`. O valor líquido é o aluguel menos IBS menos CBS.
Se não existir alíquota cadastrada para aquela combinação, lança uma exceção.

---

**Por que as alíquotas ficam no banco e não no código?**

Porque as alíquotas da Reforma Tributária sobem todo ano até 2033. Se
estivessem no código, seria preciso alterar o sistema e fazer um novo deploy a
cada virada de ano. Com a tabela `aliquotas_vigentes`, basta inserir as linhas
do novo ano — sem tocar em nenhuma linha de Java.

---

**Por que o boleto congela os valores de alíquota?**

Porque auditoria fiscal exige rastreabilidade. O boleto guarda uma cópia
(snapshot) das alíquotas e dos valores calculados no momento em que foi
gerado. Mesmo que as alíquotas mudem amanhã, o boleto de hoje permanece
imutável. Isso atende o requisito legal de guarda de documentos fiscais.

---

**Como o schema do banco é criado?**

Manualmente, executando os scripts SQL na pasta `database/` na ordem:
`schema.sql` (cria as 6 tabelas base), `V2__motor_fiscal.sql` (adiciona
colunas, cria `aliquotas_vigentes` e `boletos`) e, em seguida, `seed.sql`
(dados de demonstração). Não há Hibernate nem criação automática por ORM —
o schema é inteiramente definido pelos scripts.

---

## 6. Perguntas de banco de dados

**Por que PostgreSQL?**

É um banco relacional robusto, open source e com excelente suporte a tipos de
dados avançados (UUID nativo, `NUMERIC` com precisão arbitrária para valores
fiscais, extensões como `pgcrypto`). É compatível com o Railway, a plataforma
de deploy escolhida.

---

**O que é chave primária (PK) e chave estrangeira (FK)?**

A PK identifica unicamente cada registro — nenhuma tabela pode ter duas linhas
com a mesma PK. A FK é uma coluna que aponta para a PK de outra tabela e
garante integridade referencial: o banco rejeita um imóvel cujo `locador_id`
não existe na tabela `locadores`.

---

**Por que UUID em vez de número sequencial?**

Com IDs sequenciais (1, 2, 3…), alguém poderia adivinhar o ID de outra
imobiliária na URL da API e tentar acessar dados alheios. UUID é gerado
aleatoriamente com 128 bits — computacionalmente inviável de adivinhar. É uma
camada extra de segurança por obscuridade no sistema multi-tenant.

---

**O que é soft delete e por que o ImobFiscal usa?**

Soft delete é exclusão lógica: em vez de apagar a linha do banco, o campo
`deleted_at` é preenchido com a data e hora da exclusão. A lei exige que
documentos fiscais (contratos, boletos, notas) sejam preservados por no mínimo
5 anos. Apagar fisicamente violaria essa obrigação. Todas as consultas normais
filtram `WHERE deleted_at IS NULL`.

---

**O que é uma migration e como o ImobFiscal gerencia o schema?**

Migration é um script SQL versionado que representa uma mudança no schema.
O ImobFiscal tem dois: `schema.sql` (V1, cria as 6 tabelas base) e
`V2__motor_fiscal.sql` (adiciona colunas, cria `aliquotas_vigentes` e `boletos`).
Esses scripts ficam em `database/` e servem como documentação do schema e como
ponto de partida para aplicação manual — por exemplo, rodar o seed no Railway.

O schema é criado executando esses scripts **manualmente**, na ordem, antes
de subir o backend. Não há Hibernate nem criação automática por ORM. O projeto
não usa Flyway. Para um sistema em produção real, o recomendado seria adotar
Flyway ou Liquibase, de modo que o schema só evolua por migrações revisadas —
nunca de forma manual e ad-hoc.

---

**Por que `aliquotas_vigentes` é uma tabela separada?**

Porque as alíquotas mudam a cada ano durante a transição 2026-2033 e variam
por regime tributário e tipo de imóvel. Guardá-las em banco permite atualizar
os valores sem alterar o código Java. A tabela tem 16 linhas para 2026 (4
regimes × 4 tipos de imóvel) e uma constraint `UNIQUE(regime, tipo_imovel,
ano_vigencia)` que impede duplicatas.

---

**Quantas tabelas tem o banco? Quais são elas?**

Oito tabelas: `imobiliarias`, `usuarios`, `locadores`, `imoveis`,
`contratos_locacao`, `notas_fiscais`, `aliquotas_vigentes` e `boletos`.
As seis primeiras foram criadas na migração V1; as duas últimas na V2 para o
módulo fiscal.

---

**Qual é o padrão de relacionamento entre as tabelas?**

Todos os relacionamentos são 1:N (um para muitos). A `imobiliarias` é a raiz:
todas as outras tabelas apontam para ela via `imobiliaria_id`. Um contrato
aponta para um imóvel; um imóvel aponta para um locador; boletos e notas
apontam para contratos. Não há relacionamento N:N — sem tabela de junção.

---

## 7. Perguntas de testes e qualidade

**Que testes vocês fizeram?**

Testes unitários no backend com JUnit 5 e Mockito, cobrindo duas classes:

- **`MotorTributarioTest`** — verifica o cálculo de IBS e CBS; testa que
  uma alíquota ausente lança exceção; e que alíquota zero resulta em isenção.
- **`ImovelDaoTest`** — verifica que o soft delete emite um `UPDATE` com
  `deleted_at`, nunca um `DELETE` físico.

São 5 testes no total, sem acesso a banco de dados real (`mvn test` verde).
No frontend não há testes automatizados — a validação foi feita manualmente.

---

**O que é um teste unitário?**

Verifica o comportamento de uma unidade isolada de código — normalmente um
método — sem depender de banco, rede ou outros sistemas. O objetivo é provar
que a lógica do código está correta, independentemente do ambiente.

---

**O que é um mock (objeto falso)?**

É um substituto de uma dependência real criado para o teste. No
`ImovelDaoTest`, o `JdbcTemplate` é um mock — ele simula as operações de banco
sem acessar o PostgreSQL de verdade. No `MotorTributarioTest`, o DAO de
alíquotas é mockado para retornar valores controlados. Isso torna o teste
rápido, previsível e sem efeitos colaterais.

---

**Por que testar especificamente o soft delete?**

Porque é a regra mais crítica do sistema do ponto de vista legal. O
`ImovelDaoTest` verifica que o método de exclusão emite um `UPDATE` com
`deleted_at = NOW()` E que nenhum `DELETE` físico é executado. Se alguém
modificar o DAO por engano e usar `DELETE` em vez de soft delete, o teste
quebra antes de chegar em produção — protegendo a conformidade fiscal.

---

**O que é o padrão Arrange-Act-Assert (AAA)?**

É a estrutura dos testes. "Arrange" prepara os dados e mocks. "Act" chama o
método sob teste. "Assert" verifica se o resultado foi o esperado. Todos os
5 testes do projeto seguem esse padrão.

---

## 8. Perguntas-armadilha — responda com honestidade técnica

> A banca valoriza quem conhece as limitações do próprio sistema.
> O padrão é: reconhecer a limitação + explicar o que faria para resolver.

---

**"E se eu passar na URL o ID de outra imobiliária?"**

Hoje a API é totalmente aberta — sem autenticação. Qualquer pessoa que
conheça a URL pode fazer requisições para qualquer `imobiliariaId`. É a
principal limitação de segurança do sistema. Para corrigir em produção,
introduziríamos JWT: o token carregaria o `imobiliariaId` do usuário, um
filtro o extrairia em cada requisição e compararia com o ID da URL —
rejeitando com 403 se não baterem.

---

**"Vocês emitem NF-e de verdade, integrados com a SEFAZ?"**

Não. A emissão de NF-e está simulada no MVP — o sistema cria registros na
tabela `notas_fiscais` com uma chave de acesso de 44 dígitos gerada
internamente, mas não faz requisições reais à SEFAZ. A entidade tem os campos
`tentativas` e `erroSefaz` preparados para uma integração futura com retry, mas
esse mecanismo não foi implementado. Para um produto de produção, seria
necessário integrar com um hub autorizado pela Receita Federal.

---

**"Por que tudo retorna erro 400?"**

O `GlobalExceptionHandler` trata todas as `RuntimeException` como HTTP 400
(Bad Request). Na prática, "imóvel não encontrado" deveria retornar 404 e erros
inesperados deveriam retornar 500. É uma limitação conhecida. Para corrigir,
criaríamos uma hierarquia de exceções customizadas —
`ImovelNotFoundException extends NotFoundException extends RuntimeException` —
e mapearíamos cada tipo para o código HTTP correto no handler.

---

**"O sistema está pronto para produção fiscal real?"**

Não. É um MVP acadêmico. As alíquotas são aproximações didáticas baseadas na
LC 214/2025, não os valores definitivos homologados. A API não tem
autenticação, não há integração real com SEFAZ, e o `imobiliariaId` está fixo
no frontend. Para produção seria necessário: alíquotas homologadas, integração
SEFAZ, JWT + Spring Security, multi-tenancy completo, índices de FK no banco,
RLS no PostgreSQL e testes de integração e de carga.

---

**"Por que o `IMOBILIARIA_ID` está fixo no frontend?"**

Para simplificar o MVP. Em produção, o ID da imobiliária deveria vir do
perfil retornado pelo login — decodificado de um JWT ou incluído diretamente
na resposta de autenticação. O `api.js` extrairia esse valor dinamicamente.
Hoje o sistema suporta tecnicamente múltiplos tenants no backend, mas o
frontend só opera com o tenant de demonstração.

---

**"Por que as FKs das tabelas não têm índice?"**

É uma limitação conhecida do banco. Apenas a tabela `boletos` tem índices
explícitos nas colunas mais consultadas. As colunas `locador_id`, `imovel_id`
e `contrato_id` das outras tabelas não têm índice — consultas que filtram por
elas fazem um full scan. Para corrigir, adicionaríamos uma migration V3 com
`CREATE INDEX` nessas colunas.

---

## 9. Divisão de apresentação (sugestão para grupos)

Sugestão genérica para grupos de 3 a 5 pessoas. Ajuste conforme quem
desenvolveu o quê:

| Membro | Bloco sugerido |
|---|---|
| 1 | Abertura com pitch + contexto da Reforma Tributária + demo do simulador fiscal |
| 2 | Arquitetura geral (diagrama de 3 camadas) + MVC + login/autenticação |
| 3 | Motor Tributário + `aliquotas_vigentes` + geração de boleto |
| 4 | Banco de dados: DER, soft delete, migrations, multi-tenancy |
| 5 | Frontend: SPA, React, proteção de rotas, preview ao vivo |

Cada membro defende as limitações da sua parte. Quem faz a abertura faz
também o fechamento respondendo perguntas gerais.

---

## 10. Checklist da véspera

- [ ] Rodar o backend localmente ou confirmar que o deploy no Railway está
      respondendo em `/api/health`.
- [ ] Rodar o frontend localmente ou acessar a URL da Vercel e fazer login com
      `admin@imobfiscal.com.br` / `admin123`.
- [ ] Confirmar que o seed está aplicado: dashboard deve mostrar 3 imóveis,
      1 contrato e 1 boleto.
- [ ] Abrir o simulador fiscal e fazer um cálculo ao vivo para a banca.
- [ ] Reler o pitch de 60 segundos da seção 1 em voz alta pelo menos 3 vezes.
- [ ] Revisar as perguntas-armadilha da seção 8 — a banca quase sempre faz
      pelo menos uma delas.
- [ ] Ter os arquivos [01-frontend.md](01-frontend.md),
      [02-backend.md](02-backend.md) e [03-banco-de-dados.md](03-banco-de-dados.md)
      abertos para consulta rápida.
- [ ] Saber de cor: 8 tabelas, MVC (Controller / Model+DAO / View-DTO), 3 camadas
      físicas, alíquota IBS de 1,45% e CBS de 0,76% para PF/RESIDENCIAL em 2026.

---

## 11. Glossário relâmpago

| Termo | O que é — em uma frase |
|---|---|
| **SPA** | Aplicação web que carrega um único HTML e troca o conteúdo com JavaScript sem recarregar a página |
| **React** | Biblioteca JavaScript para construir interfaces baseadas em componentes |
| **Componente** | Função JavaScript que retorna o que deve ser exibido na tela |
| **Hook** | Função do React (prefixo `use`) que adiciona estado, efeitos ou contexto a um componente |
| **Estado (state)** | Memória interna de um componente; quando muda, o React redesenha a tela |
| **`useEffect`** | Hook para executar código após a renderização: buscar dados, timers, eventos |
| **`useCallback`** | Hook que memoriza uma função para evitar recriá-la desnecessariamente |
| **Contexto** | Mecanismo React para compartilhar dados globais sem passar props em cascata |
| **Rota privada** | Rota que só renderiza se o usuário estiver autenticado (`RotaPrivada`) |
| **Interceptor** | Função que o Axios executa antes de cada requisição — injeta o marcador de sessão no cabeçalho `Authorization` |
| **Proxy (Vite)** | Redireciona chamadas `/api` para o backend em dev, evitando bloqueio de CORS |
| **CORS** | Política do navegador que bloqueia chamadas entre origens diferentes |
| **Rewrite (Vercel)** | Regra que serve `index.html` para qualquer URL, permitindo o roteamento da SPA |
| **API REST** | Conjunto de endpoints HTTP com verbos padronizados e dados em JSON |
| **Endpoint** | Endereço de uma operação na API (ex: `POST /api/imobiliarias/{id}/imoveis`) |
| **Controller** | Camada MVC que recebe a requisição HTTP e chama o Model — sem lógica de negócio |
| **Model** | Contém os POJOs com regras de negócio (`MotorTributario`, `GeradorBoleto`) e os DAOs |
| **DAO** | Data Access Object — classe que executa SQL puro via JdbcTemplate e mapeia resultados para POJOs |
| **JdbcTemplate** | Classe do Spring que executa SQL escrito à mão de forma segura (parâmetros tipados) |
| **DTO** | Objeto que define o que entra (Request) e o que sai (Response) da API — a camada View do MVC |
| **JWT** | Token compacto e assinado para autenticação stateless — **não usado** neste projeto (API aberta) |
| **BCrypt** | Algoritmo de hash unidirecional para armazenar senhas de forma segura — usado no login |
| **Stateless** | Servidor não guarda estado entre requisições |
| **Multi-tenancy** | Um sistema serve múltiplos clientes com dados isolados por `imobiliaria_id` |
| **Soft delete** | Preenche `deleted_at` em vez de apagar a linha — obrigatório por lei fiscal |
| **Migration** | Script SQL versionado que evolui o schema; neste projeto os scripts ficam em `database/` e são aplicados manualmente — sem ORM, sem Hibernate |
| **Seed** | Script que insere dados de demonstração para facilitar testes |
| **IBS** | Imposto sobre Bens e Serviços — substitui ICMS e ISS, arrecadado por estados e municípios |
| **CBS** | Contribuição sobre Bens e Serviços — substitui PIS e COFINS, arrecadada pela União |
| **Split Payment** | IBS e CBS retidos automaticamente no pagamento antes de depositar o líquido |
| **Motor Tributário** | Classe do Model (`MotorTributario`) que busca alíquotas no banco via DAO e calcula IBS, CBS e valor líquido |
| **`aliquotas_vigentes`** | Tabela com as alíquotas por regime, tipo de imóvel e ano — não está hardcoded no código |
| **Snapshot fiscal** | Cópia imutável das alíquotas e valores no boleto no momento da geração |
| **UUID** | Identificador único de 128 bits gerado aleatoriamente — impossível de adivinhar |
| **FK (Foreign Key)** | Coluna que referencia a PK de outra tabela, garantindo integridade referencial |
| **Mock** | Objeto falso que substitui uma dependência real em testes unitários |
| **AAA** | Padrão de teste: Arrange (prepara), Act (executa), Assert (verifica) |

---

*Verificado contra o código-fonte (backend MVC + SQL puro, sem JWT/Hibernate), 01-frontend.md e 03-banco-de-dados.md — PI 2 · FATEC DSM 2026-1. Última atualização: 2026-06-02.*
