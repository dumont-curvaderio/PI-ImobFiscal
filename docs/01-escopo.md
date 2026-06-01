# Fase 1 — Escopo do Sistema

**Projeto:** ImobFiscal  
**PI:** 2º Semestre DSM — FATEC Indaiatuba  
**Data:** 2026-06-01

---

## 1. Descrição do Problema

Imobiliárias e proprietários de imóveis precisam gerenciar contratos de locação de forma
organizada: cadastrar imóveis, registrar locadores e locatários, formalizar contratos e
acompanhar os valores de aluguel. Com a entrada em vigor da Reforma Tributária (LC 214/2025),
surgiu também a necessidade de calcular e exibir os novos tributos IBS e CBS nas transações.

Sem um sistema digital, esse controle é feito em planilhas ou papel, o que gera erros,
retrabalho e dificuldade de consulta.

---

## 2. Proposta do Sistema

O **ImobFiscal** é um sistema web que permite:

- Cadastrar e gerenciar **Locadores** (proprietários de imóveis — Pessoa Física ou Jurídica)
- Cadastrar e gerenciar **Imóveis** vinculados a um locador
- Criar e gerenciar **Contratos de Locação** (locador + locatário + valor do aluguel)
- Calcular automaticamente **IBS e CBS** sobre o valor do aluguel (Reforma Tributária)
- Gerar o resumo de uma **Nota Fiscal** simulada com os valores tributários

---

## 3. Atores do Sistema

| Ator | Descrição |
|---|---|
| **AdmImobiliaria** | Administrador que gerencia tudo: cadastra locadores, imóveis, contratos e clientes |
| **Locador** | Proprietário do imóvel — pode consultar seus imóveis e contratos |
| **Locatário** | Inquilino — pode consultar seu contrato e o boleto simulado |

---

## 4. Funcionalidades no Escopo (PI 2)

### Módulo de Locadores
- Cadastrar locador (nome, CPF ou CNPJ, regime tributário: PF / PJ / Simples Nacional)
- Listar locadores cadastrados
- Editar dados de um locador
- Excluir locador (soft delete)

### Módulo de Imóveis
- Cadastrar imóvel (endereço, tipo de uso: Residencial / Comercial, valor venal, locador responsável)
- Listar imóveis com filtro por locador e tipo
- Editar dados de um imóvel
- Excluir imóvel (soft delete)

### Módulo de Contratos de Locação
- Criar contrato (locador, nome do locatário, valor do aluguel, data de início, dia de vencimento)
- Listar contratos ativos
- Editar contrato
- Encerrar contrato (soft delete)

### Cálculo Tributário (IBS/CBS)
- Calcular IBS e CBS sobre o valor do aluguel conforme alíquotas da Reforma Tributária
- Exibir o detalhamento tributário vinculado ao contrato

### Autenticação
- Login com e-mail e senha (JWT)
- Rotas protegidas — apenas usuário autenticado acessa o sistema

---

## 5. Fora do Escopo (PI 2)

Os itens abaixo existem no produto final planejado, mas **não serão implementados** neste semestre:

| Item | Motivo |
|---|---|
| Emissão real de NF-e (SEFAZ) | Requer certificado digital e integração fiscal real |
| Boleto bancário real | Requer integração com banco/fintech |
| Assinatura eletrônica de contrato | Requer integração com Clicksign ou similar |
| Aplicativo mobile | Foco do PI é web |
| Multi-tenancy (múltiplas imobiliárias) | Complexidade além do 2º semestre |
| Relatórios e dashboards avançados | Entregas futuras |

---

## 6. Contexto Tecnológico

| Camada | Tecnologia |
|---|---|
| Frontend | React + JavaScript (Vite) |
| Backend API | Spring Boot + Java |
| Banco de Dados | PostgreSQL |
| Autenticação | JWT (Spring Security) |
| Deploy | Vercel (frontend) + Railway (backend) |

---

## 7. Diagrama de Contexto (simplificado)

```
[Navegador do usuário]
        |
        | HTTP/HTTPS
        v
[Frontend — React/Vite]  ←→  [Backend Spring Boot]  ←→  [PostgreSQL]
```

---

## 8. Referências

- Manual do Projeto Integrador PI 2 — FATEC DSM 2026-2
- Diagrama de Classes orientador — professor da disciplina
- LC 214/2025 — Reforma Tributária brasileira
