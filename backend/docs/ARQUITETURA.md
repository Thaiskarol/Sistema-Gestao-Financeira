# Arquitetura do Backend

## Arquitetura em camadas
O backend segue arquitetura em camadas. Cada camada tem uma responsabilidade única:

- **Controller**: recebe as requisições HTTP, valida a entrada e devolve a resposta. Não contém regra de negócio.
- **Service**: contém as regras de negócio do sistema (ex.: só cadastrar transação se usuário e categoria existirem).
- **Repository**: acesso ao banco de dados via Spring Data JPA.
- **Entity**: representa as tabelas do banco.
- **DTO**: objetos de entrada (request) e saída (response), separando a API do modelo interno.
- **Exception**: tratamento centralizado de erros.
- **Converter / Enums**: apoio ao mapeamento de dados (ex.: TipoTransacao).

## Separação entre negócio e infraestrutura
- **Regras do sistema (negócio)**: validação de existência de usuário e categoria, regras de transação, tipos permitidos (RECEITA/DESPESA). Ficam na camada Service.
- **Implementação técnica (infraestrutura)**: conexão com PostgreSQL, JPA/Hibernate, Flyway, configuração do Spring, serialização JSON. Ficam em configuração e nas camadas de Repository/Entity.

Essa separação permite trocar detalhes técnicos (ex.: banco) sem reescrever as regras de negócio.
