# Banco de Dados — ImobFiscal

**PI 2 · FATEC DSM 2026-2**

---

## DER — Diagrama Entidade-Relacionamento

```mermaid
erDiagram
    IMOBILIARIAS {
        uuid id PK
        varchar cnpj
        varchar razao
        varchar nome_fantasia
        varchar email
        varchar telefone
        varchar plano
        timestamp created_at
        timestamp updated_at
        timestamp deleted_at
    }

    USUARIOS {
        uuid id PK
        uuid imobiliaria_id FK
        varchar email
        varchar senha
        varchar nome
        varchar perfil
        timestamp created_at
        timestamp updated_at
        timestamp deleted_at
    }

    LOCADORES {
        uuid id PK
        uuid imobiliaria_id FK
        varchar tipo_pessoa
        varchar cpf_cnpj
        varchar nome
        varchar email
        varchar telefone
        varchar regime_tributario
        timestamp created_at
        timestamp updated_at
        timestamp deleted_at
    }

    IMOVEIS {
        uuid id PK
        uuid imobiliaria_id FK
        uuid locador_id FK
        varchar codigo
        varchar tipo
        char cep
        varchar logradouro
        varchar numero
        varchar complemento
        varchar bairro
        varchar cidade
        char uf
        numeric area_total
        integer quartos
        integer vagas
        numeric valor_compra
        date data_compra
        numeric valor_venal
        timestamp created_at
        timestamp updated_at
        timestamp deleted_at
    }

    CONTRATOS_LOCACAO {
        uuid id PK
        uuid imobiliaria_id FK
        uuid imovel_id FK
        varchar tipo_locacao
        varchar status
        varchar locatario_tipo
        varchar locatario_cpf_cnpj
        varchar locatario_nome
        numeric valor_aluguel
        integer dia_vencimento
        date data_inicio
        date data_fim
        integer prazo_meses
        timestamp created_at
        timestamp updated_at
        timestamp deleted_at
    }

    NOTAS_FISCAIS {
        uuid id PK
        uuid imobiliaria_id FK
        uuid contrato_id FK
        varchar numero
        varchar serie
        varchar chave_acesso
        varchar status
        numeric valor_servico
        numeric valor_ibs
        numeric valor_cbs
        boolean recolhimento_obrigatorio
        integer tentativas
        text erro_sefaz
        timestamp created_at
        timestamp updated_at
        timestamp deleted_at
    }

    ALIQUOTAS_VIGENTES {
        uuid id PK
        varchar regime
        varchar tipo_imovel
        numeric aliquota_ibs
        numeric aliquota_cbs
        integer ano_vigencia
        timestamp created_at
    }

    BOLETOS {
        uuid id PK
        uuid imobiliaria_id FK
        uuid contrato_id FK
        numeric valor_aluguel
        numeric aliquota_ibs
        numeric aliquota_cbs
        numeric valor_ibs
        numeric valor_cbs
        numeric valor_liquido
        date data_vencimento
        varchar status
        varchar regime_tributario
        varchar tipo_imovel
        timestamp created_at
        timestamp updated_at
        timestamp deleted_at
    }

    IMOBILIARIAS ||--o{ USUARIOS : "possui"
    IMOBILIARIAS ||--o{ LOCADORES : "cadastra"
    IMOBILIARIAS ||--o{ IMOVEIS : "gerencia"
    IMOBILIARIAS ||--o{ CONTRATOS_LOCACAO : "opera"
    IMOBILIARIAS ||--o{ NOTAS_FISCAIS : "emite"
    IMOBILIARIAS ||--o{ BOLETOS : "gera"
    LOCADORES ||--o{ IMOVEIS : "possui"
    IMOVEIS ||--o{ CONTRATOS_LOCACAO : "objeto de"
    CONTRATOS_LOCACAO ||--o{ NOTAS_FISCAIS : "origina"
    CONTRATOS_LOCACAO ||--o{ BOLETOS : "origina"
```

---

## Arquivos

| Arquivo | Descrição |
|---|---|
| `schema.sql` | DDL — criação das 6 tabelas base |
| `V2__motor_fiscal.sql` | Migração V2 — adiciona `aliquotas_vigentes`, `boletos`, `regime_tributario` (locadores) e `valor_venal` (imoveis) |
| `seed.sql` | Dados fictícios para demonstração |

## Como executar

```bash
# 1. Criar o banco
psql -U postgres -c "CREATE DATABASE imobfiscal;"

# 2. Criar as tabelas base (schema.sql requer a extensão pgcrypto)
psql -U postgres -d imobfiscal -f database/schema.sql

# 3. Aplicar migração V2 (Motor Tributário + boletos)
psql -U postgres -d imobfiscal -f database/V2__motor_fiscal.sql

# 4. Inserir dados de exemplo
psql -U postgres -d imobfiscal -f database/seed.sql
```

## Observações

- `deleted_at`: todas as tabelas de negócio utilizam exclusão lógica. Registros com este campo preenchido são ignorados nas listagens (`WHERE deleted_at IS NULL`).
- `aliquota_ibs` / `aliquota_cbs` em `boletos` e `notas_fiscais`: valores decimais — ex: `0.0145` = 1,45%. Alíquotas ilustrativas conforme LC 214/2025.
- Chaves primárias: `UUID` gerado por `gen_random_uuid()` (requer extensão `pgcrypto`).
- Chaves estrangeiras: declaradas com `REFERENCES` para garantir integridade referencial.
- `aliquotas_vigentes`: não usa soft delete — é uma tabela de configuração, nunca apagada logicamente.
