package com.bradesco.service;

import com.bradesco.dto.response.CategoriaResponseDTO;
import com.bradesco.repository.CategoriaRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<CategoriaResponseDTO> listar() {
        return categoriaRepository.findAll()
                .stream()
                .map(categoria -> new CategoriaResponseDTO(
                        categoria.getId(),
                        categoria.getNome()))
                .toList();
    }
}
