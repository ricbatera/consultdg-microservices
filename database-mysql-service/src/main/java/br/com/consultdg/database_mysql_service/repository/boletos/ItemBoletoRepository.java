package br.com.consultdg.database_mysql_service.repository.boletos;

import br.com.consultdg.database_mysql_service.model.boletos.ItemBoleto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemBoletoRepository extends JpaRepository<ItemBoleto, Long> {
    List<ItemBoleto> findByBoletoId(Long boletoId);
}
