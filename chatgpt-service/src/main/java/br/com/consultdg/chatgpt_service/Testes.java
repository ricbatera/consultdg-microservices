package br.com.consultdg.chatgpt_service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Testes {
    public static void main(String[] args) throws Exception {
        String content = "```json\n{\n  \"valor_boleto\": 36543.20,\n  \"data_vencimento\": \"01/07/2025\",\n  \"itens\": [\n    {\n      \"descricao\": \"Aluguel Mínimo - 06/2025\",\n      \"valor\": 16324.70\n    },\n    {\n      \"descricao\": \"Aluguel Atual - 07/2025\",\n      \"valor\": 16324.70\n    }\n  ],\n  \"itens_validados\": true,\n  \"cnpj_pagador\": \"24.276.833/0001-48\",\n  \"cnpj_beneficiario\": \"51.305.880/0011-8\",\n  \"numero_documento\": \"5900002921\",\n  \"codigo_barras\": \"341919090160104208293185986180009911290003654320\"\n}\n```";

        // Remove delimitadores de código Markdown
        String jsonString = content.replaceAll("(?s)^```json\\s*|\\s*```$", "");

        // Parseando para JsonNode (pode ser mapeado também para POJO)
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonString);

        // Exemplo de leitura de dados
        double valorBoleto = rootNode.get("valor_boleto").asDouble();
        String vencimento = rootNode.get("data_vencimento").asText();

        System.out.println("Valor do boleto: " + valorBoleto);
        System.out.println("Vencimento: " + vencimento);
    }
}
