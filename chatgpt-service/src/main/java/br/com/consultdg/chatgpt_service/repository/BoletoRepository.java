package br.com.consultdg.chatgpt_service.repository;

import br.com.consultdg.chatgpt_service.model.BoletoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoletoRepository extends JpaRepository<BoletoEntity, Long> {
}
