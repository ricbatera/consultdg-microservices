package br.com.consultdg.chatgpt_service.service;

import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    public static final String FORMAT_JPEG = "jpeg";
    public static final String FORMAT_PNG = "png";
    public static final String FORMAT_DEFAULT = FORMAT_PNG; // Troque aqui para FORMAT_JPEG ou FORMAT_PNG

    @Value("${path.base}")
    private String basePath;

    public ProcessPdfService() {
        // Garante que o ImageIO reconheça JPEG
        ImageIO.scanForPlugins();
    }

    public List<String> convertAllPdfsToBase64Images() {
        File directory = new File(basePath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new RuntimeException("Diretório base não encontrado: " + basePath);
        }
        List<File> pdfFiles = FileUtils.listFiles(directory, new String[]{"pdf"}, false)
                .stream().collect(Collectors.toList());
        List<String> base64Images = new ArrayList<>();
        for (File pdf : pdfFiles) {
            base64Images.addAll(convertPdfToBase64Images(pdf, FORMAT_DEFAULT));
        }
        return base64Images;
    }

    public List<String> convertPdfToBase64Images(File pdfFile) {
        return convertPdfToBase64Images(pdfFile, FORMAT_DEFAULT);
    }

    public List<String> convertPdfToBase64Images(File pdfFile, String format) {
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
            throw new RuntimeException("Erro ao processar PDF: " + pdfFile.getName(), e);
        }
        return imagesBase64;
    }
}
