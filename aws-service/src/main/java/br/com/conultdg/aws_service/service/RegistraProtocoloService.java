package br.com.conultdg.aws_service.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.consultdg.protocolo_service_util.dto.ProtocoloDto;
import br.com.consultdg.protocolo_service_util.dto.boletos.EventosProtocoloBoletoRequest;
import br.com.consultdg.protocolo_service_util.dto.response.NovoProtocoloResponse;
import br.com.consultdg.protocolo_service_util.enums.StatusProtocolo;
import br.com.consultdg.protocolo_service_util.enums.boletos.SubStatusEventosBoleto;
import br.com.consultdg.protocolo_service_util.enums.boletos.TipoEvento;
import br.com.conultdg.aws_service.proxy.ProtocoloServiceProxy;

@Service
public class RegistraProtocoloService {
    @Autowired
    private ProtocoloServiceProxy protocoloServiceProxy;
    private static final String SISTEMA_ORIGEM = "AWS_SERVICE";
    private EventosProtocoloBoletoRequest  eventoRequest = new EventosProtocoloBoletoRequest();
    public NovoProtocoloResponse registraProtocolo() {
        // Cria o objeto de requisição para o protocolo
        var protocoloRequest = new ProtocoloDto(SISTEMA_ORIGEM);        
        // Chama o serviço de protocolo para criar um novo protocolo
        return protocoloServiceProxy.newProtocol(protocoloRequest);
    }

    public void registraEventoProtocolo(String fileName, Long protocolo_id, SubStatusEventosBoleto subStatusEvento, TipoEvento tipoEvento){                
        
        eventoRequest.setNomeArquivo(fileName);
        
        eventoRequest.setSubStatusEvento(subStatusEvento);
        eventoRequest.setTipoEvento(tipoEvento);
        
        // Registra o evento do protocolo do boleto
        registroEfetivoEvento(protocolo_id);
    }

    public void registraEventoProtocoloPathS3Alvo(Long protocolo_id, SubStatusEventosBoleto subStatusEvento, TipoEvento tipoEvento, String pathS3Alvo) {        
        eventoRequest.setSubStatusEvento(subStatusEvento);
        eventoRequest.setTipoEvento(tipoEvento);
        eventoRequest.setPathS3Alvo(pathS3Alvo);
        
        // Registra o evento do protocolo do boleto
        registroEfetivoEvento(protocolo_id);

    }
        public void registraEventoProtocoloPathS3Atual(Long protocolo_id, SubStatusEventosBoleto subStatusEvento, TipoEvento tipoEvento, String pathS3Atual) {        
        eventoRequest.setSubStatusEvento(subStatusEvento);
        eventoRequest.setTipoEvento(tipoEvento);
        eventoRequest.setPathS3Atual(pathS3Atual);
        
        // Registra o evento do protocolo do boleto
        registroEfetivoEvento(protocolo_id);

    }

    private void registroEfetivoEvento(Long protocolo_id){
        if(protocolo_id == null) {
            throw new RuntimeException("ID do protocolo não pode ser nulo");
        }
        eventoRequest.setSistemaOrigemEvento(SISTEMA_ORIGEM);
        eventoRequest.setIdProtocolo(protocolo_id);
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
