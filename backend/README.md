# Sistema de Gestão Financeira — Backend

Aplicação Spring Boot (Java 21, Maven) para gestão financeira pessoal.

## Tecnologias
- Java 21
- Spring Boot 4.1.1 (Web, Data JPA, Validation)
- PostgreSQL
- Flyway (migrations)
- springdoc-openapi (Swagger)
- JUnit 5 + Mockito (testes)

## Como rodar localmente
1. Tenha o PostgreSQL rodando e um banco chamado `financas`.
2. Configure as variáveis de ambiente (veja `.env.example`): DB_URL, DB_USER, DB_PASSWORD.
3. Na pasta `backend`, rode: `./mvnw spring-boot:run`
4. A documentação da API fica em: http://localhost:8080/swagger-ui.html

## Convenção de branches
- `main`: versão estável.
- `william-branch`: integração do backend.
- Cada pessoa trabalha em branch própria e abre Pull Request. Nenhum PR entra sem o CI verde.
