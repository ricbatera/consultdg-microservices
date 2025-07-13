package br.com.consultdg.api_boleto_service.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.consultdg.api_boleto_service.dto.UrlS3Response;
import br.com.consultdg.api_boleto_service.proxy.S3ServiceProxy;
import br.com.consultdg.api_boleto_service.service.RegistraProtocoloService;
import br.com.consultdg.api_boleto_service.service.ProcessaBoletoService;
import br.com.consultdg.api_boleto_service.dto.NovoBoletoRequest;
import br.com.consultdg.api_boleto_service.dto.ResponseDefault;
import br.com.consultdg.api_boleto_service.service.DownloadPdfService;
import br.com.consultdg.protocolo_service_util.dto.response.NovoProtocoloResponse;
import br.com.consultdg.protocolo_service_util.enums.StatusProtocolo;
import br.com.consultdg.protocolo_service_util.enums.boletos.SubStatusEventosBoleto;
import br.com.consultdg.protocolo_service_util.enums.boletos.TipoEvento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;

import br.com.consultdg.api_boleto_service.producer.BoletoProducer;

@Tag(name = "Boletos", description = "API para gerenciamento de requisições dos Sistema de Boletos")
@RestController
@RequestMapping("/api/boletos")
public class AppBoletoController {
	Logger logger = LoggerFactory.getLogger(AppBoletoController.class);

	@Autowired
	private RegistraProtocoloService registraProtocoloService;

	@Autowired
	private S3ServiceProxy s3ServiceProxy;

	@Autowired
	private ProcessaBoletoService processaBoletoService;

	@Autowired
	private DownloadPdfService downloadPdfService;

	@Autowired
	private br.com.consultdg.api_boleto_service.service.DownloadImagemService downloadImagemService;

	@Autowired
	private BoletoProducer boletoProducer;

	@GetMapping("get-url-to-upload-s3")
	@Operation(summary = "Obtém a URL para upload de arquivos S3", description = "Gera uma URL pré-assinada para upload de arquivos no S3.")
	public ResponseEntity<UrlS3Response> getUrlS3(@RequestParam String fileName) {
		logger.info("Novo boleto recebido, destino S3: " + fileName);

		// Registra o protocolo do boleto
		var response = registraProtocolo(fileName);
		// Aqui você pode implementar a lógica para gerar a URL de upload para o S3
		String url = s3ServiceProxy.generatePresignedUrl(fileName, response.id()).getBody();
		return ResponseEntity.ok(new UrlS3Response(url));
	}

	@GetMapping("hello")
	public String hello() {
		return "Hello, API Boleto Service is running!";
	}

	@PostMapping("/processar-novo-boleto")
	@Operation(summary = "Processa um novo boleto", description = "Recebe um novo boleto para processamento, contendo nome do arquivo e arquivo em base64.")
	public ResponseEntity<ResponseDefault> processarNovoBoleto(@RequestBody NovoBoletoRequest novoBoletoRequest) {
		logger.info("Processando novo boleto: " + novoBoletoRequest.getNomeArquivo());
		var res = registraProtocolo(novoBoletoRequest.getNomeArquivo());
		processaBoletoService.processar(res, novoBoletoRequest);
		registraProtocoloService.atualizaProtocolo(res.id(), StatusProtocolo.ABERTO, null);

		// Envia o id do protocolo para o RabbitMQ
		boletoProducer.sendProtocoloId(res.id());
		registraProtocoloService.registraEventoProtocolo(novoBoletoRequest.getNomeArquivo(), res.id(), SubStatusEventosBoleto.EM_ANDAMENTO, TipoEvento.PROCESSA_BOLETO_ENVIADO_FILA);

		return ResponseEntity.ok(new ResponseDefault("Boleto recebido para processamento: " + novoBoletoRequest.getNomeArquivo()));
	}

	private NovoProtocoloResponse registraProtocolo(String fileName){
		var response = registraProtocoloService.registraProtocolo();
		registraProtocoloService.registraEventoProtocolo(fileName, response.id(), SubStatusEventosBoleto.CRIADO,
				TipoEvento.SOLICITACAO_RECEBIDA);
				//altere aqui para retornar o response
		return response;
	}
	@GetMapping("/download-pdf")
	@Operation(summary = "Download do PDF do boleto", description = "Realiza o download do PDF do boleto pelo ID.")
	public ResponseEntity<byte[]> downloadPdf(@RequestParam Long id) {
		return downloadPdfService.buscarBoletoPorId(id)
			.map(boleto -> {
				String base64 = boleto.getArquivoPdfBase64();
				if (base64 == null || base64.isEmpty()) {
					return ResponseEntity.notFound().<byte[]>build();
				}
				byte[] pdfBytes = java.util.Base64.getDecoder().decode(base64);
				org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
				headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
				headers.setContentDispositionFormData("attachment", boleto.getNomeArquivo() != null ? boleto.getNomeArquivo() : ("boleto-" + id + ".pdf"));
				return ResponseEntity.ok().headers(headers).body(pdfBytes);
			})
			.orElse(ResponseEntity.notFound().<byte[]>build());
	}

	@GetMapping("/download-imagem-boleto")
	@Operation(summary = "Download das imagens do boleto", description = "Realiza o download das imagens do boleto pelo ID do boleto. Retorna uma lista de imagens base64, uma para cada página.")
	public ResponseEntity<?> downloadImagensBoleto(@RequestParam Long id) {
		var imagens = downloadImagemService.buscarImagensPorBoletoId(id);
		if (imagens == null || imagens.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		// Retorna lista de objetos com base64, formato e número da página
		var response = imagens.stream().map(img -> new java.util.HashMap<String, Object>() {{
			put("imagemBase64", img.getImagemBase64());
			put("formatoImagem", img.getFormatoImagem());
			put("numeroPagina", img.getNumeroPagina());
		}}).toList();
		return ResponseEntity.ok(response);
	}
}

