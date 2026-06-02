# Diagrama de Caso de Uso

**Sistema:** ImobFiscal — Gestão de Locação de Imóveis com Cálculo Tributário (IBS/CBS)
**Versão:** 1.0 — PI 2 / 2026

---

## Atores

| Ator | Tipo | Descrição |
| ---- | ---- | --------- |
| Locador | Principal | Proprietário do imóvel. Cadastra imóveis no sistema. |
| ClienteLocatário | Secundário | Inquilino. Consulta contrato e gera boleto. |
| AdmImobiliária | Secundário | Administrador da imobiliária. Gerencia contratos e negociações. |

---

## Casos de Uso

| Código | Nome | Ator(es) |
| ------ | ---- | -------- |
| UC01 | Cadastrar Imóvel | Locador |
| UC02 | Consultar Imóvel | ClienteLocatário |
| UC03 | Gerar Boleto | ClienteLocatário |
| UC04 | Cadastrar Dados do Cliente | AdmImobiliária |
| UC05 | Cadastrar Negociação | AdmImobiliária |
| UC06 | Analisar Documentação | AdmImobiliária |
| UC07 | Gerar Contrato | AdmImobiliária |
| UC08 | Solicitar Assinatura | AdmImobiliária |
| UC09 | Calcular IBS/CBS | Sistema (automático) |
| UC10 | Emitir Nota Fiscal | Sistema (automático) |

---

## Diagrama

```mermaid
graph TB
    %% ── Atores ──────────────────────────────────────────
    Locador(["👤 Locador\n(Proprietário)"])
    Locatario(["👤 ClienteLocatário"])
    Adm(["👤 AdmImobiliária"])

    %% ── Sistema ─────────────────────────────────────────
    subgraph Sistema ImobFiscal

        UC01("UC01\nCadastrar Imóvel")
        UC02("UC02\nConsultar Imóvel")
        UC03("UC03\nGerar Boleto")
        UC04("UC04\nCadastrar Dados\ndo Cliente")
        UC05("UC05\nCadastrar\nNegociação")
        UC06("UC06\nAnalisar\nDocumentação")
        UC07("UC07\nGerar Contrato")
        UC08("UC08\nSolicitar\nAssinatura")
        UC09("UC09\nCalcular IBS/CBS")
        UC10("UC10\nEmitir\nNota Fiscal")

    end

    %% ── Locador ─────────────────────────────────────────
    Locador --> UC01

    %% ── ClienteLocatário ────────────────────────────────
    Locatario --> UC02
    Locatario --> UC03

    %% ── AdmImobiliária ──────────────────────────────────
    Adm --> UC04
    Adm --> UC05
    Adm --> UC06
    Adm --> UC07
    Adm --> UC08

    %% ── Relacionamentos internos ─────────────────────────
    %% <<include>>: UC03 e UC07 sempre acionam o cálculo de impostos
    UC03 -. "<<include>>" .-> UC09
    UC07 -. "<<include>>" .-> UC09

    %% <<extend>>: o cálculo de impostos pode estender para emissão de NF
    UC09 -. "<<extend>>" .-> UC10
```

---

## Descrição dos Casos de Uso

### UC01 — Cadastrar Imóvel

- **Ator:** Locador
- **Pré-condição:** Locador autenticado no sistema.
- **Fluxo principal:**
  1. Locador acessa a tela de imóveis.
  2. Preenche endereço, tipo de uso (Residencial/Comercial) e valor venal.
  3. Sistema salva o imóvel vinculado ao Locador.
- **Pós-condição:** Imóvel disponível para ser associado a um contrato.

---

### UC03 — Gerar Boleto

- **Ator:** ClienteLocatário
- **Pré-condição:** Contrato de locação ativo.
- **Fluxo principal:**
  1. Locatário acessa o portal e seleciona o contrato.
  2. Solicita a geração do boleto do mês.
  3. Sistema **inclui** UC09 — calcula IBS/CBS sobre o valor do aluguel.
  4. Sistema gera o boleto com o valor líquido e emite a Nota Fiscal (UC10).
  5. Locatário recebe o boleto para pagamento.
- **Pós-condição:** Boleto emitido; Nota Fiscal gerada com alíquotas IBS/CBS.

---

### UC07 — Gerar Contrato

- **Ator:** AdmImobiliária
- **Pré-condição:** Dados do cliente e negociação cadastrados (UC04, UC05, UC06).
- **Fluxo principal:**
  1. AdmImobiliária acessa a negociação aprovada.
  2. Sistema gera o contrato com os dados do locador, imóvel e locatário.
  3. Sistema **inclui** UC09 — define as alíquotas IBS/CBS conforme o regime tributário.
  4. Contrato é gerado para assinatura (UC08).
- **Pós-condição:** Contrato pronto para coleta de assinatura.

---

### UC02 — Consultar Imóvel

- **Ator:** ClienteLocatário
- **Pré-condição:** ClienteLocatário autenticado no sistema e com contrato ativo.
- **Fluxo principal:**
  1. ClienteLocatário acessa a listagem de imóveis disponíveis.
  2. Sistema retorna os imóveis ativos da imobiliária (registros com `deletedAt` nulo).
  3. ClienteLocatário seleciona um imóvel para ver os detalhes.
  4. Sistema exibe as informações do imóvel: endereço, tipo, área, valor de compra e dados do locador.
- **Fluxo alternativo — imóvel não encontrado:**
  - Se o ID informado não existir ou pertencer a outra imobiliária, o sistema lança erro e retorna mensagem "Imóvel não encontrado".
- **Pós-condição:** ClienteLocatário visualiza os dados do imóvel sem alterá-los.

---

### UC04 — Cadastrar Dados do Cliente

- **Ator:** AdmImobiliária
- **Pré-condição:** AdmImobiliária autenticada no sistema.
- **Fluxo principal:**
  1. AdmImobiliária acessa a tela de locadores.
  2. Preenche os dados obrigatórios: tipo de pessoa (PF ou PJ), CPF/CNPJ, nome, e-mail, telefone e regime tributário.
  3. Sistema cria o registro do locador vinculado à imobiliária (`imobiliariaId`).
  4. Sistema retorna o locador criado com status HTTP 201.
- **Fluxo alternativo — dados inválidos:**
  - Se campos obrigatórios estiverem ausentes ou com formato inválido, o sistema rejeita a requisição com erro de validação (400).
- **Pós-condição:** Locador disponível para ser associado a um imóvel.

---

### UC05 — Cadastrar Negociação

- **Ator:** AdmImobiliária
- **Pré-condição:** AdmImobiliária autenticada; imóvel e locador já cadastrados.
- **Fluxo principal:**
  1. AdmImobiliária acessa a tela de contratos.
  2. Informa imóvel, tipo de locação, dados do locatário (nome, CPF/CNPJ, tipo de pessoa), valor do aluguel, dia de vencimento, datas de início e fim, e prazo em meses.
  3. Sistema cria o contrato com status inicial `RASCUNHO`.
  4. Sistema retorna o contrato criado com status HTTP 201.
- **Fluxo alternativo — imóvel de outra imobiliária:**
  - Se o `imovelId` não pertencer à imobiliária autenticada, o sistema lança erro e retorna mensagem "Imóvel não encontrado".
- **Pós-condição:** Contrato em `RASCUNHO` disponível para análise documental (UC06) e geração formal (UC07).

---

### UC06 — Analisar Documentação

- **Ator:** AdmImobiliária
- **Pré-condição:** Contrato em status `RASCUNHO` existente (UC05 concluído).
- **Fluxo principal:**
  1. AdmImobiliária revisa os documentos do locatário fora do sistema (processo manual).
  2. Após aprovação, AdmImobiliária acessa o contrato e solicita a atualização de status via `PATCH /{id}/status?status=ATIVO`.
  3. Sistema atualiza o campo `status` para `ATIVO` e persiste.
  4. Sistema retorna o contrato atualizado.
- **Fluxo alternativo — documentação reprovada:**
  - Se os documentos forem reprovados, o contrato permanece em `RASCUNHO` ou é movido para `RESCINDIDO`.
- **Pós-condição:** Contrato com status refletindo o resultado da análise, pronto para geração formal (UC07) se aprovado.

---

### UC08 — Solicitar Assinatura

- **Ator:** AdmImobiliária
- **Pré-condição:** Contrato gerado com status `ATIVO` (UC07 concluído).
- **Fluxo principal:**
  1. AdmImobiliária localiza o contrato na listagem.
  2. Aciona o processo de coleta de assinatura (comunicação externa ao sistema — e-mail ou presencial).
  3. Após assinatura coletada, AdmImobiliária registra a confirmação atualizando o status do contrato via `PATCH /{id}/status`.
  4. Sistema persiste a mudança e retorna o contrato atualizado.
- **Pós-condição:** Contrato assinado; locação vigente para geração de boletos (UC03).

---

### UC09 — Calcular IBS/CBS *(caso de uso interno)*

- **Ator:** Sistema (acionado automaticamente)
- **Descrição:** Aplica as alíquotas IBS (estadual/municipal) e CBS (federal) conforme a
  Reforma Tributária (LC 214/2025). Usa a `CalculadoraReformaTributaria` para retornar
  o `DetalhamentoTributario` com os valores separados.
- **Regras aplicadas:**
  - IBS: 0,1% (2026, recolhimento dispensado)
  - CBS: 0,9% (2026, recolhimento dispensado)
  - Fator de redução: 30% para residencial, 40% para short stay
  - A partir de 2027: CBS plena (8,8%), recolhimento obrigatório

---

### UC10 — Emitir Nota Fiscal *(caso de uso interno)*

- **Ator:** Sistema (acionado automaticamente após UC09)
- **Descrição:** Cria o registro de Nota Fiscal de Serviço com o detalhamento fiscal IBS/CBS calculado.
  A transmissão eletrônica para a SEFAZ é assíncrona; a nota é criada com status `AGUARDANDO`.
- **Fluxo principal:**
  1. Sistema recebe o `contratoId` e `valorServico` do solicitante.
  2. Sistema localiza o contrato e verifica que pertence à imobiliária (controle multi-tenant).
  3. Sistema cria a `NotaFiscal` com status inicial `AGUARDANDO` e persiste.
  4. Após processamento assíncrono pela SEFAZ, o status é atualizado via `PATCH /{id}/status`
     para `AUTORIZADA`, `REJEITADA` ou `CANCELADA`.
- **Fluxo alternativo — contrato inválido:**
  - Se o `contratoId` não existir ou não pertencer à imobiliária, o sistema lança erro "Contrato não encontrado".
- **Pós-condição:** Registro de Nota Fiscal criado com status `AGUARDANDO`; locador e locatário têm comprovante fiscal da operação de locação.
