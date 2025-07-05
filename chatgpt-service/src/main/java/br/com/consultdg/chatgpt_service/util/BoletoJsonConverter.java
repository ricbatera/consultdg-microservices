package br.com.consultdg.chatgpt_service.util;

import br.com.consultdg.chatgpt_service.dto.BoletoDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BoletoJsonConverter {
    static Logger logger = LoggerFactory.getLogger(BoletoJsonConverter.class);
    public static BoletoDTO fromJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, BoletoDTO.class);
        } catch (Exception e) {
            logger.error("Erro ao converter JSON para BoletoDTO: {}", e.getMessage());
            logger.error("Causa: {}", e.getCause());
            throw new RuntimeException("Erro ao converter JSON para BoletoDTO", e);
        }
    }
}
