package br.com.consultdg.api_boleto_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.consultdg.database_mysql_service.repository.boletos.BoletoRepository;

import br.com.consultdg.database_mysql_service.model.boletos.Boleto;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DownloadPdfService {
    @Autowired
    private BoletoRepository boletoRepository;

    @Transactional(readOnly = true)
    public Optional<Boleto> buscarBoletoPorId(Long id) {
        return boletoRepository.findById(id);
    }

}
