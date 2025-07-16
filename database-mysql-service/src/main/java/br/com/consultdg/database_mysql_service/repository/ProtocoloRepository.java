
package br.com.consultdg.database_mysql_service.repository;


import br.com.consultdg.database_mysql_service.model.Protocolo;
import br.com.consultdg.database_mysql_service.repository.projection.ProtocoloBoletoProjection;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProtocoloRepository extends JpaRepository<Protocolo, Long> {
    // Métodos customizados podem ser adicionados aqui

    @Query("""
        select 
            a.id as id, 
            a.statusProtocolo as statusProtocolo, 
            b.nomeArquivo as nomeArquivo, 
            a.mensagemErro as mensagemErro, 
            a.dataHoraCriacao as dataHoraCriacao
        from Protocolo a
        join Boleto b on a.id = b.protocolo.id
        where a.dataHoraCriacao between :inicio and :fim
        order by a.dataHoraCriacao desc
    """)
    List<ProtocoloBoletoProjection> buscarProtocoloBoletoPorPeriodo(
        @Param("inicio") LocalDateTime inicio,
        @Param("fim") LocalDateTime fim
    );
}
