package br.com.srm.srmcreditengine.business.service;

import br.com.srm.srmcreditengine.business.exception.RecursoNaoEncontradoException;
import br.com.srm.srmcreditengine.persistence.entities.Cedente;
import br.com.srm.srmcreditengine.persistence.entities.Recebivel;
import br.com.srm.srmcreditengine.persistence.enums.Moeda;
import br.com.srm.srmcreditengine.persistence.enums.StatusRecebivel;
import br.com.srm.srmcreditengine.persistence.enums.TipoRecebivel;
import br.com.srm.srmcreditengine.persistence.repository.CedenteRepository;
import br.com.srm.srmcreditengine.persistence.repository.RecebivelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class RecebivelService {

    private final RecebivelRepository recebivelRepository;
    private final CedenteRepository cedenteRepository;

    public RecebivelService(RecebivelRepository recebivelRepositorio, CedenteRepository cedenteRepositorio) {
        this.recebivelRepository = recebivelRepositorio;
        this.cedenteRepository = cedenteRepositorio;
    }

    @Transactional
    public Recebivel cadastrar(Long cedenteId, TipoRecebivel tipo, BigDecimal valorFace, Moeda moedaTitulo,
            Integer prazoMeses, LocalDate dataEmissao, LocalDate dataVencimento) {

        Cedente cedente = cedenteRepository.findById(cedenteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cedente nao encontrado: id " + cedenteId));

        Recebivel recebivel = Recebivel.builder()
                .cedente(cedente)
                .tipo(tipo)
                .valorFace(valorFace)
                .moedaTitulo(moedaTitulo)
                .prazoMeses(prazoMeses)
                .dataEmissao(dataEmissao)
                .dataVencimento(dataVencimento)
                .status(StatusRecebivel.PENDENTE)
                .build();

        return recebivelRepository.save(recebivel);
    }

    @Transactional(readOnly = true)
    public Recebivel buscarPorId(Long id) {
        return recebivelRepository.buscarComCedentePorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Recebivel nao encontrado: id " + id));
    }

    @Transactional(readOnly = true)
    public List<Recebivel> listarTodos() {

        return recebivelRepository.listarTodosComCedente();
    }
}
