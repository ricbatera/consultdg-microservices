package br.com.consultdg.protocolo_service_util.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import br.com.consultdg.protocolo_service_util.enums.StatusProtocolo;

public class ProtocoloDto  implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String numeroProtocolo;
    private String sistemaOrigem;
    private LocalDateTime dataHoraCriacao;
    private LocalDateTime dataHoraUltimaAtualizacao;
    private LocalDateTime dataHoraFinalizacao;
    private StatusProtocolo statusProtocolo;
    private String mensagemErro;
    public ProtocoloDto() {}
    public ProtocoloDto(String sistemaOrigem) {
        this.sistemaOrigem = sistemaOrigem;
        this.dataHoraCriacao = LocalDateTime.now();
        this.statusProtocolo = StatusProtocolo.CRIADO;
        this.numeroProtocolo = generateProtocol(sistemaOrigem, dataHoraCriacao);
    }
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }
    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }
    public String getSistemaOrigem() {
        return sistemaOrigem;
    }
    public void setSistemaOrigem(String sistemaOrigem) {
        this.sistemaOrigem = sistemaOrigem;
    }
    public LocalDateTime getDataHoraCriacao() {
        return dataHoraCriacao;
    }
    public void setDataHoraCriacao(LocalDateTime dataHoraCriacao) {
        this.dataHoraCriacao = dataHoraCriacao;
    }
    public LocalDateTime getDataHoraUltimaAtualizacao() {
        return dataHoraUltimaAtualizacao;
    }
    public void setDataHoraUltimaAtualizacao(LocalDateTime dataHoraUltimaAtualizacao) {
        this.dataHoraUltimaAtualizacao = dataHoraUltimaAtualizacao;
    }
    public LocalDateTime getDataHoraFinalizacao() {
        return dataHoraFinalizacao;
    }
    public void setDataHoraFinalizacao(LocalDateTime dataHoraFinalizacao) {
        this.dataHoraFinalizacao = dataHoraFinalizacao;
    }
    public StatusProtocolo getStatusProtocolo() {
        return statusProtocolo;
    }
    public void setStatusProtocolo(StatusProtocolo statusProtocolo) {
        this.statusProtocolo = statusProtocolo;
    }
    public String getMensagemErro() {
        return mensagemErro;
    }
    public void setMensagemErro(String mensagemErro) {
        this.mensagemErro = mensagemErro;
    }

        /**
     * Gera um número de protocolo no formato SIST-YYYYMMDD-HHMMSS-XXXX
     * @param sistemaOrigem Prefixo do sistema de origem (ex: SIST)
     * @param dataHora Data e hora para compor o protocolo
     * @param sequencial Número sequencial (será preenchido com zeros à esquerda até 4 dígitos)
     * @return String no formato SIST-YYYYMMDD-HHMMSS-XXXX
     */
    public static String generateProtocol(String sistemaOrigem, LocalDateTime dataHora) {
        var now = System.currentTimeMillis();
        String data = String.format("%04d%02d%02d", dataHora.getYear(), dataHora.getMonthValue(), dataHora.getDayOfMonth());
        return String.format("%s-%s-%s", sistemaOrigem, data, now);
    }
    
}
