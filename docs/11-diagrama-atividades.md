# Fase 6 — Diagramas de Atividades UML

**Projeto:** ImobFiscal
**PI:** 2º Semestre DSM — FATEC Indaiatuba
**Data:** 2026-06-02

---

## 1. O que é um Diagrama de Atividades

O Diagrama de Atividades é um diagrama UML comportamental que representa o fluxo de
controle de um processo — uma sequência de passos, com pontos de decisão (losangos),
ramificações e estados terminais. Ele responde à pergunta: "o que acontece, passo a passo,
quando o usuário realiza esta ação?"

No ImobFiscal, os diagramas de atividades complementam os Casos de Uso (que mostram
**quem** faz **o quê**) com a sequência detalhada de passos e as condições que alteram o
caminho percorrido.

Convenções usadas nos diagramas abaixo:

- Nó inicial: círculo preenchido (`([Início])`).
- Nó final: círculo com borda dupla (`([Fim])`).
- Atividade: retângulo arredondado.
- Decisão: losango — sai com uma condição `[sim]` e outra `[não]`.

---

## 2. Diagrama A — Fluxo de Login e Autenticação

### Fluxo A — diagrama

```mermaid
flowchart TD
    A([Início]) --> B[Usuário acessa a tela de login]
    B --> C[Preenche e-mail e senha]
    C --> D[Clica em 'Entrar']
    D --> E[Frontend envia POST /api/auth/login]
    E --> F{Campos\nválidados no\nfrontend?}
    F -- Não --> G[Exibe mensagem de erro\nno formulário]
    G --> C
    F -- Sim --> H[Backend recebe credenciais]
    H --> I[AuthController busca\nusuário por e-mail no banco]
    I --> J{Usuário\nencontrado?}
    J -- Não --> K[Retorna HTTP 401\nUnauthorized]
    K --> L[Frontend exibe\n'Credenciais inválidas']
    L --> C
    J -- Sim --> M[BCrypt.matches:\nsenha digitada vs hash]
    M --> N{Senha\ncorreta?}
    N -- Não --> K
    N -- Sim --> O[Backend retorna\nHTTP 200 + dados do usuário]
    O --> P[Frontend armazena\ndados em localStorage]
    P --> Q[Redireciona para\nDashboard]
    Q --> R([Fim])
```

### Fluxo A — explicação

O processo começa quando o usuário acessa a tela de login e preenche o formulário. Há dois
pontos de decisão relevantes.

O primeiro ponto de decisão ocorre no frontend: se os campos estiverem vazios ou mal
formatados, o erro é apresentado imediatamente, sem enviar requisição ao servidor. Esse
comportamento reduz requisições desnecessárias e melhora a experiência do usuário.

O segundo e o terceiro pontos de decisão ocorrem no backend. O `AuthController` busca
diretamente no banco se o e-mail existe (via `UsuarioDao`). Se não existir, retorna HTTP
401. Se existir, o BCrypt compara o hash armazenado com o hash da senha digitada
(`BCryptPasswordEncoder.matches()`). Se não bater, também retorna 401. A mensagem de erro
do frontend é genérica em ambos os casos ("Credenciais inválidas") — isso é deliberado
para não revelar se o e-mail existe ou não no sistema.

A API é aberta: não há filtro JWT interceptando requisições. Somente quando os dois checks
passam o backend retorna HTTP 200 com os dados do usuário (sem token JWT). O frontend
armazena esses dados em `localStorage` para manter a sessão.

---

## 3. Diagrama B — Cadastro de Imóvel

### Fluxo B — diagrama

```mermaid
flowchart TD
    A([Início]) --> B[Usuário acessa\ntela de Imóveis]
    B --> C[Clica em 'Novo Imóvel']
    C --> D[Preenche código e CEP]
    D --> E[Frontend dispara\nautocomplete via ViaCEP API]
    E --> F{ViaCEP retornou\ndados do endereço?}
    F -- Sim --> G[Preenche logradouro,\nbairro, cidade, UF\nautomaticamente]
    F -- Não --> H[Usuário preenche\nendereço manualmente]
    G --> I[Usuário preenche\ncampos restantes:\ntipo, valor venal]
    H --> I
    I --> J{Seleciona\num Locador\nexistente?}
    J -- Sim --> K[Associa locador\nao imóvel]
    J -- Não --> L[Usuário cria\nnovo Locador]
    L --> M[Salva Locador\nPOST /api/.../locadores]
    M --> K
    K --> N[Usuário clica\nem 'Salvar']
    N --> O[Frontend valida\ncampos obrigatórios]
    O --> P{Formulário\nválidado?}
    P -- Não --> Q[Exibe erros\npor campo]
    Q --> I
    P -- Sim --> R[Envia POST\n/api/.../imoveis]
    R --> S[ImovelController chama\nImovelDao.criar]
    S --> T{Locador\nencontrado\nno banco?}
    T -- Não --> U[Retorna HTTP 400\n'Locador não encontrado']
    U --> V[Frontend exibe\nmensagem de erro]
    V --> I
    T -- Sim --> W[ImovelDao executa\nINSERT INTO imoveis via SQL]
    W --> X[Retorna HTTP 201\nImovelResponse]
    X --> Y[Frontend exibe\nsucesso e atualiza lista]
    Y --> Z([Fim])
```

### Fluxo B — explicação

O cadastro de imóvel tem três pontos de decisão que estruturam o processo.

O primeiro é o autocomplete de CEP: ao sair do campo CEP, o frontend consulta a ViaCEP
(serviço público gratuito) e preenche logradouro, bairro, cidade e UF automaticamente.
Se a API não responder ou o CEP não existir, o usuário preenche o endereço manualmente —
o fluxo não trava.

O segundo ponto de decisão é a associação ao Locador. O imóvel precisa estar vinculado a
um Locador existente (RN01). Se o locador ainda não está cadastrado, o usuário cria um
novo antes de continuar — o frontend oferece esse desvio sem precisar sair da tela de
imóveis.

O terceiro ponto de decisão está no backend. O `ImovelController` repassa a criação ao
`ImovelDao`, que verifica via SQL se o Locador existe (defesa em profundidade). Se não
existir, retorna HTTP 400. Só após essa validação o DAO executa o `INSERT INTO imoveis`
com `JdbcTemplate` e a resposta 201 é devolvida ao frontend.

---

## 4. Diagrama C — Geração de Boleto com Cálculo Fiscal

### Fluxo C — diagrama

```mermaid
flowchart TD
    A([Início]) --> B[Usuário acessa\nlistagem de Contratos]
    B --> C[Seleciona um\nContrato ativo]
    C --> D[Clica em\n'Gerar Boleto']
    D --> E[Informa data\nde vencimento]
    E --> F[Frontend envia\nPOST /api/.../boletos/gerar]
    F --> G[BoletoController chama\nGeradorBoleto.gerar]
    G --> H[GeradorBoleto busca\nContrato + Imóvel + Locador via DAO]
    H --> I[Extrai: valorAluguel,\nregime tributário,\ntipo do imóvel]
    I --> J[GeradorBoleto chama\nMotorTributario.calcular]
    J --> K[MotorTributario\nextrai ano atual\nLocalDate.now getYear]
    K --> L[Consulta aliquotas_vigentes\nWHERE regime = X\nAND tipo_imovel = Y\nAND ano_vigencia = 2026]
    L --> M{Alíquota\nencontrada\npara o ano?}
    M -- Não --> N[Lança RuntimeException\n'Alíquota não encontrada\npara regime/tipo/ano']
    N --> O[GlobalExceptionHandler\nretorna HTTP 400]
    O --> P[Frontend exibe\nmensagem de erro]
    P --> Q([Fim com erro])
    M -- Sim --> R[Calcula:\nvalorIbs = aluguel × aliquotaIbs\nvalorCbs = aluguel × aliquotaCbs\nvalorLiquido = aluguel − IBS − CBS]
    R --> S[Congela valores no Boleto:\naliquotaIbs, aliquotaCbs,\nvalorIbs, valorCbs,\nvalorLiquido — snapshot imutável]
    S --> T[BoletoDao executa INSERT\nBoleto com status GERADO]
    T --> U[Retorna HTTP 201\nBoletoResponse]
    U --> V[Frontend exibe\nresumo fiscal:\nR$ aluguel bruto\nR$ IBS deduzido\nR$ CBS deduzido\nR$ líquido ao locador]
    V --> W([Fim com sucesso])
```

### Fluxo C — explicação

O processo de geração de boleto é onde o Motor Tributário entra em cena. O fluxo envolve
três camadas do backend trabalhando em sequência.

Primeiro, o `BoletoController` aciona `GeradorBoleto.gerar()` (classe em `model/`), que
carrega o contexto completo via DAOs: o contrato selecionado, o imóvel associado a ele e
o locador dono do imóvel. Esses dados fornecem os três parâmetros necessários para o
cálculo: `valorAluguel`, regime tributário do locador e tipo do imóvel.

O ponto de decisão central é a consulta à tabela `aliquotas_vigentes`. O `MotorTributario`
(classe em `model/`) extrai o ano atual dinamicamente (`LocalDate.now().getYear()`) e
busca via SQL a linha que combina regime + tipo + ano. Se não existir essa combinação no
banco, o sistema lança uma exceção e retorna HTTP 400 com mensagem descritiva — isso pode
ocorrer se o banco não tiver sido populado com as alíquotas do ano em vigor.

Quando a alíquota é encontrada, o cálculo usa `BigDecimal` com arredondamento `HALF_UP`
(padrão fiscal brasileiro): 4 casas decimais para IBS e CBS, 2 casas para o valor líquido
final. Imediatamente após o cálculo, os valores são **congelados** no registro de boleto —
as alíquotas do momento da emissão são gravadas junto com os valores calculados. Isso
garante que, mesmo se as alíquotas mudarem no banco para o ano seguinte, o boleto já
emitido permanece com os dados fiscais do dia em que foi gerado, atendendo às exigências
de rastreabilidade fiscal da Receita Federal.

---

_Verificado contra: `docs/03-casos-de-uso.md`, código em `backend/` — Spring Boot 3.3.0, Java 17, JdbcTemplate (sem Hibernate/JPA, sem JWT)._
_Última atualização: 2026-06-02._
