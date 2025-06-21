package br.com.consultdg.database_mysql_service.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import br.com.consultdg.protocolo_service_util.enums.StatusProtocolo;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "protocolo_tb")
public class Protocolo  implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String numeroProtocolo;
    private String sistemaOrigem;
    private LocalDateTime dataHoraCriacao;
    private LocalDateTime dataHoraUltimaAtualizacao;
    private LocalDateTime dataHoraFinalizacao;
    @Enumerated(EnumType.STRING)
    private StatusProtocolo statusProtocolo;
    private String mensagemErro;
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((numeroProtocolo == null) ? 0 : numeroProtocolo.hashCode());
        result = prime * result + ((sistemaOrigem == null) ? 0 : sistemaOrigem.hashCode());
        result = prime * result + ((dataHoraCriacao == null) ? 0 : dataHoraCriacao.hashCode());
        result = prime * result + ((dataHoraUltimaAtualizacao == null) ? 0 : dataHoraUltimaAtualizacao.hashCode());
        result = prime * result + ((dataHoraFinalizacao == null) ? 0 : dataHoraFinalizacao.hashCode());
        result = prime * result + ((statusProtocolo == null) ? 0 : statusProtocolo.hashCode());
        result = prime * result + ((mensagemErro == null) ? 0 : mensagemErro.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Protocolo other = (Protocolo) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (numeroProtocolo == null) {
            if (other.numeroProtocolo != null)
                return false;
        } else if (!numeroProtocolo.equals(other.numeroProtocolo))
            return false;
        if (sistemaOrigem == null) {
            if (other.sistemaOrigem != null)
                return false;
        } else if (!sistemaOrigem.equals(other.sistemaOrigem))
            return false;
        if (dataHoraCriacao == null) {
            if (other.dataHoraCriacao != null)
                return false;
        } else if (!dataHoraCriacao.equals(other.dataHoraCriacao))
            return false;
        if (dataHoraUltimaAtualizacao == null) {
            if (other.dataHoraUltimaAtualizacao != null)
                return false;
        } else if (!dataHoraUltimaAtualizacao.equals(other.dataHoraUltimaAtualizacao))
            return false;
        if (dataHoraFinalizacao == null) {
            if (other.dataHoraFinalizacao != null)
                return false;
        } else if (!dataHoraFinalizacao.equals(other.dataHoraFinalizacao))
            return false;
        if (statusProtocolo != other.statusProtocolo)
            return false;
        if (mensagemErro == null) {
            if (other.mensagemErro != null)
                return false;
        } else if (!mensagemErro.equals(other.mensagemErro))
            return false;
        return true;
    }
    @Override
    public String toString() {
        return "Protocolo [id=" + id + ", numeroProtocolo=" + numeroProtocolo + ", sistemaOrigem=" + sistemaOrigem
                + ", dataHoraCriacao=" + dataHoraCriacao + ", dataHoraUltimaAtualizacao=" + dataHoraUltimaAtualizacao
                + ", dataHoraFinalizacao=" + dataHoraFinalizacao + ", statusProtocolo=" + statusProtocolo
                + ", mensagemErro=" + mensagemErro + "]";
    }

    

}