package br.com.consultdg.api_boleto_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.consultdg.api_boleto_service.dto.AllProtocoloRequest;
import br.com.consultdg.database_mysql_service.repository.ProtocoloRepository;
import br.com.consultdg.database_mysql_service.repository.projection.ProtocoloBoletoProjection;

@Service
public class ProtocoloService {
    Logger logger = LoggerFactory.getLogger(ProtocoloService.class);

    @Autowired
    private ProtocoloRepository protocoloRepository;

    public List<ProtocoloBoletoProjection> getProtocolosByDateInicialFinal(AllProtocoloRequest req) {
        LocalDateTime dataInicial = LocalDateTime.parse(req.dataInicial()+"T00:00:00");
        LocalDateTime dataFinal = LocalDateTime.parse(req.dataFinal()+"T23:59:59");
        
        logger.info("\nData inicial: {}", dataInicial);
        logger.info("\nData final: {}", dataFinal);
        return protocoloRepository.buscarProtocoloBoletoPorPeriodo(dataInicial, dataFinal);
    }

}
