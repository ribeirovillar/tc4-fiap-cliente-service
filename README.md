
# FIAP Cliente Service

API REST para gerenciamento de clientes, baseada em Spring Boot e Clean Architecture.

---

## 🚀 Tecnologias

- **Java 24**
- **Spring Boot 3.5.3**
- **PostgreSQL**
- **Flyway** (migrações SQL)
- **MapStruct** (DTO ↔ Domínio ↔ Persistência)
- **Lombok**
- **Arquitetura Limpa (Clean Architecture)**

---

## 📋 Endpoints REST

| Método | Endpoint             | Descrição                |
|--------|----------------------|--------------------------|
| GET    | `/customers`         | Listar todos os clientes |
| GET    | `/customers/{id}`    | Buscar cliente por ID    |
| POST   | `/customers`         | Criar novo cliente       |
| PUT    | `/customers/{id}`    | Atualizar cliente        |
| DELETE | `/customers/{id}`    | Remover cliente          |

### Exemplo de criação

```json
POST /customers
{
  "fullName": "Demostenis Villar",
  "cpf": "06847362754",
  "birthDate": "1985-09-26",
  "address": {
    "street": "Rua Artur",
    "number": "78",
    "city": "Recife",
    "state": "Pernambuco",
    "zipCode": "20715-364"
  }
}
```

---

## 🏃 Como Rodar

### 1. Docker Compose (App + Banco)

```bash
docker-compose up --build
```
- API: http://localhost:8080/customers
- PostgreSQL: localhost:5432

### 2. Local com Maven

1. Suba o banco:
   ```bash
   docker-compose up -d postgres
   ```
2. Rode a aplicação:
   ```bash
   ./mvnw spring-boot:run
   ```
3. API Swagger/OpenAPI: http://localhost:8080/swagger-ui.html

---

## 🧪 Testes & Cobertura

- Testes automatizados:
  ```bash
  ./mvnw clean verify
  ```
- Relatório Jacoco:
  ```
  target/site/jacoco/index.html
  ```


---

## 🗄️ Banco de Dados

- **Desenvolvimento:** PostgreSQL (porta 5432)
- **Migrações automáticas:** via Flyway (`src/main/resources/db/migration`)

---

## 🧱 Estrutura de Pastas

- `domain`      - modelo rico do domínio (Customer, Address)
- `usecase`     - casos de uso da aplicação
- `gateway`     - interface e implementação (database, etc)
- `controller`  - REST API (entrada/saída)
- `mapper`      - conversores (DTO ↔ domínio ↔ persistência)
- `infra`       - configurações globais, handlers, OpenAPI
- `exception`   - tratamento global de exceções

