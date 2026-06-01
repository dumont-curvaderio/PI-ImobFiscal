# Fase 2 — Diagrama de Classes

**Projeto:** ImobFiscal  
**PI:** 2º Semestre DSM — FATEC Indaiatuba  
**Data:** 2026-06-01

---

## Diagrama

```mermaid
classDiagram
    direction TB

    class Usuario {
        +Long id
        +String email
        +String senha
        +LocalDateTime criadoEm
    }

    class Locador {
        +Long id
        +String nome
        +String cpf
        +String cnpj
        +String regimeTributario
        +LocalDateTime criadoEm
        +LocalDateTime deletedAt
    }

    class Imovel {
        +Long id
        +String endereco
        +String tipoUso
        +BigDecimal valorVenal
        +LocalDateTime criadoEm
        +LocalDateTime deletedAt
    }

    class ContratoLocacao {
        +Long id
        +String locatarioNome
        +BigDecimal valorAluguel
        +LocalDate dataInicio
        +Integer diaVencimento
        +LocalDateTime criadoEm
        +LocalDateTime deletedAt
    }

    class NotaFiscal {
        +Long id
        +LocalDate dataEmissao
        +BigDecimal valorBruto
        +BigDecimal valorLiquido
        +BigDecimal aliquotaIbs
        +BigDecimal aliquotaCbs
        +LocalDateTime criadoEm
        +LocalDateTime deletedAt
    }

    class CalculadoraImposto {
        <<interface>>
        +calcular(BigDecimal valorAluguel) ResultadoCalculo
    }

    class CalculadoraReformaTributaria {
        -BigDecimal aliquotaIbs
        -BigDecimal aliquotaCbs
        +calcular(BigDecimal valorAluguel) ResultadoCalculo
    }

    class ResultadoCalculo {
        +BigDecimal valorIbs
        +BigDecimal valorCbs
        +BigDecimal valorLiquido
    }

    Locador "1" --> "0..*" Imovel : possui
    Locador "1" --> "0..*" ContratoLocacao : celebra
    ContratoLocacao "1" --> "0..*" NotaFiscal : gera
    CalculadoraReformaTributaria ..|> CalculadoraImposto : implements
    CalculadoraImposto ..> ResultadoCalculo : retorna
```

---

## Descrição das Classes

### Entidades de domínio

| Classe | Responsabilidade |
|---|---|
| **Usuario** | Usuário autenticado no sistema (AdmImobiliaria). Armazena e-mail e senha com hash BCrypt |
| **Locador** | Proprietário do imóvel — pode ser Pessoa Física (CPF) ou Pessoa Jurídica (CNPJ). O campo `regimeTributario` define as alíquotas aplicáveis |
| **Imovel** | Bem imóvel vinculado a um Locador. O `tipoUso` distingue Residencial de Comercial |
| **ContratoLocacao** | Formaliza a relação entre Locador e Locatário. Contém o valor do aluguel que será base para o cálculo tributário |
| **NotaFiscal** | Registra os valores tributários (IBS e CBS) calculados sobre o aluguel de um Contrato |

### Classes de cálculo

| Classe | Responsabilidade |
|---|---|
| **CalculadoraImposto** | Interface que define o contrato de cálculo tributário — padrão Strategy |
| **CalculadoraReformaTributaria** | Implementa o cálculo de IBS e CBS conforme LC 214/2025 |
| **ResultadoCalculo** | Objeto com os valores calculados: IBS, CBS e valor líquido |

---

## Relacionamentos

| Relacionamento | Cardinalidade | Descrição |
|---|---|---|
| Locador → Imovel | 1 para N | Um Locador pode ter vários imóveis |
| Locador → ContratoLocacao | 1 para N | Um Locador pode ter vários contratos |
| ContratoLocacao → NotaFiscal | 1 para N | Um Contrato pode gerar várias notas fiscais |
| CalculadoraReformaTributaria → CalculadoraImposto | Implementação | Segue o padrão Strategy |

---

## Observações

- `deletedAt`: campo presente nas entidades de negócio para exclusão lógica (soft delete). Registros com este campo preenchido são tratados como excluídos.
- `regimeTributario`: valores possíveis — `PF`, `PJ`, `SIMPLES`.
- `tipoUso`: valores possíveis — `RESIDENCIAL`, `COMERCIAL`.
- O cálculo tributário não é persistido diretamente — é gerado pelo `CalculadoraReformaTributaria` e armazenado na `NotaFiscal`.
