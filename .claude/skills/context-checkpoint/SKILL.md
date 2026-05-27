---
name: context-checkpoint
description: Use quando o contexto atingir 50-60% da janela, antes de
             qualquer /clear, ao final de toda sessão de trabalho, ou
             ao detectar qualquer sinal de degradação do agente.
             Triggers: "salvar progresso", "checkpoint", "/clear",
             "nova sessão", sessão com mais de 2h de trabalho.
version: 1.0
owner: Todos os AI Architects
last_updated: 2026-05-01
---

A janela de contexto tem 200k tokens. A qualidade degrada a partir de
40-60% de uso. A auto-compactação em ~83,5% é destrutiva — retém apenas
20-30% dos detalhes. Checkpoint antes de /clear é a diferença entre
continuar produtivo e perder decisões críticas. /clear é grátis.
Retrabalho não é.

## Sinais de degradação (agir imediatamente)

| Sinal | Urgência |
|---|---|
| Agente "esqueceu" decisão recente | IMEDIATO |
| Resposta vaga onde antes era precisa | IMEDIATO |
| Sugestão contrária ao CLAUDE.md | IMEDIATO |
| Agente declara concluído sem rodar testes | Parar + testar |
| Sessão > 2h contínuas | PREVENTIVO |
| Contexto estimado > 50% | PREVENTIVO |

## Processo

1. Criar/atualizar `.claude/checkpoints/YYYY-MM-DD-[feature].md`
2. Preencher o template abaixo completamente
3. Salvar qualquer código em progresso no arquivo
4. `git add .claude/checkpoints/ && git commit -m "chore: checkpoint [feature]"`
5. Executar `/clear`
6. Nova sessão: começar lendo o checkpoint

## Template de checkpoint

```markdown
# Checkpoint: [nome] — branch: feat/[xxx]
## [YYYY-MM-DD HH:MM] — Contexto: ~[XX]%

### Concluído
- [item 1]
- [item 2]

### Decisões tomadas (crítico — não perder)
- [decisão]: [por quê]
- [decisão]: [por quê]

### Estado atual
- Testes: [passando / X falhando: listar]
- Migrations pendentes: [nenhuma / listar]
- TODOs no código: [listar path:linha]

### Próximos 3 passos
1. [ação concreta e verificável]
2. [ação concreta e verificável]
3. [ação concreta e verificável]

### Trecho de código em foco
Arquivo: [path]
```typescript
[código parcial relevante ou referência ao ponto exato]
```
```

## Regras absolutas

- NUNCA fazer /clear sem checkpoint salvo e comitado
- NUNCA confiar em "o agente vai lembrar" — escrever no checkpoint
- Checkpoints de features fiscais: copiar os valores de teste que estavam passando
