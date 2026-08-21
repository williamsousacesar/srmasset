# SRM Asset — Credit Engine

> Case Técnico SRM Asset: sistema de gestão de recebíveis com cadastro de cedentes, precificação por valor presente, liquidação com conversão de moeda e extrato de operações.

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0-brightgreen?logo=springboot&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-18-red?logo=angular&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)

---

## Sumário

- [Visão Geral](#visão-geral)
- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Como Executar](#como-executar)
- [Endpoints da API](#endpoints-da-api)
- [Exemplos de Uso](#exemplos-de-uso)
- [Regras de Negócio](#regras-de-negócio)
- [Dados de Exemplo](#dados-de-exemplo)
- [Testes](#testes)

---

## Visão Geral

O **Credit Engine** simula o motor de crédito de uma securitizadora:

1. **Cedentes** (empresas que vendem seus títulos) são cadastrados;
2. **Recebíveis** (duplicatas mercantis ou cheques pré-datados) são registrados e **precificados automaticamente** — o sistema calcula o valor presente aplicando desconto composto conforme o tipo do título;
3. Recebíveis pendentes podem ser **liquidados** em qualquer moeda suportada (BRL, USD, EUR), com **conversão automática** pela taxa de câmbio vigente;
4. Todas as operações ficam disponíveis em um **extrato de liquidações** com filtros.

O sistema possui um **frontend Angular** com telas para todas as operações e documentação interativa via **Swagger**.

## Tecnologias

| Camada | Stack |
|---|---|
| **Backend** | Java 21 · Spring Boot 4 · Spring Web · Spring Data JPA · Bean Validation · Lombok |
| **Banco de dados** | MySQL 8 (Docker) · H2 (desenvolvimento) |
| **Documentação** | SpringDoc OpenAPI / Swagger UI |
| **Frontend** | Angular 18 (standalone components) · RxJS |
| **Servidor web** | Nginx (com proxy reverso `/api` → backend) |
| **Infraestrutura** | Docker · Docker Compose |

## Arquitetura

O backend segue **arquitetura em camadas** com separação clara de responsabilidades:

```
src/main/java/br/com/srm/srmcreditengine
├── presentation/        # Camada de apresentação
│   ├── controller/      #   REST controllers
│   ├── dto/             #   Records de requisição/resposta com validações
│   └── exceptionhandler/#   Tratamento global de erros (RFC de resposta padronizada)
├── business/            # Camada de negócio
│   ├── service/         #   Serviços (cedente, recebível, liquidação, câmbio, precificação)
│   ├── strategy/        #   Estratégias de precificação (padrão Strategy)
│   └── exception/       #   Exceções de negócio
├── persistence/         # Camada de persistência
│   ├── entities/        #   Entidades JPA (Cedente, Recebivel, Liquidacao, TaxaCambio)
│   ├── enums/           #   Moeda, StatusRecebivel, TipoRecebivel
│   └── repository/      #   Spring Data repositories
├── relatorio/           # Módulo de relatórios (extrato de liquidações)
└── config/              # CORS, OpenAPI e carga de dados inicial
```

### Padrão Strategy na precificação

Cada tipo de recebível tem sua própria estratégia com spread específico, permitindo adicionar novos tipos sem alterar o serviço de cálculo:

| Estratégia | Tipo | Spread mensal |
|---|---|---|
| `EstrategiaDuplicataMercantil` | `DUPLICATA_MERCANTIL` | 1,5% |
| `EstrategiaChequePreDatado` | `CHEQUE_PRE_DATADO` | 2,5% |

### Frontend

O app Angular (`frontend/`) usa componentes standalone com as telas **Cedentes**, **Recebíveis**, **Liquidações**, **Câmbio** e **Extrato**. Em produção é servido pelo Nginx, que também faz proxy das chamadas `/api` para o backend — evitando problemas de CORS.

### Containers

```
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│ angular_container│ ───▶ │  java_container │ ───▶ │ mysql_container │
│  Nginx :4300    │ /api │ Spring Boot :8080│ JDBC │   MySQL :3306   │
└─────────────────┘      └─────────────────┘      └─────────────────┘
                       rede: rede_comunicacao
```

## Como Executar

### Pré-requisitos

- [Docker](https://www.docker.com/) com Docker Compose (execução completa), **ou**
- Java 21 + Node 20 (desenvolvimento local)

### Com Docker (recomendado)

Na raiz do projeto:

```bash
docker compose up -d --build
```

| Serviço | URL |
|---|---|
| 🖥️ Frontend | http://localhost:4300 |
| 🔌 API | http://localhost:8080 |
| 📚 Swagger | http://localhost:8080/swagger-ui/index.html |
| 🗄️ MySQL | `localhost:3306` — usuário/senha: `srmasset` |

Comandos úteis:

```bash
docker compose logs -f app   # acompanhar logs do backend
docker compose ps            # status dos containers
docker compose down          # parar tudo (dados preservados no volume mysql_data)
```

### Desenvolvimento local

**Backend** — requer o MySQL do compose no ar:

```bash
docker compose up -d db
./mvnw spring-boot:run
```

**Frontend:**

```bash
cd frontend
npm install
npm start        # ng serve com proxy /api → localhost:8080
```

## Endpoints da API

### 🏢 Cedentes — `/api/cedentes`

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/cadastrar` | Cadastra um cedente |
| `GET` | `/listar-tudo` | Lista todos os cedentes |
| `GET` | `/{id}` | Busca um cedente pelo id |

### 📄 Recebíveis — `/api/recebiveis`

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/cadastrar` | Cadastra um recebível já precificado (status `PENDENTE`) |
| `GET` | `/listar-tudo` | Lista todos os recebíveis |
| `GET` | `/{id}` | Busca um recebível pelo id |

### 💰 Liquidações — `/api/liquidacoes`

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/liquidar` | Liquida um recebível na moeda informada |
| `GET` | `/recebivel/{recebivelId}` | Consulta a liquidação de um recebível |

### 💱 Câmbio — `/api/cambio`

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/taxas/listar-tudo` | Lista todas as taxas de câmbio |
| `GET` | `/taxas/{moedaOrigem}/{moedaDestino}` | Consulta a taxa entre duas moedas |
| `PUT` | `/taxas` | Atualiza uma taxa de câmbio |

### 📊 Relatórios — `/api/relatorios`

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/extrato-liquidacao` | Extrato de liquidações com filtros |

> 📚 A documentação completa e interativa está no **Swagger**: http://localhost:8080/swagger-ui/index.html

## Exemplos de Uso

**Cadastrar um recebível:**

```bash
curl -X POST http://localhost:8080/api/recebiveis/cadastrar \
  -H "Content-Type: application/json" \
  -d '{
    "cedenteId": 1,
    "tipo": "DUPLICATA_MERCANTIL",
    "valorFace": 10000.00,
    "moedaTitulo": "BRL",
    "prazoMeses": 3,
    "dataEmissao": "2026-08-21",
    "dataVencimento": "2026-11-21"
  }'
```

**Liquidar um recebível em dólar:**

```bash
curl -X POST http://localhost:8080/api/liquidacoes/liquidar \
  -H "Content-Type: application/json" \
  -d '{
    "recebivelId": 1,
    "moedaPagamento": "USD"
  }'
```

## Regras de Negócio

### 💵 Precificação (valor presente)

Calculado automaticamente no cadastro do recebível, por desconto composto:

```
                    valorFace
valorPresente = ─────────────────────
                (1 + i)^prazoMeses
```

Onde `i = taxaBaseMensal + spreadDoTipo`:

- **Taxa base mensal:** 1% a.m. — configurável em `srm.precificacao.taxa-base-mensal`
- **Spread:** 1,5% a.m. (duplicata mercantil) ou 2,5% a.m. (cheque pré-datado)

*Exemplo:* duplicata de R$ 10.000,00 com prazo de 3 meses → taxa total 2,5% a.m. → valor presente ≈ **R$ 9.285,99**.

### 🔒 Liquidação

- Um recebível **já liquidado não pode ser liquidado novamente** (`RecebivelJaLiquidadoException` → HTTP 409/422);
- Na liquidação, o valor presente é **convertido para a moeda de pagamento** pela taxa de câmbio cadastrada;
- São registradas `data_inclusao` (cadastro) e `data_ultima_alteracao` (liquidação) do recebível.

### ⚠️ Tratamento de erros

O `GlobalExceptionHandler` padroniza todas as respostas de erro (`ErroRespostaDTO`):

- **400** — validações de campos (Bean Validation);
- **404** — recurso não encontrado (`RecursoNaoEncontradoException`);
- **4xx** — violações de regra de negócio (`RegraDeNegocioException`, `RecebivelJaLiquidadoException`);
- **500** — handler genérico controlado para erros inesperados.

## Dados de Exemplo

Na primeira execução, a `CargaDeDadosInicial` popula o banco automaticamente:

- **Taxas de câmbio:** BRL → USD (0,185) e USD → BRL (5,40);
- **Cedente:** Comercial Exemplo Ltda (CNPJ 12.345.678/0001-90);
- **Recebíveis:** uma duplicata mercantil de R$ 10.000,00 (3 meses) e um cheque pré-datado de R$ 5.000,00 (2 meses), ambos pendentes.

## Testes

```bash
./mvnw test
```

Os testes unitários da camada de negócio ficam em `src/test/java/br/com/srm/srmcreditengine/business/`.

---

Desenvolvido como case técnico para a **SRM Asset**. 🚀
