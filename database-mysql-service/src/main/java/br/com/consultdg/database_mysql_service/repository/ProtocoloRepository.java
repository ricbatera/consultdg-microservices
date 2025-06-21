package br.com.consultdg.database_mysql_service.repository;

import br.com.consultdg.database_mysql_service.model.Protocolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProtocoloRepository extends JpaRepository<Protocolo, Long> {
    // Métodos customizados podem ser adicionados aqui
}
