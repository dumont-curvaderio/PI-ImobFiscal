# Fase 3 — Dicionário de Dados

**Projeto:** ImobFiscal  
**PI:** 2º Semestre DSM — FATEC Indaiatuba  
**Data:** 2026-06-01

---

## Tabela: `usuarios`

| Coluna | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `id` | BIGSERIAL | Sim | Identificador único, gerado automaticamente |
| `email` | VARCHAR(150) | Sim | E-mail do usuário — único no sistema |
| `senha` | VARCHAR(255) | Sim | Senha armazenada com hash BCrypt |
| `criado_em` | TIMESTAMP | Sim | Data e hora de criação do registro |

---

## Tabela: `locadores`

| Coluna | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `id` | BIGSERIAL | Sim | Identificador único, gerado automaticamente |
| `nome` | VARCHAR(150) | Sim | Nome completo do locador |
| `cpf` | VARCHAR(14) | Não | CPF — preenchido apenas para Pessoa Física |
| `cnpj` | VARCHAR(18) | Não | CNPJ — preenchido apenas para PJ ou Simples Nacional |
| `regime_tributario` | VARCHAR(20) | Sim | Regime: `PF`, `PJ` ou `SIMPLES` |
| `criado_em` | TIMESTAMP | Sim | Data e hora de criação do registro |
| `deleted_at` | TIMESTAMP | Não | Preenchido na exclusão lógica. Nulo = ativo |

---

## Tabela: `imoveis`

| Coluna | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `id` | BIGSERIAL | Sim | Identificador único, gerado automaticamente |
| `endereco` | VARCHAR(255) | Sim | Endereço completo do imóvel |
| `tipo_uso` | VARCHAR(20) | Sim | Tipo de uso: `RESIDENCIAL` ou `COMERCIAL` |
| `valor_venal` | NUMERIC(15,2) | Sim | Valor venal do imóvel em reais |
| `locador_id` | BIGINT | Sim | Referência ao locador proprietário do imóvel |
| `criado_em` | TIMESTAMP | Sim | Data e hora de criação do registro |
| `deleted_at` | TIMESTAMP | Não | Preenchido na exclusão lógica. Nulo = ativo |

---

## Tabela: `contratos_locacao`

| Coluna | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `id` | BIGSERIAL | Sim | Identificador único, gerado automaticamente |
| `locatario_nome` | VARCHAR(150) | Sim | Nome do locatário (inquilino) |
| `valor_aluguel` | NUMERIC(15,2) | Sim | Valor mensal do aluguel em reais |
| `data_inicio` | DATE | Sim | Data de início da vigência do contrato |
| `dia_vencimento` | SMALLINT | Sim | Dia do mês para vencimento (1 a 31) |
| `locador_id` | BIGINT | Sim | Referência ao locador do contrato |
| `imovel_id` | BIGINT | Sim | Referência ao imóvel objeto do contrato |
| `criado_em` | TIMESTAMP | Sim | Data e hora de criação do registro |
| `deleted_at` | TIMESTAMP | Não | Preenchido no encerramento do contrato. Nulo = ativo |

---

## Tabela: `notas_fiscais`

| Coluna | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| `id` | BIGSERIAL | Sim | Identificador único, gerado automaticamente |
| `data_emissao` | DATE | Sim | Data de emissão do documento fiscal |
| `valor_bruto` | NUMERIC(15,2) | Sim | Valor do aluguel antes dos tributos |
| `valor_liquido` | NUMERIC(15,2) | Sim | Valor do aluguel após dedução de IBS e CBS |
| `aliquota_ibs` | NUMERIC(5,4) | Sim | Alíquota do IBS aplicada — ex: `0.0875` = 8,75% |
| `aliquota_cbs` | NUMERIC(5,4) | Sim | Alíquota do CBS aplicada — ex: `0.0875` = 8,75% |
| `contrato_id` | BIGINT | Sim | Referência ao contrato de locação |
| `criado_em` | TIMESTAMP | Sim | Data e hora de criação do registro |
| `deleted_at` | TIMESTAMP | Não | Preenchido na exclusão lógica. Nulo = ativo |

---

## Convenções

| Convenção | Descrição |
|---|---|
| Chave primária | `BIGSERIAL` em todas as tabelas — auto-incremento |
| Soft delete | Campo `deleted_at TIMESTAMP` — nulo indica registro ativo |
| Datas | `DATE` para datas; `TIMESTAMP` para data e hora |
| Valores monetários | `NUMERIC(15,2)` — precisão para reais e centavos |
| Alíquotas | `NUMERIC(5,4)` — ex: `0.0875` representa 8,75% |
