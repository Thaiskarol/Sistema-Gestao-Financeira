# Padrão de Respostas da API

## Sucesso
Formato `ApiResponse`:
{ "success": true, "message": "texto", "data": { ... } }

## Erro
Formato `ErrorResponse`:
{ "success": false, "message": "texto", "data": null, "errors": [ { "field": "campo", "message": "detalhe" } ] }

## Códigos HTTP utilizados
- 201 Created — recurso criado com sucesso (ex.: cadastro de transação)
- 400 Bad Request — erro de validação de entrada
- 404 Not Found — recurso inexistente (usuário/categoria não encontrados)
- 500 Internal Server Error — erro inesperado
