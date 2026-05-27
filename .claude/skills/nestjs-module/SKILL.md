---
name: nestjs-module
description: Use ao criar módulo NestJS novo ou adicionar endpoints a
             módulo existente. Triggers: "criar módulo", "novo endpoint",
             "adicionar rota", "criar service", "criar controller",
             "novo recurso da API".
version: 1.0
owner: AI Architect de Produto
last_updated: 2026-05-01
---

Cada módulo é uma unidade isolada. Módulo A nunca importa o Repository
do Módulo B — apenas o Service. Toda query filtra por `imobiliaria_id`.
Todo endpoint tem validação Zod + Swagger decorator. Sem exceções.

## Pré-condições

- [ ] Módulo não existe ainda (verificar apps/api/src/modules/)
- [ ] Migration da tabela correspondente já foi aplicada
- [ ] DTO de input definido (se feature nova)

## Processo

1. Criar estrutura em `apps/api/src/modules/[nome]/`
2. Criar: module, controller, service, repository, dto/
3. Adicionar `@ApiTags`, `@ApiOperation`, `@ApiResponse` em todo endpoint
4. Validar body com `class-validator` + `@ApiProperty` no DTO
5. Garantir que todo método do repository filtra por `imobiliaria_id`
6. Registrar o módulo no `AppModule`
7. Criar testes em `__tests__/`
8. Confirmar que Swagger está correto em `/api/docs`

## Estrutura obrigatória

```
modules/[nome]/
├── [nome].module.ts
├── [nome].controller.ts      ← @ApiTags('[nome]'), @ApiBearerAuth() em todos
├── [nome].service.ts         ← lógica de negócio
├── [nome].repository.ts      ← Prisma — só este módulo acessa seus dados
├── dto/
│   ├── create-[nome].dto.ts  ← @IsString(), @ApiProperty() em cada campo
│   ├── update-[nome].dto.ts  ← extends PartialType(Create...)
│   └── [nome]-response.dto.ts
└── __tests__/
    ├── [nome].service.spec.ts
    └── [nome].controller.spec.ts
```

## Regras absolutas

- NUNCA importar Repository de outro módulo — usar o Service
- TODA query do repository: `WHERE imobiliaria_id = ?` obrigatório
- TODO endpoint público precisa de aprovação explícita (anomalia)
- NENHUM `req.body` sem validação de schema

## Exemplo correto

```typescript
// [nome].repository.ts
async findAll(imobiliariaId: string): Promise<Imovel[]> {
  return this.prisma.imovel.findMany({
    where: {
      imobiliaria_id: imobiliariaId,  // ← obrigatório
      deleted_at: null,                // ← soft delete
    }
  })
}
```

## Testes obrigatórios

- [ ] Service: CRUD com mock do repository
- [ ] Service: erro 403 quando tenant não autorizado
- [ ] Controller: 201 no create com body válido
- [ ] Controller: 400 com body inválido (campo obrigatório ausente)
- [ ] Controller: 401 sem Bearer token
