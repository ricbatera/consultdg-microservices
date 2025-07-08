package br.com.consultdg.api_boleto_service.service;

import br.com.consultdg.database_mysql_service.model.boletos.ImagemBase64;
import br.com.consultdg.database_mysql_service.repository.ImagemBase64Repository;
import br.com.consultdg.database_mysql_service.model.boletos.Boleto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DownloadImagemService {
    @Autowired
    private ImagemBase64Repository imagemBase64Repository;
    // BoletoRepository não é necessário para o download das imagens

    @Transactional(readOnly = true)
    public List<ImagemBase64> buscarImagensPorBoletoId(Long boletoId) {
        return imagemBase64Repository.findByBoletoIdOrderByNumeroPaginaAsc(boletoId);
    }
}
