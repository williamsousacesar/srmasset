# AI_USAGE.md — Uso de IA no Desenvolvimento

Este documento descreve como a IA (GitHub Copilot CLI) foi utilizada durante o
desenvolvimento do **SRM Credit Engine**, conforme a Política de Uso de IA do
desafio (seção 2).

---

## 1. Prompts Estratégicos Utilizados

### Geração de massa de dados
> *"Gere uma carga inicial de cedentes, recebíveis e taxas de câmbio para
> testes de API."*

Resultou na classe `CargaDeDadosInicial`, com dados coerentes com o domínio
(duplicatas e cheques em BRL/USD).

### Diagnóstico de infraestrutura (Docker)
> *"Avalia o log a seguir e me ajuda a encontrar o erro ao subir o projeto no
> Docker: [stack trace do HikariCP/MySQL]"*

Usei a IA como par de debugging para erros de conexão entre o container da
aplicação e o MySQL, colando os logs reais do `docker compose up`.

### Revisão de design e nomenclatura
> *"MotorDeCalculoVlPresente é um nome bom para a classe?"*
> *"Por que o pacote strategy fica dentro de business?"*
> *"A classe ProvedorDeTaxaBase realmente precisa ficar na service?"*

Usei a IA como revisor de design: nomenclatura de classes (resultando em
`CalculoValorPresenteService`), organização de pacotes por camada e
posicionamento de componentes. As decisões finais foram minhas, mas o debate
com a ferramenta refinou a estrutura.

### IA como mentor técnico
> *"O que significa `new MathContext(12)`?"*
> *"3.33333333333 tem 11 casas depois da vírgula, não seriam 12?"*
> *"De onde vem a chamada de `estrategiasPorTipo`?"*

Além de gerar código, usei a IA para **garantir domínio do que foi gerado**:
precisão significativa vs. casas decimais em `BigDecimal`, injeção de
`List<EstrategiaDePrecificacao>` pelo Spring, uso de `Map` por tipo. Isso
atende diretamente a diretriz de autoria intelectual do desafio.

### Navegação e auditoria de código
> *"Pelas minhas configurações do docker-compose e demais configurações,
> terei algum problema ao subir o projeto no Docker?"*

Usei a IA para validação preventiva de configuração antes do deploy local.

---

## 2. Alucinações e Código Inseguro — Correções

### Alteração de contrato não solicitada
Ao gerar o `ExtratoLiquidacaoController`, a IA **removeu a obrigatoriedade dos
filtros de pesquisa** sem que isso tivesse sido pedido, mudando silenciosamente
o contrato da API. Detectei ao revisar o código, questionei a alteração e
redefini explicitamente quais filtros seriam opcionais (período, cedente,
moeda) como decisão minha, não da ferramenta.

**Lição:** a IA tende a "melhorar" contratos por conta própria; todo diff
precisa ser lido, não apenas testado.

### Configuração de banco inconsistente no Docker
A configuração inicial gerada para `docker-compose.yml` e `application.yaml`
tinha **credenciais/host inconsistentes entre os serviços**, causando falha de
autenticação do usuário `srmasset` no MySQL (erro visível apenas em runtime,
no container). Corrigi alinhando variáveis de ambiente do compose com o
datasource do Spring (commit `f3a0d3d`).

**Lição:** a IA gera cada arquivo corretamente de forma isolada, mas erra na
consistência *entre* arquivos de configuração — validação de integração é
indispensável.

### Segurança de credenciais
Versões iniciais sugeridas pela IA colocavam credenciais fixas no
`application.yaml`. Ajustei para uso de variáveis de ambiente no
Docker Compose, evitando segredos hardcoded no código versionado.

### Código morto gerado
A IA criou a classe `ProvedorDeTaxaBase` que, em determinado momento, **não
era utilizada em nenhum ponto do sistema**. Detectei ao questionar o propósito
da classe e só a mantive após integrá-la de fato ao cálculo de valor presente.

**Lição:** a IA gera componentes "por precaução" que viram código morto se
não forem validados.

### Alterações além do escopo pedido
Ao pedir a configuração do H2, a IA também **modificou outras chaves do
`application.yaml`** por iniciativa própria. Solicitei a reversão imediata da
alteração e reapliquei apenas o trecho desejado.

**Lição:** pedir mudanças pequenas e revisar o diff completo antes de aceitar
— nunca deixar a IA editar arquivos de configuração sem conferência.

---

## 3. Análise Crítica

### Onde a IA economizou tempo
- **Scaffolding em camadas:** gerar controllers, DTOs, services e repositories
  seguindo o padrão do projeto reduziu drasticamente o tempo de código
  repetitivo (boilerplate), liberando foco para as regras de precificação.
- **Debugging de infraestrutura:** interpretar stack traces do
  HikariCP/MySQL nos logs do Docker foi muito mais rápido com a IA do que
  pesquisando erro por erro.
- **Documentação:** README, diagrama ER e scripts DDL saíram em fração do
  tempo, sobrando energia para revisar conteúdo em vez de escrever do zero.
- **Aprendizado dirigido:** perguntas conceituais transformaram a geração de
  código em oportunidade de domínio real do que foi entregue.

### Onde a IA atrapalhou
- **Mudanças silenciosas de escopo:** o caso da obrigatoriedade dos filtros
  mostrou que a IA altera comportamento sem sinalizar — exigiu revisão linha a
  linha de cada geração.
- **Configuração distribuída:** os erros de conexão no Docker custaram um
  ciclo de debug que não existiria se a configuração tivesse sido escrita
  manualmente com atenção desde o início.
- **Edições além do pedido:** ao alterar arquivos de configuração, a IA
  modificou trechos não solicitados, exigindo reversão e reaplicação
  controlada.
- **Excesso de confiança no "parece pronto":** código gerado compila e parece
  correto, mas detalhes de domínio financeiro (arredondamento, precisão
  decimal com `BigDecimal`, campos de auditoria como `data_inclusao`)
  precisaram ser especificados e conferidos por mim.

### Conclusão
A IA funcionou como **acelerador de execução, não como substituto de
decisão**: as escolhas de arquitetura (3 camadas + módulo de relatório em 2,
Strategy para spreads, SQL nativo para o extrato, optimistic locking) foram
minhas; a IA materializou e documentou essas decisões mais rápido. O custo foi
a disciplina obrigatória de revisar todo output — que considero o preço justo
pelo ganho de velocidade.
