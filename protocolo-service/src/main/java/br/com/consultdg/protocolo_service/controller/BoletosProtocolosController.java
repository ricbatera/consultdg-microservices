package br.com.consultdg.protocolo_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.consultdg.database_mysql_service.model.boletos.protocolo.EventosProtocoloBoleto;
import br.com.consultdg.database_mysql_service.repository.ProtocoloRepository;
import br.com.consultdg.database_mysql_service.repository.boletos.protocolo.EventosProtocoloBoletoRepository;
import br.com.consultdg.protocolo_service_util.dto.boletos.EventosProtocoloBoletoRequest;

@RestController
@RequestMapping("/boletos-protocolos")
public class BoletosProtocolosController {

    Logger logger = LoggerFactory.getLogger(BoletosProtocolosController.class);

    @Autowired
    private ProtocoloRepository protocoloRepository;
    @Autowired
    private EventosProtocoloBoletoRepository eventosProtocoloBoletoRepository;

    @PostMapping("/novo-evento")
    public void novoEvento(@RequestBody EventosProtocoloBoletoRequest entity) {
        EventosProtocoloBoleto entityToSave = new EventosProtocoloBoleto();
        BeanUtils.copyProperties(entity, entityToSave);
        entityToSave.setIdEvento(null); // Garantir que o ID seja nulo para novo registro
        // Verifica se o protocolo existe antes de salvar o evento
        try {
            if (entity.getIdProtocolo() != null) {
                var protocolo = protocoloRepository.findById(entity.getIdProtocolo());
                if (protocolo.isPresent()) {
                    entityToSave.setProtocolo(protocolo.get());
                } else {
                    // TODO aqui para eu implmentar exceção personalizada

                    throw new RuntimeException("Protocolo não encontrado com ID: " + entity.getIdProtocolo());
                }
            } else {
                // TODO aqui para eu implmentar exceção personalizada
                throw new RuntimeException("Protocolo inválido ou não fornecido.");
            }
            eventosProtocoloBoletoRepository.save(entityToSave);
        } catch (Exception e) {
            logger.error("Erro ao salvar novo evento de protocolo de boleto: {}", e.getMessage());
        }

    }

    @GetMapping("hello")
    public String hello() {
        return "Hello, Boletos Protocolos Service is running! Controlller name: " + this.getClass().getSimpleName();
    }

}
