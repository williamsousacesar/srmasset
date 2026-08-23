# Arquitetura Baseada em Eventos (EDA) — Como os Sistemas Conversam sem Travar

Proposta de evolução do sistema para funcionar através de **comunicação por eventos**. Em vez de um serviço esperar o outro responder para continuar trabalhando, cada ação gera uma "notificação" (evento) que avança o processo automaticamente.

---

## 1. Principais Objetivos

- **Zero Espera (Desacoplamento):** O sistema aceita o pedido do cliente imediatamente e processa o pagamento em segundo plano.
- **Suporte a Picos Extremos:** Filas de mensagens funcionam como amortecedores durante picos de até 1 milhão de operações/minuto.
- **Novos Recursos sem Complicação:** Facilita adicionar funções (como envio de e-mails, relatórios ou análise de fraude) sem precisar mexer no fluxo principal do sistema.
- **Histórico Completo:** Registra tudo o que acontece no sistema de forma permanente, criando uma trilha automática para auditorias.

---

## 2. Principais Eventos do Sistema

| Ação / Evento | Quem Dispara? | Quem Ouve / Consome? | Para que serve? |
| :--- | :--- | :--- | :--- |
| **Cliente Cadastrado** | Sistema de Cadastro | Relatórios, Área de Compliance | Informa que um novo cliente entrou na plataforma. |
| **Pagamento Solicitado** | API de Pagamentos | Processador Principal | Avisa que um novo pagamento precisa ser processado. |
| **Pagamento Concluído** | Processador Principal | Notificações, Contabilidade, Relatórios | Confirma que o dinheiro caiu e dispara avisos e registros. |
| **Pagamento Recusado** | Processador Principal | Notificações / E-mail | Avisa ao cliente o motivo da recusa (ex: saldo insuficiente). |
| **Taxa de Câmbio Atualizada** | Serviço de Câmbio | Sistema de Pagamentos | Atualiza os valores das moedas para os próximos cálculos. |

---

## 3. Como a Informação Trafega (Fluxo Simplificado)

```
[ Cliente ] ──1. Pede pagamento──▶ [ API Principal ] ──3. Salva pedido──▶ [ Fila Kafka ]
     ▲                                    │                                  │
     │                                    │                                  │ 4. Pega pedido
     └──────2. Responde: "Aceito!"────────┘                                  ▼
             (202 Accepted)                                         [ Processador ]
                                                                             │
                                                                             │ 5. Grava e avisa
                                                                             ▼
                                                                    ┌─────────────────┐
                                                                    │ Notificações &  │
                                                                    │   Relatórios    │
                                                                    └─────────────────┘
```

* **Fila Principal (Kafka):** Funciona como uma esteira rolante bem organizada que garante que as mensagens cheguem na ordem certa.
* **Envio Garantido (Outbox Pattern):** O pedido e a notificação são salvos juntos no banco de dados na mesma fração de segundo. Isso garante que a mensagem nunca se perca, mesmo que ocorra uma queda de energia ou oscilação de rede.

---

## 4. Cuidados e Soluções Práticas

| Problema Possível | Como Resolver? |
| :--- | :--- |
| **Mensagem Processada Duas Vezes** | **Bloqueio de Duplicados:** Cada pedido tem um código único. Se a mesma mensagem chegar duas vezes, a segunda é descartada automaticamente. |
| **Ordem das Operações** | **Organização por Cliente:** Todas as ações do mesmo cliente entram na mesma "esteira", garantindo que a sequência de transações não se embaralhe. |
| **Falha de Processamento** | **Fila de Correção (DLQ):** Se um pedido falhar após várias tentativas, ele é movido para uma fila de análise técnica sem travar o restante do sistema. |
| **Tempo de Atualização do Cliente** | **Status Intermediário:** O pedido ganha o status `Em Processamento`. O cliente acompanha o progresso pela tela ou recebe um aviso por Webhook assim que finalizar. |

---

## 5. Etapas para Implementar Sem Riscos

1. **Fase 1 (Segura):** Continuar processando tudo como é hoje, mas começar a salvar o histórico de eventos no banco de dados.
2. **Fase 2 (Relatórios Leves):** Usar os eventos para atualizar relatórios e extratos em segundo plano.
3. **Fase 3 (Ganhos de Velocidade):** Liberar o processamento assíncrono para o cliente (responder "Aceito" em milissegundos).
4. **Fase 4 (Independência Total):** Separar os processadores pesados em servidores independentes que escalam sozinhos.
