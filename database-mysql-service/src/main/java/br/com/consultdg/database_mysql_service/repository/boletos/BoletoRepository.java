package br.com.consultdg.database_mysql_service.repository.boletos;

import br.com.consultdg.database_mysql_service.model.boletos.Boleto;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoletoRepository extends JpaRepository<Boleto, Long> {
    Optional<Boleto> findByProtocoloId(Long id);
}
