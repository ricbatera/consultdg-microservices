package br.com.consultdg.api_boleto_service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoletoDetailsResponse {

    private Long protocoloId;
    private String numeroDocumento;
    private String codigoBarras;
    private LocalDate dataVencimento;
    private BigDecimal valor;
    List<ItensDetailsResponse> itens;

}
