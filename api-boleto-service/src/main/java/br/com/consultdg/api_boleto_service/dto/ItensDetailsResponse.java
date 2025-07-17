package br.com.consultdg.api_boleto_service.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItensDetailsResponse {

    String descricao;
    BigDecimal valor;

    public ItensDetailsResponse() {
    }
    public ItensDetailsResponse(String descricao, BigDecimal valor) {
        this.descricao = descricao;
        this.valor = valor;
    }
}
