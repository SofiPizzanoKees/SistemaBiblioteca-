package com.biblioteca.service;

import com.biblioteca.model.Editorial;
import com.biblioteca.repository.EditorialRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EditorialService {

    private final EditorialRepository editorialRepository;

    public EditorialService(EditorialRepository editorialRepository) {
        this.editorialRepository = editorialRepository;
    }

    public Editorial guardar(Editorial editorial) {
        return editorialRepository.save(editorial);
    }

    public List<Editorial> listarTodas() {
        return editorialRepository.findAll();
    }

    public Editorial buscarPorId(Long id) {
        return editorialRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Editorial no encontrada: " + id));
    }
}
