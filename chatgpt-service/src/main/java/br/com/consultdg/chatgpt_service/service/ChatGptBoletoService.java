package br.com.consultdg.chatgpt_service.service;

import br.com.consultdg.chatgpt_service.dto.BoletoDTO;
import br.com.consultdg.chatgpt_service.util.BoletoJsonConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import java.util.ArrayList;
import java.util.List;

import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionContentPart;
import com.openai.models.chat.completions.ChatCompletionContentPartImage;
import com.openai.models.chat.completions.ChatCompletionContentPartText;

@Service
public class ChatGptBoletoService {

    Logger logger = LoggerFactory.getLogger(ChatGptBoletoService.class);

    OpenAIClient client = OpenAIOkHttpClient.fromEnv();
    String imageURL = "";
    String prompt = "Analise essa imagem, é um boleto de cobrança, me retorne um JSON com as seguintes informações: valor do boleto (valor_boleto), data de vencimento (data_vencimento, formato: YYYY-MM-DD), procure um campo chamado número do documento (numero_documento), código de barras (codigo_barras) somente os números e valide se o código de barras tem 47 dígitos, não tiver retorne null nesse campo, cnpj do pagador (cnpj_pagador) formatado, cnpj do beneficiário (cnpj_beneficiario) formatado. Esse boleto pode possuir itens que discriminam separadamente cada valor que compõe o valor final, Caso encontre itens me retorne uma lista com cada item encontrado e seu respectivo valor, nesse padrao: nome, valor (numerico), se o boleto não tiver itens retorne uma lista vazia. Valide se a soma dos itens corresponde ao valor do boleto, para isso retorne itens_validados: true ou false. Esse JSON é padrão, portanto se não encontrar algum das informações solicitadas, retorne o nome do campo com valor null.";
    ChatCompletionContentPart imageContentPart;
    ChatCompletionContentPart questionContentPart;
    ChatCompletionCreateParams createParams;
    ChatCompletionCreateParams params;

    public List<BoletoDTO> analisarImagens(List<String> imagesBase64) {
        List<BoletoDTO> resultados = new ArrayList<>();
        for (String imageBase64 : imagesBase64) {
            String content = buildRequest(imageBase64);
            //logger.info("Resposta do ChatGPT: {}", content);
            if (content != null) {
                String json = content.replaceAll("(?s)^```json\\s*|\\s*```$", "").trim();
                logger.info("JSON retornado: {}", json);
                try {
                    BoletoDTO dto = BoletoJsonConverter.fromJson(json);
                    dto.setJson(json);
                    resultados.add(dto);
                } catch (Exception e) {
                    logger.error("Falha ao converter resposta do ChatGPT: {}", e.getMessage());
                }
            }
        }
        return resultados;
    }

    private String buildRequest(String imageBase64) {
        imageContentPart = ChatCompletionContentPart.ofImageUrl(ChatCompletionContentPartImage.builder()
                .imageUrl(ChatCompletionContentPartImage.ImageUrl.builder()
                        .url(imageBase64)
                        .build())
                .build());
        questionContentPart = ChatCompletionContentPart.ofText(ChatCompletionContentPartText.builder()
                .text(prompt)
                .build());
        createParams = ChatCompletionCreateParams.builder()
                .model("gpt-4o")
                .maxCompletionTokens(1000)
                .addUserMessageOfArrayOfContentParts(List.of(questionContentPart, imageContentPart))
                .build();
        return sendMessage();
    }

    private String sendMessage() {
        return client.chat().completions().create(createParams).choices().stream()
                .flatMap(choice -> choice.message().content().stream())
                .findFirst()
                .orElse(null);
    }    
}
