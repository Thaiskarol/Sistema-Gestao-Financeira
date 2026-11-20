package com.bradesco.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bradesco.dto.request.CategoriaRequestDTO;
import com.bradesco.dto.response.CategoriaResponseDTO;
import com.bradesco.entity.Categoria;
import com.bradesco.entity.Usuario;
import com.bradesco.exception.ConflitoException;
import com.bradesco.exception.ResourceNotFoundException;
import com.bradesco.repository.CategoriaRepository;
import com.bradesco.repository.OrcamentoRepository;
import com.bradesco.repository.TransacaoRepository;
import com.bradesco.repository.UsuarioRepository;

@Service
public class CategoriaService {

    private static final String MSG_NAO_ENCONTRADA = "Categoria não encontrada.";
    private static final String MSG_PADRAO = "As categorias padrão não podem ser alteradas nem excluídas.";
    private static final String MSG_NOME_REPETIDO = "Já existe uma categoria com esse nome.";
    private static final String MSG_EM_USO =
            "Esta categoria possui transações ou orçamentos vinculados e não pode ser excluída.";

    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TransacaoRepository transacaoRepository;
    private final OrcamentoRepository orcamentoRepository;

    public CategoriaService(CategoriaRepository categoriaRepository,
                            UsuarioRepository usuarioRepository,
                            TransacaoRepository transacaoRepository,
                            OrcamentoRepository orcamentoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.transacaoRepository = transacaoRepository;
        this.orcamentoRepository = orcamentoRepository;
    }

    // Sem usuário: só as padrão (mantém o GET /categorias antigo funcionando)
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listar() {
        return categoriaRepository.findByUsuarioIsNullOrderByIdAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    // Com usuário: padrão + as dele
    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listar(Long usuarioId) {
        validarUsuario(usuarioId);
        return categoriaRepository.findDisponiveis(usuarioId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public CategoriaResponseDTO criar(CategoriaRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        String nome = dto.nome().trim();

        if (categoriaRepository.existsByUsuarioIsNullAndNomeIgnoreCase(nome)
                || categoriaRepository.existsByUsuarioIdAndNomeIgnoreCase(dto.usuarioId(), nome)) {
            throw new ConflitoException(MSG_NOME_REPETIDO);
        }

        Categoria categoria = new Categoria();
        categoria.setNome(nome);
        categoria.setUsuario(usuario);

        return toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO dto) {
        Categoria categoria = buscarDoUsuario(id, dto.usuarioId());
        String nome = dto.nome().trim();

        if (categoriaRepository.existsByUsuarioIsNullAndNomeIgnoreCase(nome)
                || categoriaRepository.existsByUsuarioIdAndNomeIgnoreCaseAndIdNot(dto.usuarioId(), nome, id)) {
            throw new ConflitoException(MSG_NOME_REPETIDO);
        }

        categoria.setNome(nome);
        return toResponse(categoriaRepository.save(categoria));
    }

    @Transactional
    public void excluir(Long id, Long usuarioId) {
        Categoria categoria = buscarDoUsuario(id, usuarioId);

        if (transacaoRepository.existsByCategoriaId(id) || orcamentoRepository.existsByCategoriaId(id)) {
            throw new ConflitoException(MSG_EM_USO);
        }

        categoriaRepository.delete(categoria);
    }

    // Só devolve a categoria se ela for do usuário. Padrão -> 409. De outro usuário -> 404.
    private Categoria buscarDoUsuario(Long id, Long usuarioId) {
        validarUsuario(usuarioId);

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MSG_NAO_ENCONTRADA));

        if (categoria.isPadrao()) {
            throw new ConflitoException(MSG_PADRAO);
        }
        if (!categoria.getUsuario().getId().equals(usuarioId)) {
            throw new ResourceNotFoundException(MSG_NAO_ENCONTRADA);
        }
        return categoria;
    }

    private void validarUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuário não encontrado.");
        }
    }

    private CategoriaResponseDTO toResponse(Categoria c) {
        return new CategoriaResponseDTO(c.getId(), c.getNome(), c.isPadrao());
    }
}