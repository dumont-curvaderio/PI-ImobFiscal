---
name: pr-review-fiscal
description: Use ao revisar Pull Request que toca código fiscal.
             Triggers: "revisar PR", "code review fiscal", qualquer PR
             que modifique packages/core/fiscal/ ou módulo fiscal da API.
             Este skill substitui o checklist padrão nesses casos.
version: 1.0
owner: AI Architect Fiscal
last_updated: 2026-05-01
---

PRs fiscais têm o maior risco do produto. Um erro que passa pelo review
chega a centenas de clientes e pode gerar multas reais. Na dúvida, não
aprovar — sempre pedir explicação da lógica antes de dar LGTM.

## Checklist de segurança fiscal

```bash
# Rodar antes de qualquer review
pnpm test --filter=@imobfiscal/core
pnpm typecheck
# Buscar hardcodes de alíquotas (qualquer número com 3+ decimais em /core/)
grep -rn "0\.[0-9]\{3,\}" packages/core/fiscal/
```

### Segurança fiscal (bloquear se qualquer item falhar)
- [ ] Nenhuma alíquota hardcoded detectada pelo grep acima
- [ ] Toda RN implementada tem comentário `// RN-XXX` na linha
- [ ] Cada isenção possível tem seu próprio teste
- [ ] Valor exato no limite testado (ex: 2500.00 E 2500.01)
- [ ] `memoriaCalculo` presente e não vazio no resultado

### Qualidade do código
- [ ] Arquivo em packages/core/fiscal/ (não em apps/)
- [ ] Sem import de Prisma, NestJS, Supabase em /core/
- [ ] Tipos explícitos em input e output (nenhum `any`)
- [ ] Coverage ≥ 95% no arquivo modificado

### Documentação
- [ ] knowledge-base-fiscal.md atualizado se regra mudou
- [ ] CLAUDE.md atualizado se novo padrão foi introduzido

## Critérios de bloqueio absoluto

Nunca aprovar com qualquer um destes:

1. Alíquota hardcoded detectada
2. Teste faltando para qualquer isenção do sistema
3. Import de framework dentro de /core/fiscal/
4. Campo financeiro retornando `any`
5. Ausência de `memoriaCalculo` em resultado de cálculo

## Comentário de review para hardcode detectado

```
⛔ Alíquota hardcoded detectada na linha X.
Alíquotas NUNCA podem ser hardcoded — devem vir de parâmetro externo
ou da tabela `aliquotas_vigentes`. A alíquota muda a cada ano da
transição 2026-2033. Se hardcodar hoje, quebrará em jan/2027.

Ver: docs/knowledge-base-fiscal.md, regra de codificação #1.
```
