package br.com.consultdg.api_boleto_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.consultdg.api_boleto_service.dto.AllProtocoloRequest;
import br.com.consultdg.api_boleto_service.dto.BoletoDetailsResponse;
import br.com.consultdg.database_mysql_service.enums.TipoBoleto;
import br.com.consultdg.database_mysql_service.model.boletos.ItemBoleto;
import br.com.consultdg.database_mysql_service.repository.ProtocoloRepository;
import br.com.consultdg.database_mysql_service.repository.boletos.BoletoRepository;
import br.com.consultdg.database_mysql_service.repository.boletos.ItemBoletoRepository;
import br.com.consultdg.database_mysql_service.repository.projection.ProtocoloBoletoProjection;
import br.com.consultdg.api_boleto_service.dto.ItensDetailsResponse;

@Service
public class ProtocoloService {
    Logger logger = LoggerFactory.getLogger(ProtocoloService.class);

    @Autowired
    private ProtocoloRepository protocoloRepository;

    @Autowired
    private BoletoRepository boletoRepository;

    @Autowired
    private ItemBoletoRepository itemBoletoRepository;

    public List<ProtocoloBoletoProjection> getProtocolosByDateInicialFinal(AllProtocoloRequest req) {
        LocalDateTime dataInicial = LocalDateTime.parse(req.dataInicial()+"T00:00:00");
        LocalDateTime dataFinal = LocalDateTime.parse(req.dataFinal()+"T23:59:59");
        
        logger.info("\nData inicial: {}", dataInicial);
        logger.info("\nData final: {}", dataFinal);
        return protocoloRepository.buscarProtocoloBoletoPorPeriodo(dataInicial, dataFinal);
    }

    public BoletoDetailsResponse getBoletoDetailsByProtocoloId(Long protocoloId) {
        var boleto = boletoRepository.findByProtocoloId(protocoloId).orElseThrow(
            () -> new RuntimeException("Boleto não encontrado protocoloId: " + protocoloId));
        List<ItemBoleto> itens = itemBoletoRepository.findByBoletoId(boleto.getId());
        List<ItensDetailsResponse> itensResponse = itens.stream().map(item -> {
            ItensDetailsResponse response = new ItensDetailsResponse(item.getNome(), item.getValor());
            return response;
        }).collect(Collectors.toList());

        BoletoDetailsResponse response = new BoletoDetailsResponse();
        response.setProtocoloId(protocoloId);
        response.setNumeroDocumento(boleto.getNumeroDocumento());
        response.setDataVencimento(boleto.getDataVencimento());
        response.setCodigoBarras(boleto.getCodigoBarras());
        response.setValor(boleto.getValor());
        if(boleto.getTipoBoleto().equals(TipoBoleto.BOLETO_COMUM)) response.setItens(itensResponse);
        return response;
    }
}
