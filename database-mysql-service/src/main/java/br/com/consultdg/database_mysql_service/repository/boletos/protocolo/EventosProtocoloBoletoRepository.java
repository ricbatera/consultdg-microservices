package br.com.consultdg.database_mysql_service.repository.boletos.protocolo;

import br.com.consultdg.database_mysql_service.model.boletos.protocolo.EventosProtocoloBoleto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventosProtocoloBoletoRepository extends JpaRepository<EventosProtocoloBoleto, Long> {
    // Métodos customizados podem ser adicionados aqui
}
