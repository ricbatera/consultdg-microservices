package br.com.consultdg.tesseract_service.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.consultdg.protocolo_service_util.dto.ProtocoloDto;
import br.com.consultdg.protocolo_service_util.dto.boletos.EventosProtocoloBoletoRequest;
import br.com.consultdg.protocolo_service_util.enums.StatusProtocolo;
import br.com.consultdg.protocolo_service_util.enums.boletos.SubStatusEventosBoleto;
import br.com.consultdg.protocolo_service_util.enums.boletos.TipoEvento;
import br.com.consultdg.tesseract_service.proxy.ProtocoloServiceProxy;

@Service
public class RegistraProtocoloService {

    @Autowired
    private ProtocoloServiceProxy protocoloServiceProxy;

    private static final String SISTEMA_ORIGEM = "TESSERACT_SERVICE";

    public void registraEventoProtocolo(String fileName, Long protocolo_id, SubStatusEventosBoleto subStatusEvento,
            TipoEvento tipoEvento) {

        var eventoRequest = new EventosProtocoloBoletoRequest();
        eventoRequest.setNomeArquivo(fileName);
        eventoRequest.setIdProtocolo(protocolo_id);
        eventoRequest.setSistemaOrigemEvento(SISTEMA_ORIGEM);
        eventoRequest.setSubStatusEvento(subStatusEvento);
        eventoRequest.setTipoEvento(tipoEvento);

        // Registra o evento do protocolo do boleto
        protocoloServiceProxy.novoEvento(eventoRequest);
    }

    public void registraEventoErroProtocolo(String fileName, Long protocolo_id, SubStatusEventosBoleto subStatusEvento,
            TipoEvento tipoEvento, String mensagemErro) {

        var eventoRequest = new EventosProtocoloBoletoRequest();
        eventoRequest.setNomeArquivo(fileName);
        eventoRequest.setIdProtocolo(protocolo_id);
        eventoRequest.setSistemaOrigemEvento(SISTEMA_ORIGEM);
        eventoRequest.setSubStatusEvento(subStatusEvento);
        eventoRequest.setTipoEvento(tipoEvento);
        eventoRequest.setMensagem(mensagemErro);

        // Registra o evento do protocolo do boleto
        protocoloServiceProxy.novoEvento(eventoRequest);
    }

    public void atualizaProtocolo(Long protocoloId, StatusProtocolo statusProtocolo, String mensagemErro) {
        var protocolo = protocoloServiceProxy.getProtocoloById(protocoloId);
        if (protocolo == null) {
            throw new RuntimeException("Protocolo não encontrado com ID: " + protocoloId);
        }
        // Cria o objeto de requisição para atualizar o protocolo
        var protocoloRequest = new ProtocoloDto();
        BeanUtils.copyProperties(protocolo, protocoloRequest);
        protocoloRequest.setStatusProtocolo(statusProtocolo);
        protocoloRequest.setMensagemErro(mensagemErro);

        // Chama o serviço de protocolo para atualizar o protocolo
        protocoloServiceProxy.atualizaProtocolo(protocoloRequest);
    }

}
