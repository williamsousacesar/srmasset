package br.com.srm.srmcreditengine.business.service;

import br.com.srm.srmcreditengine.business.exception.RecursoNaoEncontradoException;
import br.com.srm.srmcreditengine.persistence.entities.Cedente;
import br.com.srm.srmcreditengine.persistence.repository.CedenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CedenteService {

    private final CedenteRepository cedenteRepositorio;

    public CedenteService(CedenteRepository cedenteRepositorio) {

        this.cedenteRepositorio = cedenteRepositorio;
    }

    @Transactional
    public Cedente cadastrar(String nome, String documento) {
        Cedente cedente = Cedente.builder()
                .nome(nome)
                .documento(documento)
                .build();
        return cedenteRepositorio.save(cedente);
    }

    @Transactional(readOnly = true)
    public Cedente buscarPorId(Long id) {
        return cedenteRepositorio.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cedente nao encontrado: id " + id));
    }

    @Transactional(readOnly = true)
    public List<Cedente> listarTodos() {

        return cedenteRepositorio.findAll();
    }
}
