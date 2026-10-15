# Contrato Inicial da API (Backend <-> Frontend)

## Endpoint disponível
POST /transacoes — cadastra uma transação.

### Corpo da requisição (JSON)
{
  "descricao": "string (obrigatório, até 350 caracteres)",
  "valor": "número positivo (obrigatório)",
  "dataTransacao": "data AAAA-MM-DD (obrigatório)",
  "recorrencia": "string (opcional)",
  "tipo": "RECEITA ou DESPESA (obrigatório)",
  "categoriaId": "número (obrigatório)",
  "usuarioId": "número (obrigatório)"
}

### Resposta de sucesso (201)
{ "success": true, "message": "Transação cadastrada com sucesso.", "data": { ...transação... } }

### Resposta de erro de validação (400)
{ "success": false, "message": "Existem dados inválidos na requisição.", "data": null, "errors": [ ... ] }

## Endpoints planejados (Epic 01)
Listar histórico, editar, excluir transação e integração bancária serão adicionados neste mesmo padrão.
