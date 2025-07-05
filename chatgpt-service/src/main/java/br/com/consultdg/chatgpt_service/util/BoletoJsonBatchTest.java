package br.com.consultdg.chatgpt_service.util;

import br.com.consultdg.chatgpt_service.dto.BoletoDTO;
import java.util.Arrays;
import java.util.List;

public class BoletoJsonBatchTest {
    public static void main(String[] args) {
        List<String> jsons = Arrays.asList("```json\\n{\\n  \"valor_boleto\": 23359.35,\\n  \"data_vencimento\": \"01.05.2024\",\\n  \"itens\": [\\n    {\\n      \"nome\": \"Fundo de Promoção\",\\n      \"valor\": 2273.18\\n    },\\n    {\\n      \"nome\": \"Aluguel Mínimo\",\\n      \"valor\": 10824.65\\n    },\\n    {\\n      \"nome\": \"Encargos Comuns\",\\n      \"valor\": 7656.79\\n    },\\n    {\\n      \"nome\": \"Ar Condicionado\",\\n      \"valor\": 1310.67\\n    },\\n    {\\n      \"nome\": \"Energia Elétrica\",\\n      \"valor\": 1051.32\\n    },\\n    {\\n      \"nome\": \"Seguro\",\\n      \"valor\": 16.95\\n    },\\n    {\\n      \"nome\": \"Taxa de Incêndio\",\\n      \"valor\": 16.11\\n    },\\n    {\\n      \"nome\": \"IPTU\",\\n      \"valor\": 215.68\\n    }\\n  ],\\n  \"cnpj_pagador\": \"24.276.833/0001-48\",\\n  \"cnpj_beneficiario\": \"14.551.970/0001-90\",\\n  \"numero_documento\": \"PQM0405452\",\\n  \"nosso_numero\": \"109-00006201-3\",\\n  \"codigo_barras\": \"34191090080062013293685856310009197030002335935\"\\n}\\n```");
        for (String json : jsons) {
            try {
                BoletoDTO dto = BoletoJsonConverter.fromJson(json);
                System.out.println(dto);
            } catch (Exception e) {
                System.out.println("Falha ao converter: " + e.getMessage());
            }
        }
    }
}
