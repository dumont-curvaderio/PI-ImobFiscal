# Fase 3 — Dicionário de Dados

**Projeto:** ImobFiscal  
**PI:** 2º Semestre DSM — FATEC Indaiatuba  
**Data:** 2026-06-01

> **Nota de versão:** este dicionário reflete `database/schema.sql` (schema base)
> e `database/V2__motor_fiscal.sql` (Motor Tributário + boletos). Chaves primárias
> são `UUID` gerado por `gen_random_uuid()` — não BIGSERIAL.

---

## Tabela: `imobiliarias`

Tenant raiz do sistema. Todas as demais tabelas referenciam `imobiliaria_id`.

| Coluna | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `id` | UUID | Sim | Identificador único, gerado automaticamente (`gen_random_uuid()`) |
| `cnpj` | VARCHAR(14) | Sim | CNPJ da imobiliária — único no sistema |
| `razao` | VARCHAR(255) | Sim | Razão social |
| `nome_fantasia` | VARCHAR(255) | Não | Nome fantasia |
| `email` | VARCHAR(255) | Sim | E-mail de contato |
| `telefone` | VARCHAR(255) | Não | Telefone de contato |
| `plano` | VARCHAR(20) | Sim | Plano de assinatura: `BASICO`, `PROFISSIONAL` ou `ENTERPRISE`. Padrão: `BASICO` |
| `created_at` | TIMESTAMP | Sim | Data e hora de criação do registro |
| `updated_at` | TIMESTAMP | Sim | Data e hora da última atualização |
| `deleted_at` | TIMESTAMP | Não | Preenchido na exclusão lógica. Nulo = ativo |

---

## Tabela: `usuarios`

| Coluna | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `id` | UUID | Sim | Identificador único, gerado automaticamente (`gen_random_uuid()`) |
| `imobiliaria_id` | UUID | Sim | FK para `imobiliarias(id)` — define o tenant do usuário |
| `email` | VARCHAR(255) | Sim | E-mail do usuário — único no sistema |
| `senha` | VARCHAR(255) | Sim | Senha armazenada com hash BCrypt |
| `nome` | VARCHAR(255) | Sim | Nome completo do usuário |
| `perfil` | VARCHAR(20) | Sim | Perfil de acesso: `ADMIN`, `GERENTE`, `OPERADOR`, `FINANCEIRO` ou `READONLY`. Padrão: `OPERADOR` |
| `created_at` | TIMESTAMP | Sim | Data e hora de criação do registro |
| `updated_at` | TIMESTAMP | Sim | Data e hora da última atualização |
| `deleted_at` | TIMESTAMP | Não | Preenchido na exclusão lógica. Nulo = ativo |

---

## Tabela: `locadores`

| Coluna | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `id` | UUID | Sim | Identificador único, gerado automaticamente (`gen_random_uuid()`) |
| `imobiliaria_id` | UUID | Sim | FK para `imobiliarias(id)` — tenant do locador |
| `tipo_pessoa` | VARCHAR(2) | Sim | Tipo de pessoa: `PF` (Física) ou `PJ` (Jurídica) |
| `cpf_cnpj` | VARCHAR(14) | Sim | CPF (11 dígitos) ou CNPJ (14 dígitos) |
| `nome` | VARCHAR(255) | Sim | Nome completo ou razão social |
| `email` | VARCHAR(255) | Não | E-mail de contato |
| `telefone` | VARCHAR(255) | Não | Telefone de contato |
| `regime_tributario` | VARCHAR(30) | Não | Regime para cálculo de IBS/CBS: `PF`, `SIMPLES_NACIONAL`, `LUCRO_PRESUMIDO` ou `LUCRO_REAL`. Adicionado em V2 |
| `created_at` | TIMESTAMP | Sim | Data e hora de criação do registro |
| `updated_at` | TIMESTAMP | Sim | Data e hora da última atualização |
| `deleted_at` | TIMESTAMP | Não | Preenchido na exclusão lógica. Nulo = ativo |

---

## Tabela: `imoveis`

| Coluna | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `id` | UUID | Sim | Identificador único, gerado automaticamente (`gen_random_uuid()`) |
| `imobiliaria_id` | UUID | Sim | FK para `imobiliarias(id)` — tenant do imóvel |
| `locador_id` | UUID | Não | FK para `locadores(id)` — proprietário (opcional no cadastro inicial) |
| `codigo` | VARCHAR(255) | Sim | Código interno do imóvel na imobiliária |
| `tipo` | VARCHAR(20) | Sim | Tipo: `RESIDENCIAL`, `COMERCIAL`, `RURAL` ou `MISTO` |
| `cep` | CHAR(8) | Sim | CEP (8 dígitos, sem hífen) |
| `logradouro` | VARCHAR(255) | Sim | Rua, avenida etc. |
| `numero` | VARCHAR(255) | Sim | Número do endereço |
| `complemento` | VARCHAR(255) | Não | Apto, bloco etc. |
| `bairro` | VARCHAR(255) | Sim | Bairro |
| `cidade` | VARCHAR(255) | Sim | Cidade |
| `uf` | CHAR(2) | Sim | Estado (sigla) |
| `area_total` | NUMERIC(12,2) | Não | Área total em m² |
| `quartos` | INTEGER | Não | Número de quartos |
| `vagas` | INTEGER | Não | Número de vagas de garagem |
| `valor_compra` | NUMERIC(15,2) | Não | Valor de aquisição — base para GCAP |
| `data_compra` | DATE | Não | Data de aquisição |
| `valor_venal` | NUMERIC(15,2) | Não | Valor de referência municipal para IBS/CBS. Adicionado em V2 |
| `created_at` | TIMESTAMP | Sim | Data e hora de criação do registro |
| `updated_at` | TIMESTAMP | Sim | Data e hora da última atualização |
| `deleted_at` | TIMESTAMP | Não | Preenchido na exclusão lógica. Nulo = ativo |

---

## Tabela: `contratos_locacao`

| Coluna | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `id` | UUID | Sim | Identificador único, gerado automaticamente (`gen_random_uuid()`) |
| `imobiliaria_id` | UUID | Sim | FK para `imobiliarias(id)` — tenant do contrato |
| `imovel_id` | UUID | Sim | FK para `imoveis(id)` — imóvel objeto do contrato |
| `tipo_locacao` | VARCHAR(20) | Sim | Modalidade: `RESIDENCIAL_LONGA`, `COMERCIAL`, `SHORT_STAY` ou `RURAL` |
| `status` | VARCHAR(20) | Sim | Estado: `RASCUNHO`, `ATIVO`, `RESCINDIDO` ou `ENCERRADO`. Padrão: `RASCUNHO` |
| `locatario_tipo` | VARCHAR(2) | Sim | Tipo do locatário: `PF` ou `PJ` |
| `locatario_cpf_cnpj` | VARCHAR(14) | Sim | CPF ou CNPJ do locatário |
| `locatario_nome` | VARCHAR(255) | Sim | Nome do locatário (inquilino) |
| `valor_aluguel` | NUMERIC(15,2) | Sim | Valor mensal do aluguel em reais |
| `dia_vencimento` | INTEGER | Sim | Dia do mês para vencimento (1 a 31) |
| `data_inicio` | DATE | Sim | Data de início da vigência |
| `data_fim` | DATE | Não | Data de encerramento (nulo = contrato em aberto) |
| `prazo_meses` | INTEGER | Não | Prazo contratual em meses |
| `created_at` | TIMESTAMP | Sim | Data e hora de criação do registro |
| `updated_at` | TIMESTAMP | Sim | Data e hora da última atualização |
| `deleted_at` | TIMESTAMP | Não | Preenchido no encerramento do contrato. Nulo = ativo |

---

## Tabela: `notas_fiscais`

| Coluna | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `id` | UUID | Sim | Identificador único, gerado automaticamente (`gen_random_uuid()`) |
| `imobiliaria_id` | UUID | Sim | FK para `imobiliarias(id)` — tenant da nota |
| `contrato_id` | UUID | Sim | FK para `contratos_locacao(id)` — contrato de origem |
| `numero` | VARCHAR(255) | Não | Número da NF na SEFAZ |
| `serie` | VARCHAR(255) | Não | Série da NF |
| `chave_acesso` | VARCHAR(44) | Não | Chave de acesso SEFAZ — única no sistema |
| `status` | VARCHAR(20) | Sim | Estado: `AGUARDANDO`, `PROCESSANDO`, `AUTORIZADA`, `REJEITADA` ou `CANCELADA`. Padrão: `AGUARDANDO` |
| `valor_servico` | NUMERIC(15,2) | Sim | Valor do serviço de locação (base de cálculo) |
| `valor_ibs` | NUMERIC(15,4) | Sim | Valor calculado do IBS. Padrão: 0 (fase de testes 2026) |
| `valor_cbs` | NUMERIC(15,4) | Sim | Valor calculado do CBS. Padrão: 0 (fase de testes 2026) |
| `recolhimento_obrigatorio` | BOOLEAN | Sim | `false` em 2026; `true` a partir de 2027 (LC 214/2025). Padrão: `false` |
| `tentativas` | INTEGER | Sim | Contador de tentativas de envio à SEFAZ (máx. 5). Padrão: 0 |
| `erro_sefaz` | TEXT | Não | Mensagem de erro retornada pela SEFAZ |
| `created_at` | TIMESTAMP | Sim | Data e hora de criação do registro |
| `updated_at` | TIMESTAMP | Sim | Data e hora da última atualização |
| `deleted_at` | TIMESTAMP | Não | Preenchido na exclusão lógica. Nulo = ativo |

---

## Tabela: `aliquotas_vigentes`

Tabela de configuração do Motor Tributário. Armazena as alíquotas IBS/CBS por regime, tipo de imóvel e ano. Nunca hardcodar alíquotas no código — sempre consultar esta tabela.

| Coluna | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `id` | UUID | Sim | Identificador único, gerado automaticamente (`gen_random_uuid()`) |
| `regime` | VARCHAR(30) | Sim | Regime tributário: `PF`, `SIMPLES_NACIONAL`, `LUCRO_PRESUMIDO` ou `LUCRO_REAL` |
| `tipo_imovel` | VARCHAR(20) | Sim | Tipo do imóvel: `RESIDENCIAL`, `COMERCIAL`, `RURAL` ou `MISTO` |
| `aliquota_ibs` | NUMERIC(6,4) | Sim | Alíquota do IBS — ex: `0.0145` = 1,45% |
| `aliquota_cbs` | NUMERIC(6,4) | Sim | Alíquota do CBS — ex: `0.0076` = 0,76% |
| `ano_vigencia` | INTEGER | Sim | Ano de vigência das alíquotas (2026, 2027 …) |
| `created_at` | TIMESTAMP | Sim | Data e hora de criação do registro |

Constraint UNIQUE: `(regime, tipo_imovel, ano_vigencia)` — uma linha por combinação por ano.

---

## Tabela: `boletos`

Representa o UC-003 (Gerar Boleto de Aluguel). Simulado no PI — sem gateway bancário real.

| Coluna | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `id` | UUID | Sim | Identificador único, gerado automaticamente (`gen_random_uuid()`) |
| `imobiliaria_id` | UUID | Sim | FK para `imobiliarias(id)` — tenant do boleto |
| `contrato_id` | UUID | Sim | FK para `contratos_locacao(id)` — contrato de origem |
| `valor_aluguel` | NUMERIC(15,2) | Sim | Valor base do aluguel neste boleto |
| `aliquota_ibs` | NUMERIC(6,4) | Sim | Alíquota IBS registrada no momento da geração (imutável) |
| `aliquota_cbs` | NUMERIC(6,4) | Sim | Alíquota CBS registrada no momento da geração (imutável) |
| `valor_ibs` | NUMERIC(15,4) | Sim | Valor do IBS calculado pelo Motor Tributário |
| `valor_cbs` | NUMERIC(15,4) | Sim | Valor do CBS calculado pelo Motor Tributário |
| `valor_liquido` | NUMERIC(15,2) | Sim | Valor que o locador recebe após retenção (Split Payment) |
| `data_vencimento` | DATE | Sim | Data de vencimento do boleto |
| `status` | VARCHAR(20) | Sim | Estado: `GERADO`, `PAGO`, `VENCIDO` ou `CANCELADO`. Padrão: `GERADO` |
| `regime_tributario` | VARCHAR(30) | Sim | Regime tributário do locador registrado no momento da geração |
| `tipo_imovel` | VARCHAR(20) | Sim | Tipo do imóvel registrado no momento da geração |
| `created_at` | TIMESTAMP | Sim | Data e hora de criação do registro |
| `updated_at` | TIMESTAMP | Sim | Data e hora da última atualização |
| `deleted_at` | TIMESTAMP | Não | Preenchido no cancelamento. Nulo = ativo |

---

## Convenções

| Convenção | Descrição |
|---|---|
| Chave primária | `UUID` gerado por `gen_random_uuid()` em todas as tabelas |
| Soft delete | Campo `deleted_at TIMESTAMP` — nulo indica registro ativo |
| Datas | `DATE` para datas; `TIMESTAMP` para data e hora |
| Valores monetários | `NUMERIC(15,2)` — precisão para reais e centavos |
| Valores tributários | `NUMERIC(15,4)` — precisão extra para IBS e CBS |
| Alíquotas | `NUMERIC(6,4)` — ex: `0.0145` representa 1,45% |
| Audit columns | `created_at`, `updated_at` e `deleted_at` herdados de `BaseEntity.java` |
| Multi-tenancy | Todas as tabelas de negócio possuem `imobiliaria_id` |
