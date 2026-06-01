# ImobFiscal — Sistema de Gestão Imobiliária

**Projeto Integrador 2 · FATEC Indaiatuba Dr. Archimedes Lammoglia · DSM 2026-2**

---

## 1. Identificação do Projeto

| Campo | Valor |
|---|---|
| Curso | Desenvolvimento de Software Multiplataforma |
| Semestre / PI | 2º Semestre — Projeto Integrador 2 (PI 2) |
| Ano/Semestre | 2026-2 |
| Tema | Desenvolvimento de Sistemas Web com Persistência de Dados |
| Disciplina Norteadora | Engenharia de Software II |
| Disciplinas Satélite | Desenvolvimento Web II · Banco de Dados Relacional |
| Repositório | https://github.com/dumont-curvaderio/PI-ImobFiscal |

**Integrantes:**

| Nome | RA |
|---|---|
| Christian Dumont | — |

---

## 2. Tema, Contexto e Desafio

Imobiliárias e proprietários de imóveis precisam gerenciar contratos de locação de forma
digital: cadastrar imóveis, registrar locadores e locatários, formalizar contratos e
calcular os tributos da **Reforma Tributária brasileira** (IBS/CBS — LC 214/2025).

O **ImobFiscal** resolve esse problema com um sistema web completo, com persistência em
banco de dados relacional e integração entre frontend e backend.

---

## 3. Objetivos

**Geral:** Desenvolver um sistema web com persistência de dados aplicando conceitos de
engenharia de software, desenvolvimento web e banco de dados relacional.

**Específicos:**
- Modelar o banco de dados com entidades Locador, Imóvel, Contrato e Nota Fiscal
- Implementar CRUD completo integrado ao PostgreSQL
- Integrar frontend React com backend Spring Boot via API REST
- Calcular IBS e CBS sobre aluguéis (Reforma Tributária)
- Documentar o sistema com diagramas UML (Caso de Uso, Classes, Sequência) e DER

---

## 4. Stack Tecnológica

| Camada | Tecnologia |
|---|---|
| Frontend | React 18 + JavaScript + Vite |
| Backend API | Spring Boot + Java |
| Banco de Dados | PostgreSQL 15 |
| Autenticação | JWT (Spring Security) |
| Deploy Frontend | Vercel |
| Deploy Backend | Railway |
| Versionamento | Git + GitHub |

---

## 5. Estrutura do Repositório

```
imobfiscal/
├── frontend/          # Interface web — React + Vite
├── backend/           # API REST — Spring Boot + Java
├── database/
│   ├── schema.sql     # DDL: criação das tabelas
│   ├── seed.sql       # Dados iniciais de exemplo
│   └── README.md      # DER (Diagrama Entidade-Relacionamento em Mermaid)
├── docs/
│   ├── 01-escopo.md
│   ├── 02-requisitos.md
│   ├── 03-casos-de-uso.md
│   ├── 04-diagrama-classes.md
│   ├── 05-dicionario-dados.md
│   ├── 06-plano-testes.md
│   └── 07-consideracoes-finais.md
└── README.md
```

---

## 6. Como Rodar Localmente

### Pré-requisitos
- Java 21+
- Maven 3.9+ (ou usar o Maven Wrapper `./mvnw` incluído no projeto)
- PostgreSQL 15+
- Node.js 20+

### Banco de Dados

```bash
# Criar o banco e rodar o schema
psql -U postgres -c "CREATE DATABASE imobfiscal;"
psql -U postgres -d imobfiscal -f database/schema.sql
psql -U postgres -d imobfiscal -f database/seed.sql
```

### Backend (Spring Boot)

```bash
cd backend
cp src/main/resources/application.example.properties src/main/resources/application.properties
# preencher spring.datasource.url, username, password e jwt.secret
./mvnw spring-boot:run    # sobe em http://localhost:8080
```

### Frontend (React)

```bash
cd frontend
npm install
cp .env.example .env    # preencher VITE_API_URL=http://localhost:8080
npm run dev             # abre em http://localhost:5173
```

---

## 7. Fases do Projeto

| Fase | Descrição | Semana |
|---|---|---|
| 1 | Revisão da ideia e escopo | 2 |
| 2 | Levantamento de requisitos | 4 |
| 3 | Modelagem do banco de dados | 6 |
| 4 | Desenvolvimento do sistema | 10 |
| 5 | Testes e validação | 12 |
| 6 | Produto final e apresentação | 14–15 |

---

## 8. Documentação Técnica

Todos os documentos estão na pasta [`/docs`](./docs/):

- [Escopo do Sistema](docs/01-escopo.md)
- [Requisitos Funcionais e Não Funcionais](docs/02-requisitos.md)
- [Diagrama de Casos de Uso](docs/03-casos-de-uso.md)
- [Diagrama de Classes](docs/04-diagrama-classes.md)
- [Diagrama de Sequência](docs/03-diagrama-sequencia.md)
- [Dicionário de Dados](docs/05-dicionario-dados.md)
- [Plano de Testes](docs/06-plano-testes.md)
- [Considerações Finais](docs/07-consideracoes-finais.md)

O DER (Diagrama Entidade-Relacionamento) está em [`database/README.md`](database/README.md).

---

## 9. Entidades Principais

Baseado no Diagrama de Classes orientador da disciplina:

| Entidade | Responsabilidade |
|---|---|
| **Locador** | Proprietário do imóvel (PF ou PJ) |
| **Imovel** | Ativo imobiliário vinculado a um Locador |
| **ContratoLocacao** | Liga Locador + Locatário com valor e prazo |
| **NotaFiscal** | Registro tributário com IBS/CBS calculados |

---

## 10. Referências

- Manual do Projeto Integrador PI 2 — FATEC DSM 2026-2
- LC 214/2025 — Lei Complementar da Reforma Tributária
- Documentação oficial: React, Spring Boot, Java, PostgreSQL

---

*FATEC Indaiatuba — Dr. Archimedes Lammoglia · 2026*
