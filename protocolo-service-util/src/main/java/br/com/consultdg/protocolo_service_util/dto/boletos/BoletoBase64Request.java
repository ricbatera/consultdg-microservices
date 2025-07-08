package br.com.consultdg.protocolo_service_util.dto.boletos;

public record BoletoBase64Request(String base64Boleto, Long idProtocolo, String nomeArquivo) {}
