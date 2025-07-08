package br.com.consultdg.processa_boleto_service.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.consultdg.protocolo_service_util.dto.boletos.BoletoBase64Request;

@FeignClient(name = "${services.chatgpt-service.name}")
public interface ChatGptServiceProxy {

    @PostMapping("chatgpt/search-boleto")
    public String searchBoleto(@RequestBody BoletoBase64Request entity);

}
