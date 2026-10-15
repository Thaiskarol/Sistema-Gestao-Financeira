# Dependências entre Módulos

- Uma transação depende de um **usuário** e de uma **categoria** existentes.
- Portanto, usuário e categoria precisam estar disponíveis no banco antes de cadastrar transações.
- No estágio atual, usuários e categorias são inseridos diretamente no banco (via migration/seed) até que os Epics de usuário e categoria sejam implementados.
- Decisão: o Epic de transações assume que já existem registros válidos de usuário e categoria para referência.
