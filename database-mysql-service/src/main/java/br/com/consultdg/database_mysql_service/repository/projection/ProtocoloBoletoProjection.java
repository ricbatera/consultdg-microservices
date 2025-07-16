package br.com.consultdg.database_mysql_service.repository.projection;

import java.time.LocalDateTime;
import br.com.consultdg.protocolo_service_util.enums.StatusProtocolo;

public interface ProtocoloBoletoProjection {
    Long getId();
    StatusProtocolo getStatusProtocolo();
    String getNomeArquivo();
    String getMensagemErro();
    LocalDateTime getDataHoraCriacao();
}
