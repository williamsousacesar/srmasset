# SRM Asset — Credit Engine

Case Técnico SRM Asset: sistema de gestão de recebíveis com cadastro de cedentes, precificação (cálculo de valor presente), liquidação com conversão de moeda e extrato de operações.

## Tecnologias

**Backend**
- Java 21 / Spring Boot 4
- Spring Web, Spring Data JPA, Bean Validation
- MySQL 8 (produção via Docker) e H2 (desenvolvimento)
- SpringDoc OpenAPI (Swagger)
- Lombok

**Frontend**
- Angular 18 (standalone components)
- Nginx (build de produção, com proxy `/api` para o backend)

**Infraestrutura**
- Docker e Docker Compose

## Arquitetura

O backend segue uma arquitetura em camadas:

```
src/main/java/br/com/srm/srmcreditengine
├── presentation/    # Controllers, DTOs e tratamento global de erros
├── business/        # Serviços, regras de negócio e estratégias de precificação
├── persistence/     # Entidades JPA, enums e repositórios
├── relatorio/       # Extrato de liquidações (controller, DTOs, repositório)
└── config/          # CORS, OpenAPI e carga de dados inicial
```

A precificação usa o padrão **Strategy** (`EstrategiaDuplicataMercantil`, `EstrategiaChequePreDatado`) para calcular o valor presente conforme o tipo do recebível.

O frontend fica em `frontend/` com telas de Cedentes, Recebíveis, Liquidações, Câmbio e Extrato.

## Como executar

### Com Docker (recomendado)

Na raiz do projeto:

```bash
docker compose up -d --build
```

Serviços disponíveis:

| Serviço  | URL                                            |
|----------|------------------------------------------------|
| Frontend | http://localhost:4300                          |
| API      | http://localhost:8080                          |
| Swagger  | http://localhost:8080/swagger-ui/index.html    |
| MySQL    | localhost:3306 (usuário/senha: `srmasset`)     |

Para parar:

```bash
docker compose down
```

Os dados do banco são mantidos no volume `mysql_data`.

### Desenvolvimento local

**Backend** (requer Java 21 e o MySQL do compose rodando):

```bash
docker compose up -d db
./mvnw spring-boot:run
```

**Frontend** (requer Node 20):

```bash
cd frontend
npm install
npm start
```

O `ng serve` usa `proxy.conf.json` para redirecionar `/api` ao backend em `localhost:8080`.

## Endpoints da API

### Cedentes — `/api/cedentes`
| Método | Rota           | Descrição                    |
|--------|----------------|------------------------------|
| POST   | `/cadastrar`   | Cadastra um cedente          |
| GET    | `/listar-tudo` | Lista todos os cedentes      |
| GET    | `/{id}`        | Busca um cedente pelo id     |

### Recebíveis — `/api/recebiveis`
| Método | Rota           | Descrição                                        |
|--------|----------------|--------------------------------------------------|
| POST   | `/cadastrar`   | Cadastra um recebível (status inicial: pendente) |
| GET    | `/listar-tudo` | Lista todos os recebíveis                        |
| GET    | `/{id}`        | Busca um recebível pelo id                       |

### Liquidações — `/api/liquidacoes`
| Método | Rota                        | Descrição                                     |
|--------|-----------------------------|-----------------------------------------------|
| POST   | `/liquidar`                 | Liquida um recebível na moeda informada       |
| GET    | `/recebivel/{recebivelId}`  | Consulta a liquidação de um recebível         |

### Câmbio — `/api/cambio`
| Método | Rota                                   | Descrição                          |
|--------|----------------------------------------|------------------------------------|
| GET    | `/taxas/listar-tudo`                   | Lista todas as taxas de câmbio     |
| GET    | `/taxas/{moedaOrigem}/{moedaDestino}`  | Consulta a taxa entre duas moedas  |
| PUT    | `/taxas`                               | Atualiza uma taxa de câmbio        |

### Relatórios — `/api/relatorios`
| Método | Rota                  | Descrição                              |
|--------|-----------------------|----------------------------------------|
| GET    | `/extrato-liquidacao` | Extrato de liquidações (com filtros)   |

A documentação completa e interativa está disponível no **Swagger**: http://localhost:8080/swagger-ui/index.html

## Regras de negócio

- O **valor presente** de um recebível é calculado no cadastro conforme sua estratégia de precificação (duplicata mercantil ou cheque pré-datado), com taxa base mensal configurável (`srm.precificacao.taxa-base-mensal`, padrão 1% a.m.).
- Um recebível **já liquidado não pode ser liquidado novamente** (`RecebivelJaLiquidadoException`).
- Na liquidação, o valor é **convertido para a moeda de pagamento** usando a taxa de câmbio cadastrada.
- Erros de negócio e recursos não encontrados são tratados pelo `GlobalExceptionHandler`, com respostas padronizadas (`ErroRespostaDTO`) e handler genérico retornando 500 controlado para erros inesperados.

## Testes

```bash
./mvnw test
```
