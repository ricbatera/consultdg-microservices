package br.com.consultdg.tesseract_service.service;

import java.awt.image.BufferedImage;
import java.nio.Buffer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.consultdg.database_mysql_service.model.boletos.Boleto;
import br.com.consultdg.database_mysql_service.repository.boletos.BoletoRepository;
import br.com.consultdg.protocolo_service_util.enums.boletos.SubStatusEventosBoleto;
import br.com.consultdg.protocolo_service_util.enums.boletos.TipoEvento;
import br.com.consultdg.tesseract_service.producer.TesseractProducer;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

@Service
public class TesseractService {
    private Logger logger = LoggerFactory.getLogger(TesseractService.class);
    @Value("${tessdata.path}")
    private String tessdataPath;
    @Value("${tessdata.dpi}")
    private int tessdataDpi;

    @Autowired
    private BoletoRepository boletoRepository;
    @Autowired
    private RegistraProtocoloService registraProtocoloService;

    @Autowired
    private TesseractProducer tesseractProducer;

    public void processarBoleto(Long protocoloId) {
        logger.info("[TesseractService] Iniciando processamento do boleto associado ao protocolo: {}", protocoloId);
        Boleto boleto = boletoRepository.findByProtocoloId(protocoloId)
                .orElseThrow(() -> new RuntimeException("Boleto não encontrado para o protocolo: " + protocoloId));
        String boletoBase64 = boleto.getArquivoPdfBase64();
        byte[] boletoBytes = java.util.Base64.getDecoder().decode(boletoBase64);
        
        int tentativas = 0;
        boolean salvo = false;
        while (!salvo && tentativas < 3) {
            try (PDDocument document = PDDocument.load(boletoBytes)) {
                registraProtocoloService.registraEventoProtocolo(null,protocoloId,SubStatusEventosBoleto.EM_ANDAMENTO,TipoEvento.TESSERACT_EXTRAINDO_TEXTO);
                logger.info("[TesseractService] Documento PDF carregado com sucesso, iniciando extração de texto...");
                int pageCount = document.getNumberOfPages();
                StringBuilder extractedText = new StringBuilder();
                PDFRenderer pdfRenderer = new PDFRenderer(document);
                for (int page = 0; page < pageCount; page++) {
                    BufferedImage image = pdfRenderer.renderImageWithDPI(page, tessdataDpi, ImageType.RGB);
                    ITesseract tesseract = new Tesseract();
                    tesseract.setDatapath(tessdataPath);
                    String result = tesseract.doOCR(image);
                    extractedText.append("***************************************************\n");
                    extractedText.append("Página ").append(page + 1).append(":\n");
                    extractedText.append(result);
                }
                registraProtocoloService.registraEventoProtocolo(null,protocoloId,SubStatusEventosBoleto.EM_ANDAMENTO,TipoEvento.TESSERACT_SUCESO_TEXTO_EXTRAIDO);
                logger.info("[TesseractService] Texto extraído com sucesso: {}", extractedText.toString());

                // Atualiza o boleto com o texto extraído
                boleto.setStringExtraidaTesseract(extractedText.toString());
                boleto.setTesseractFinalizado(true);
                boletoRepository.save(boleto);
                tesseractProducer.sendProtocoloId(protocoloId);
                salvo = true;
            } catch (org.springframework.dao.OptimisticLockingFailureException | jakarta.persistence.OptimisticLockException e) {
                tentativas++;
                logger.warn("[TesseractService] Concorrência detectada ao salvar boleto. Tentando novamente (tentativa {}/3)...", tentativas);
                // Recarrega o registro atualizado do banco
                boleto = boletoRepository.findByProtocoloId(protocoloId)
                        .orElseThrow(() -> new RuntimeException("Boleto não encontrado para o protocolo: " + protocoloId));
                if (tentativas >= 3) {
                    logger.error("[TesseractService] Falha ao salvar boleto após 3 tentativas devido a concorrência.", e);
                    throw new RuntimeException("Erro ao processar boleto devido a concorrência: " + e.getMessage(), e);
                }
            } catch (Exception e) {
                logger.error("[TesseractService] Erro ao processar boleto: {}", e.getMessage());
                throw new RuntimeException("Erro ao processar boleto: " + e.getMessage(), e);
            }
        }
    }

}
