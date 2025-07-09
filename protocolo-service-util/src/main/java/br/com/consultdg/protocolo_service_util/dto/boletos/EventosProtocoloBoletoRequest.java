package br.com.consultdg.protocolo_service_util.dto.boletos;

import java.time.LocalDateTime;

import br.com.consultdg.protocolo_service_util.enums.boletos.SubStatusEventosBoleto;
import br.com.consultdg.protocolo_service_util.enums.boletos.TipoEvento;

public class EventosProtocoloBoletoRequest {
    private Long idEvento;
    private String nomeArquivo;
    private String pathS3Atual;
    private String pathS3Alvo;
    private String sistemaOrigemEvento;
    private Long idProtocolo;
    private SubStatusEventosBoleto subStatusEvento;
    private TipoEvento tipoEvento;
    private LocalDateTime dataHoraCriacao = LocalDateTime.now();
    private String mensagem;


    public EventosProtocoloBoletoRequest() {}

    public EventosProtocoloBoletoRequest(Long idEvento, String nomeArquivo, String pathS3Atual, String pathS3Alvo,
                                         String sistemaOrigemEvento, Long idProtocolo,
                                         SubStatusEventosBoleto subStatusEvento, TipoEvento tipoEvento) {
        this(idEvento, nomeArquivo, pathS3Atual, pathS3Alvo, sistemaOrigemEvento, idProtocolo, subStatusEvento, tipoEvento, null);
    }

    public EventosProtocoloBoletoRequest(Long idEvento, String nomeArquivo, String pathS3Atual, String pathS3Alvo,
                                         String sistemaOrigemEvento, Long idProtocolo,
                                         SubStatusEventosBoleto subStatusEvento, TipoEvento tipoEvento, String mensagem) {
        this.idEvento = idEvento;
        this.nomeArquivo = nomeArquivo;
        this.pathS3Atual = pathS3Atual;
        this.pathS3Alvo = pathS3Alvo;
        this.sistemaOrigemEvento = sistemaOrigemEvento;
        this.idProtocolo = idProtocolo;
        this.subStatusEvento = subStatusEvento;
        this.tipoEvento = tipoEvento;
        this.dataHoraCriacao = LocalDateTime.now();
        this.mensagem = mensagem;
    }
    public LocalDateTime getDataHoraCriacao() {
        return dataHoraCriacao;
    }    
    public SubStatusEventosBoleto getSubStatusEvento() {
        return subStatusEvento;
    }
    public void setSubStatusEvento(SubStatusEventosBoleto subStatusEvento) {
        this.subStatusEvento = subStatusEvento;
    }
    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }
    public void setTipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
    }
    
    public Long getIdEvento() {
        return idEvento;
    }
    public void setIdEvento(Long idEvento) {
        this.idEvento = idEvento;
    }
    public String getNomeArquivo() {
        return nomeArquivo;
    }
    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }
    public String getPathS3Atual() {
        return pathS3Atual;
    }
    public void setPathS3Atual(String pathS3Atual) {
        this.pathS3Atual = pathS3Atual;
    }
    public String getPathS3Alvo() {
        return pathS3Alvo;
    }
    public void setPathS3Alvo(String pathS3Alvo) {
        this.pathS3Alvo = pathS3Alvo;
    }
    public String getSistemaOrigemEvento() {
        return sistemaOrigemEvento;
    }
    public void setSistemaOrigemEvento(String sistemaOrigemEvento) {
        this.sistemaOrigemEvento = sistemaOrigemEvento;
    }
    public Long getIdProtocolo() {
        return idProtocolo;
    }
    public void setIdProtocolo(Long idProtocolo) {
        this.idProtocolo = idProtocolo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

}
