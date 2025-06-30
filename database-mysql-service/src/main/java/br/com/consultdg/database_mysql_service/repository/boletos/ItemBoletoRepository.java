package br.com.consultdg.database_mysql_service.repository.boletos;

import br.com.consultdg.database_mysql_service.model.boletos.ItemBoleto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemBoletoRepository extends JpaRepository<ItemBoleto, Long> {
}
