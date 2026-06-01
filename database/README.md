# Banco de Dados — ImobFiscal

**PI 2 · FATEC DSM 2026-2**

---

## DER — Diagrama Entidade-Relacionamento

```mermaid
erDiagram
    USUARIOS {
        bigint id PK
        varchar email
        varchar senha
        timestamp criado_em
    }

    LOCADORES {
        bigint id PK
        varchar nome
        varchar cpf
        varchar cnpj
        varchar regime_tributario
        timestamp criado_em
        timestamp deleted_at
    }

    IMOVEIS {
        bigint id PK
        varchar endereco
        varchar tipo_uso
        numeric valor_venal
        bigint locador_id FK
        timestamp criado_em
        timestamp deleted_at
    }

    CONTRATOS_LOCACAO {
        bigint id PK
        varchar locatario_nome
        numeric valor_aluguel
        date data_inicio
        smallint dia_vencimento
        bigint locador_id FK
        bigint imovel_id FK
        timestamp criado_em
        timestamp deleted_at
    }

    NOTAS_FISCAIS {
        bigint id PK
        date data_emissao
        numeric valor_bruto
        numeric valor_liquido
        numeric aliquota_ibs
        numeric aliquota_cbs
        bigint contrato_id FK
        timestamp criado_em
        timestamp deleted_at
    }

    LOCADORES ||--o{ IMOVEIS : "possui"
    LOCADORES ||--o{ CONTRATOS_LOCACAO : "celebra"
    IMOVEIS ||--o{ CONTRATOS_LOCACAO : "objeto de"
    CONTRATOS_LOCACAO ||--o{ NOTAS_FISCAIS : "gera"
```

---

## Arquivos

| Arquivo | Descrição |
|---|---|
| `schema.sql` | DDL — criação de todas as tabelas |
| `seed.sql` | Dados fictícios para demonstração |

## Como executar

```bash
# 1. Criar o banco
psql -U postgres -c "CREATE DATABASE imobfiscal;"

# 2. Criar as tabelas
psql -U postgres -d imobfiscal -f database/schema.sql

# 3. Inserir dados de exemplo
psql -U postgres -d imobfiscal -f database/seed.sql
```

## Observações

- `deleted_at`: todas as tabelas de negócio utilizam exclusão lógica. Registros com este campo preenchido são ignorados nas listagens (`WHERE deleted_at IS NULL`).
- `aliquota_ibs` e `aliquota_cbs`: valores decimais — ex: `0.0875` = 8,75%. Alíquotas ilustrativas conforme LC 214/2025.
- Chaves primárias: `BIGSERIAL` (auto-incremento).
- Chaves estrangeiras: declaradas com `REFERENCES` para garantir integridade referencial.
