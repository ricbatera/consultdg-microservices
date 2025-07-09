package br.com.consultdg.chatgpt_service.service;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.consultdg.chatgpt_service.dto.BoletoDTO;
import br.com.consultdg.database_mysql_service.model.boletos.Boleto;
import br.com.consultdg.database_mysql_service.repository.boletos.BoletoRepository;
import br.com.consultdg.database_mysql_service.model.boletos.ImagemBase64;
import br.com.consultdg.database_mysql_service.repository.ImagemBase64Repository;
import br.com.consultdg.protocolo_service_util.dto.boletos.BoletoBase64Request;
import br.com.consultdg.protocolo_service_util.enums.StatusProtocolo;
import br.com.consultdg.protocolo_service_util.enums.boletos.SubStatusEventosBoleto;
import br.com.consultdg.protocolo_service_util.enums.boletos.TipoEvento;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProcessPdfService {

    Logger logger = LoggerFactory.getLogger(ProcessPdfService.class);

    public static final String FORMAT_JPEG = "jpeg";
    public static final String FORMAT_PNG = "png";
    public static final String FORMAT_DEFAULT = FORMAT_PNG; // Troque aqui para FORMAT_JPEG ou FORMAT_PNG


    @Autowired
    private ChatGptBoletoService chatGptBoletoService;

    @Autowired
    private BoletoRepository boletoRepository;

    @Autowired
    private ImagemBase64Repository imagemBase64Repository;

    @Autowired
    private RegistraProtocoloService registraProtocoloService;

    @Value("${path.base}")
    private String basePath;

    public ProcessPdfService() {
        // Garante que o ImageIO reconheça JPEG
        ImageIO.scanForPlugins();
    }

    public List<String> convertAllPdfsToBase64Images() {
        logger.info("Convertendo todos os PDFs no diretório: {}", basePath);
        File directory = new File(basePath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new RuntimeException("Diretório base não encontrado: " + basePath);
        }
        List<File> pdfFiles = FileUtils.listFiles(directory, new String[] { "pdf" }, false)
                .stream().collect(Collectors.toList());
        List<String> base64Images = new ArrayList<>();
        for (File pdf : pdfFiles) {
            base64Images.addAll(convertPdfToBase64Images(pdf, FORMAT_DEFAULT));
        }
        return base64Images;
    }

    public List<String> convertPdfToBase64Images(File pdfFile) {
        logger.info("Convertendo PDF para imagens base64: {}", pdfFile.getName());
        return convertPdfToBase64Images(pdfFile, FORMAT_DEFAULT);
    }

    public List<String> convertPdfToBase64Images(File pdfFile, String format) {
        logger.info("Convertendo PDF para imagens base64: {} no formato {}", pdfFile.getName(), format);
        List<String> imagesBase64 = new ArrayList<>();
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 200); // 200 DPI para boa qualidade
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bim, format, baos);
                String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
                String mime = format.equals(FORMAT_PNG) ? "png" : "jpeg";
                imagesBase64.add("data:image/" + mime + ";base64," + base64);
            }
        } catch (Exception e) {
            logger.error("Erro ao processar PDF: {}", pdfFile.getName(), e.getMessage());
            throw new RuntimeException("Erro ao processar PDF: " + pdfFile.getName(), e);
        }
        return imagesBase64;
    }

    public void processaPdfBase64(BoletoBase64Request entity) {
        logger.info("Processando PDF base64 para o protocolo: {}", entity.idProtocolo());
        registraProtocoloService.registraEventoProtocolo(null,entity.idProtocolo(),SubStatusEventosBoleto.EM_ANDAMENTO,TipoEvento.PROCESSA_BOLETO_EM_ANDAMENTO_CHATGPT);
        List<String> imagesBase64 = new ArrayList<>();
        String pdfBase64 = entity.base64Boleto();
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(pdfBase64);
            try (PDDocument document = PDDocument.load(decodedBytes)) {
                PDFRenderer pdfRenderer = new PDFRenderer(document);
                for (int page = 0; page < document.getNumberOfPages(); ++page) {
                    BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 200);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(bim, FORMAT_DEFAULT, baos);
                    String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
                    String mime = FORMAT_DEFAULT.equals(FORMAT_PNG) ? "png" : "jpeg";
                    imagesBase64.add("data:image/" + mime + ";base64," + base64);
                }
                processaGpt(imagesBase64, entity);
            }
        registraProtocoloService.registraEventoProtocolo(null,entity.idProtocolo(),SubStatusEventosBoleto.EM_ANDAMENTO,TipoEvento.PROCESSA_BOLETO_FINALIZADO_CHATGPT);
        } catch (Exception e) {
            registraProtocoloService.registraEventoProtocolo(null,entity.idProtocolo(),SubStatusEventosBoleto.ERRO,TipoEvento.PROCESSA_BOLETO_ERRO_CHATGPT);
            registraProtocoloService.atualizaProtocolo(entity.idProtocolo(),StatusProtocolo.COM_ERRO, e.getLocalizedMessage());
            throw new RuntimeException("Erro ao processar PDF base64", e);
        }
    }

    private void processaGpt(List<String> imagensBase64, BoletoBase64Request request) {
        // Recupera o boleto pelo protocolo_id
        Boleto boleto = boletoRepository.findByProtocoloId(request.idProtocolo())
                .orElseThrow(() -> new RuntimeException("Boleto não encontrado para o protocolo: " + request.idProtocolo()));

        for (int i = 0; i < imagensBase64.size(); i++) {
            String imagemBase64 = imagensBase64.get(i);
            String formato = "png";
            if (imagemBase64.startsWith("data:image/")) {
                int idx = imagemBase64.indexOf(';');
                if (idx > 0) {
                    String mime = imagemBase64.substring(11, idx);
                    formato = mime;
                }
            }
            // Remove o prefixo data:image/...;base64,
            String base64SemPrefixo = imagemBase64.contains(",") ? imagemBase64.substring(imagemBase64.indexOf(",") + 1) : imagemBase64;

            ImagemBase64 imagem = new ImagemBase64(base64SemPrefixo, formato, i + 1, boleto);
            imagemBase64Repository.save(imagem);
        }

        // Chama a API do ChatGPT para cada base64 (mantém lógica original)
        for (int i = 0; i < imagensBase64.size(); i++) {
            List<BoletoDTO> boletos = chatGptBoletoService.analisarImagens(List.of(imagensBase64.get(i)));
            for (BoletoDTO dto : boletos) {
                Boleto entity = converterParaEntity(dto, request);
                boletoRepository.save(entity);
                logger.info("Boleto persistido: {}", entity.getNomeArquivo());
            }
        }
    }

    private Boleto converterParaEntity(BoletoDTO dto, BoletoBase64Request request) {
        Boleto entity = boletoRepository.findByProtocoloId(request.idProtocolo())
                .orElse(new Boleto()); // Busca ou cria um novo objeto Boleto
        entity.setCodigoBarras(dto.getCodigo_barras());
        // Conversão de data_vencimento (String) para LocalDate
        if (dto.getData_vencimento() != null && !dto.getData_vencimento().isEmpty()) {
            try {
                entity.setDataVencimento(java.time.LocalDate.parse(dto.getData_vencimento()));
            } catch (Exception e) {
                entity.setDataVencimento(null);
            }
        }
        entity.setValor(dto.getValor_boleto());
        entity.setNumeroDocumento(dto.getNumero_documento());
        entity.setCnpjPagador(dto.getCnpj_pagador());
        entity.setCnpjRecebedor(dto.getCnpj_beneficiario());
        entity.setJson(dto.getJson());
        // Conversão de tipo_boleto (String) para Enum
        if (dto.getTipo_boleto() != null) {
            try {
                entity.setTipoBoleto(br.com.consultdg.database_mysql_service.enums.TipoBoleto.valueOf(dto.getTipo_boleto()));
            } catch (Exception e) {
                entity.setTipoBoleto(null);
            }
        }
        // Conversão de data_criacao (String) para LocalDateTime
        if (dto.getData_criacao() != null && !dto.getData_criacao().isEmpty()) {
            try {
                entity.setDataCriacao(java.time.LocalDateTime.parse(dto.getData_criacao()));
            } catch (Exception e) {
                entity.setDataCriacao(java.time.LocalDateTime.now());
            }
        }
        entity.setItensValidados(dto.getItens_validados());
        // Conversão dos itens (corrigido para evitar erro de orphanRemoval)
        if (dto.getItens() != null) {
            List<br.com.consultdg.database_mysql_service.model.boletos.ItemBoleto> itensEntity = entity.getItens();
            if (itensEntity == null) {
                itensEntity = new java.util.ArrayList<>();
                entity.setItens(itensEntity);
            } else {
                itensEntity.clear(); // Remove todos os antigos corretamente
            }
            for (BoletoDTO.ItemDTO itemDTO : dto.getItens()) {
                br.com.consultdg.database_mysql_service.model.boletos.ItemBoleto itemEntity = new br.com.consultdg.database_mysql_service.model.boletos.ItemBoleto();
                itemEntity.setNome(itemDTO.getNome());
                itemEntity.setValor(itemDTO.getValor());
                itemEntity.setBoleto(entity); // Relaciona o item ao boleto
                itensEntity.add(itemEntity);
            }
        }
        // Protocolo (apenas id, se necessário)
        // Se precisar buscar o Protocolo pelo id, adicione aqui
        return entity;
    }
}
