package br.com.consultdg.api_boleto_service.proxy;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.consultdg.protocolo_service_util.dto.ProtocoloDto;
import br.com.consultdg.protocolo_service_util.dto.boletos.EventosProtocoloBoletoRequest;
import br.com.consultdg.protocolo_service_util.dto.response.NovoProtocoloResponse;

// @FeignClient(name = "protocolo-service", url = "http://localhost:8091")
@FeignClient(name = "${services.protocolo-service.name}", url = "${services.protocolo-service.url}")
public interface ProtocoloServiceProxy {

    @PostMapping("/protocolo-service/novo-protocolo")
    public NovoProtocoloResponse newProtocol(@RequestBody ProtocoloDto entity);

    
    @PostMapping("/protocolo-service/atualiza-protocolo")
    public void atualizaProtocolo(@RequestBody ProtocoloDto entity);
    
    @GetMapping("/protocolo-service/{id}")
    public ProtocoloDto getProtocoloById(@RequestParam Long id);
    
    @PostMapping("/boletos-protocolos/novo-evento")
    public void novoEvento(@RequestBody EventosProtocoloBoletoRequest entity);

}
