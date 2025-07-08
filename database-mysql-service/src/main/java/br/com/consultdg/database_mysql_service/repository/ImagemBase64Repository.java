package br.com.consultdg.database_mysql_service.repository;

import br.com.consultdg.database_mysql_service.model.boletos.ImagemBase64;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImagemBase64Repository extends JpaRepository<ImagemBase64, Long> {
    List<ImagemBase64> findByBoletoIdOrderByNumeroPaginaAsc(Long boletoId);
}
