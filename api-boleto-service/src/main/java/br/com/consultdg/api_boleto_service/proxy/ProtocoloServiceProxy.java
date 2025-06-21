package br.com.consultdg.api_boleto_service.proxy;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.consultdg.protocolo_service_util.dto.request.ProtocoloRequest;
import br.com.consultdg.protocolo_service_util.dto.response.NovoProtocoloResponse;

// @FeignClient(name = "protocolo-service", url = "http://localhost:8091")
@FeignClient(name = "${services.protocolo-service.name}", url = "${services.protocolo-service.url}")
public interface ProtocoloServiceProxy {

    @PostMapping("/novo-protocolo")
    public NovoProtocoloResponse newProtocol(@RequestBody ProtocoloRequest entity);

}
