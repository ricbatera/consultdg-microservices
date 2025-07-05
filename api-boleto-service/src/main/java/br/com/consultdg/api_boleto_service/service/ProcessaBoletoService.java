package br.com.consultdg.api_boleto_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import br.com.consultdg.api_boleto_service.dto.NovoBoletoRequest;
import br.com.consultdg.database_mysql_service.model.Protocolo;
import br.com.consultdg.database_mysql_service.model.boletos.Boleto;
import br.com.consultdg.database_mysql_service.repository.ProtocoloRepository;
import br.com.consultdg.database_mysql_service.repository.boletos.BoletoRepository;
import br.com.consultdg.protocolo_service_util.dto.response.NovoProtocoloResponse;
import br.com.consultdg.protocolo_service_util.enums.boletos.SubStatusEventosBoleto;
import br.com.consultdg.protocolo_service_util.enums.boletos.TipoEvento;

@Service
public class ProcessaBoletoService {
    @Autowired
    private BoletoRepository boletoRepository;
    @Autowired
    private ProtocoloRepository protocoloRepository;

    @Autowired
    private RegistraProtocoloService registraProtocoloService;


    /**
     * Processa o boleto recebido, realizando as operações necessárias como salvar o arquivo,
     * registrar informações no banco de dados, etc.
     *
     * @param protocoloResponse Resposta do protocolo que contém informações sobre o boleto.
     * @param novoBoletoRequest Dados do novo boleto a ser processado.
     */
    public void processar(NovoProtocoloResponse protocoloResponse, NovoBoletoRequest novoBoletoRequest) {
        Boleto boleto = new Boleto();
        Protocolo protocolo = protocoloRepository.findById(protocoloResponse.id())
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado"));
        boleto.setProtocolo(protocolo);
        boleto.setNomeArquivo(novoBoletoRequest.getNomeArquivo());
        boleto.setArquivoPdfBase64(novoBoletoRequest.getArquivoBase64());
        registraProtocoloService.registraEventoProtocolo(novoBoletoRequest.getNomeArquivo(), protocoloResponse.id(),SubStatusEventosBoleto.CRIADO, TipoEvento.PROCESSA_BOLETO_CRIADO);
        boletoRepository.save(boleto);
        registraProtocoloService.registraEventoProtocolo(novoBoletoRequest.getNomeArquivo(), protocoloResponse.id(),SubStatusEventosBoleto.CRIADO, TipoEvento.PROCESSA_BOLETO_NOVO);
        
    }
}
