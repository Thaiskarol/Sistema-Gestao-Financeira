# Padrões de Código e Organização

## Estrutura de pacotes (base: com.bradesco)
- `controller`  — endpoints REST
- `service`     — regras de negócio
- `repository`  — acesso a dados
- `entity`      — entidades JPA
- `dto.request` — objetos de entrada
- `dto.response`— objetos de saída
- `enums`       — enumerações (ex.: TipoTransacao)
- `converter`   — conversores JPA
- `exception`   — exceções e handler global
- `config`      — configurações (ex.: OpenAPI)

## Convenções de código
- Indentação: 4 espaços em todos os arquivos.
- Nomes de classes em PascalCase; métodos e variáveis em camelCase.
- DTOs são `record`.
- Entidades em português, alinhadas às tabelas.
- Tratamento de erros: sempre usar exceções específicas (ex.: ResourceNotFoundException), nunca RuntimeException genérica.
- Toda regra de negócio fica no Service, nunca no Controller.
