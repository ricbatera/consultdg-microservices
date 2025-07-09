package br.com.consultdg.database_mysql_service.model.boletos.protocolo;

import java.io.Serializable;
import java.time.LocalDateTime;

import br.com.consultdg.database_mysql_service.model.Protocolo;
import br.com.consultdg.protocolo_service_util.enums.boletos.SubStatusEventosBoleto;
import br.com.consultdg.protocolo_service_util.enums.boletos.TipoEvento;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "eventos_protocolo_boleto_tb")
public class EventosProtocoloBoleto implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private Long idEvento;
    @Column(name = "nome_arquivo")
    private String nomeArquivo;
    @Column(name = "path_s3_atual")
    private String pathS3Atual;
    @Column(name = "path_s3_alvo")
    private String pathS3Alvo;
    @Column(name = "sistema_origem_evento")
    private String sistemaOrigemEvento;
    @ManyToOne
    @JoinColumn(name = "protocolo_id", nullable = false)
    private Protocolo protocolo;
    @Enumerated(EnumType.STRING)
    private SubStatusEventosBoleto subStatusEvento;
    @Enumerated(EnumType.STRING)
    private TipoEvento tipoEvento;
    @Column(name = "data_hora_criacao")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dataHoraCriacao;
    @Column(name = "mensagem")
    private String mensagem;
}
