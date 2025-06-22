    package br.com.consultdg.protocolo_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.consultdg.database_mysql_service.model.Protocolo;
import br.com.consultdg.database_mysql_service.repository.ProtocoloRepository;
import br.com.consultdg.protocolo_service_util.dto.ProtocoloDto;
import br.com.consultdg.protocolo_service_util.dto.response.NovoProtocoloResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/protocolo-service")
public class ProtocoloController {

    @Autowired
    private ProtocoloRepository protocoloRepository;

    @PostMapping("/novo-protocolo")
    public NovoProtocoloResponse newProtocol(@RequestBody ProtocoloDto entity) {
        
        Protocolo entityToSave = new Protocolo();
        BeanUtils.copyProperties(entity, entityToSave);
        
        var protocolo = protocoloRepository.save(entityToSave);
        return new NovoProtocoloResponse(protocolo.getId(), protocolo.getNumeroProtocolo());
    }

    @PostMapping("/atualiza-protocolo")
    public void atualizaProtocolo(@RequestBody ProtocoloDto entity) {
        Protocolo protocolo = protocoloRepository.findById(entity.getId())
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado com ID: " + entity.getId()));
        //TODO implementar a lógica de atualização do protocolo
        BeanUtils.copyProperties(entity, protocolo);
        protocoloRepository.save(protocolo);
    }

    @GetMapping("/{id}")
    public ProtocoloDto getProtocoloById(@RequestParam Long id) {
        Protocolo protocolo = protocoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado com ID: " + id));
        
        ProtocoloDto response = new ProtocoloDto();
        BeanUtils.copyProperties(protocolo, response);
        
        return response;
    }
    
    @GetMapping("/hello")
	public String hello() {
		return "Hello, Protocolo Service is running! Controller name: ProtocoloController";
	}
    
}
