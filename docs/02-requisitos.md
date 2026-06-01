# Fase 2 — Levantamento de Requisitos

**Projeto:** ImobFiscal  
**PI:** 2º Semestre DSM — FATEC Indaiatuba  
**Data:** 2026-06-01

---

## 1. Requisitos Funcionais (RF)

Descrevem **o que o sistema deve fazer**.

| ID | Descrição | Ator | Prioridade |
|---|---|---|---|
| RF01 | O sistema deve permitir que o usuário faça login com e-mail e senha | AdmImobiliaria | Alta |
| RF02 | O sistema deve permitir cadastrar um Locador informando nome, CPF ou CNPJ e regime tributário (PF / PJ / Simples Nacional) | AdmImobiliaria | Alta |
| RF03 | O sistema deve listar todos os Locadores cadastrados | AdmImobiliaria | Alta |
| RF04 | O sistema deve permitir editar os dados de um Locador | AdmImobiliaria | Alta |
| RF05 | O sistema deve permitir excluir um Locador (exclusão lógica — soft delete) | AdmImobiliaria | Alta |
| RF06 | O sistema deve permitir cadastrar um Imóvel informando endereço, tipo de uso (Residencial / Comercial), valor venal e o Locador responsável | AdmImobiliaria | Alta |
| RF07 | O sistema deve listar todos os Imóveis cadastrados, com filtro por Locador | AdmImobiliaria | Alta |
| RF08 | O sistema deve permitir editar os dados de um Imóvel | AdmImobiliaria | Alta |
| RF09 | O sistema deve permitir excluir um Imóvel (exclusão lógica — soft delete) | AdmImobiliaria | Alta |
| RF10 | O sistema deve permitir criar um Contrato de Locação informando Locador, nome do Locatário, valor do aluguel, data de início e dia de vencimento | AdmImobiliaria | Alta |
| RF11 | O sistema deve listar todos os Contratos ativos | AdmImobiliaria | Alta |
| RF12 | O sistema deve permitir editar os dados de um Contrato | AdmImobiliaria | Alta |
| RF13 | O sistema deve permitir encerrar um Contrato (exclusão lógica — soft delete) | AdmImobiliaria | Alta |
| RF14 | O sistema deve calcular automaticamente os tributos IBS e CBS sobre o valor do aluguel conforme as alíquotas da Reforma Tributária (LC 214/2025) | Sistema | Alta |
| RF15 | O sistema deve exibir o resumo tributário (IBS, CBS, valor líquido) vinculado ao Contrato de Locação | AdmImobiliaria | Média |

---

## 2. Requisitos Não Funcionais (RNF)

Descrevem **como o sistema deve se comportar**.

| ID | Descrição | Categoria |
|---|---|---|
| RNF01 | O sistema deve utilizar PostgreSQL como banco de dados relacional | Tecnologia |
| RNF02 | A API deve seguir o padrão REST com respostas em formato JSON | Arquitetura |
| RNF03 | As senhas dos usuários devem ser armazenadas com hash BCrypt | Segurança |
| RNF04 | O acesso às rotas protegidas deve exigir autenticação via token JWT | Segurança |
| RNF05 | O frontend deve funcionar corretamente em navegadores desktop modernos (Chrome, Firefox, Edge) | Usabilidade |
| RNF06 | O backend deve ser desenvolvido com Spring Boot e Java | Tecnologia |
| RNF07 | O frontend deve ser desenvolvido com React, JavaScript e Vite | Tecnologia |

---

## 3. Regras de Negócio (RN)

Complementam os requisitos com restrições específicas do domínio.

| ID | Descrição |
|---|---|
| RN01 | Um Imóvel só pode ser cadastrado se estiver vinculado a um Locador existente |
| RN02 | Um Contrato só pode ser criado se estiver vinculado a um Locador e a um Imóvel existentes |
| RN03 | Registros excluídos (soft delete) não devem aparecer nas listagens |
| RN04 | O cálculo de IBS e CBS é realizado sobre o valor do aluguel informado no Contrato |
| RN05 | O sistema não emite Nota Fiscal real — o resultado tributário é apenas uma simulação |
