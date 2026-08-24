# ADR — MySQL vs PostgreSQL

- **Status:** Aceito
- **Data:** 2026-08-22
- **Decisores:** Equipe de engenharia

## Contexto

Os dois candidatos maduros para trabalhar com modelo relacional e open source são o **MySQL 8** e **PostgreSQL 16**. As
necessidades do Credit Engine são: transações ACID, `DECIMAL` para valores
monetários, integridade referencial, boa integração com Spring Data
JPA/Hibernate e operação simples via Docker.

### Comparativo

| Critério | MySQL 8 | PostgreSQL 16 |
|---|---|---|
| ACID / transações | ✅  | ✅ |
| Tipos monetários (`DECIMAL`) | ✅ | ✅ |
| Suporte Hibernate/JPA | Excelente | Excelente |
| Leituras simples por PK (workload dominante do sistema) | Muito rápido; índice clusterizado por PK favorece lookups | Rápido |
| Recursos avançados (JSONB, CTEs ricas, tipos custom, particionamento declarativo) | Parcial | ✅ Superior |
| Concorrência de escrita alta / MVCC | Bom | Excelente (sem locks de leitura; porém *vacuum* exige tuning) |
| Replicação | Simples e madura (binlog, GTID) | Madura (streaming, logical) |
| Caminho de sharding | **Vitess** (nativo p/ MySQL, batalha-testado) | Citus |
| Familiaridade do time / stack pedida no case | ✅ | Menor |
| Operação em Docker (dev) | Simples | Simples |

## Decisão

**MySQL 8** com engine InnoDB.

Motivos determinantes:

1. **Adequação ao workload**: o sistema é dominado por lookups por chave
   (recebível por id, taxa por par de moedas) e escritas transacionais
   pontuais — perfil em que o índice clusterizado do InnoDB brilha; não
   usamos os diferenciais do Postgres (JSONB, tipos custom, full-text);
2. **Caminho de escala já traçado**: o plano de alta escala (README) poe prevê
   sharding via **Vitess**, ecossistema consolidado sobre MySQL (YouTube,
   Slack, GitHub);
3. **Simplicidade operacional**: replicação por GTID é simples de operar e
   suficiente para réplicas de leitura;
4. **Familiaridade do time e aderência ao stack do case**, reduzindo risco
   de entrega;
5. Ambos atendem plenamente os requisitos funcionais — o desempate é
   operacional/estratégico, não técnico-funcional.

## Consequências

- ✅ Entrega rápida com stack conhecido; caminho claro para réplicas e
  sharding (Vitess).
- ⚠️ Abrimos mão de recursos avançados do Postgres (JSONB indexável,
  `EXCLUDE` constraints, particionamento mais rico). Se surgirem requisitos
  de documentos semiestruturados ou analytics no transacional, reavaliar.
- ⚠️ Atenção a diferenças de comportamento do MySQL: *collation* e
  case-insensitivity padrão, `TIMESTAMP` com fuso, isolamento padrão
  `REPEATABLE READ` (vs `READ COMMITTED` do Postgres) — mitigado por testes
  de integração contra MySQL real (Docker), não apenas H2.
- 🔄 A camada JPA/Hibernate mantém o custo de uma eventual migração para
  PostgreSQL baixo (SQL portável, sem features proprietárias no código).
