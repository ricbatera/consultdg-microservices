    package br.com.consultdg.protocolo_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.consultdg.database_mysql_service.model.Protocolo;
import br.com.consultdg.database_mysql_service.repository.ProtocoloRepository;
import br.com.consultdg.protocolo_service_util.dto.request.ProtocoloRequest;
import br.com.consultdg.protocolo_service_util.dto.response.NovoProtocoloResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/protocolo-service")
public class ProtocoloController {

    @Autowired
    private ProtocoloRepository protocoloRepository;

    @PostMapping("novo-protocolo")
    public NovoProtocoloResponse newProtocol(@RequestBody ProtocoloRequest entity) {
        
        Protocolo entityToSave = new Protocolo();
        BeanUtils.copyProperties(entity, entityToSave);
        
        var protocolo = protocoloRepository.save(entityToSave);
        return new NovoProtocoloResponse(protocolo.getId(), protocolo.getNumeroProtocolo());
    }
    

}
