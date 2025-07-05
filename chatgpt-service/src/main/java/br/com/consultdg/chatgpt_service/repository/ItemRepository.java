package br.com.consultdg.chatgpt_service.repository;

import br.com.consultdg.chatgpt_service.model.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
}
