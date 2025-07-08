// package br.com.consultdg.chatgpt_service;

// import org.springframework.boot.context.event.ApplicationReadyEvent;
// import org.springframework.context.event.EventListener;
// import org.springframework.stereotype.Component;

// import br.com.consultdg.chatgpt_service.dto.BoletoDTO;
// import br.com.consultdg.chatgpt_service.service.ProcessPdfService;
// import br.com.consultdg.chatgpt_service.util.BoletoJsonConverter;
// import br.com.consultdg.chatgpt_service.service.ChatGptBoletoService;
// import br.com.consultdg.chatgpt_service.repository.BoletoRepository;
// import br.com.consultdg.chatgpt_service.model.BoletoEntity;
// import br.com.consultdg.chatgpt_service.model.ItemEntity;

// import java.nio.file.Files;
// import java.nio.file.Paths;
// import java.util.Arrays;
// import java.util.List;
// import java.io.File;
// import org.springframework.beans.factory.annotation.Value;

// @Component
// public class PdfStartupRunner{

//     private final ProcessPdfService processPdfService;
//     private final ChatGptBoletoService chatGptBoletoService;
//     private final BoletoRepository boletoRepository;

//     @Value("${path.base}")
//     private String basePath;

//     public PdfStartupRunner(ProcessPdfService processPdfService, ChatGptBoletoService chatGptBoletoService, BoletoRepository boletoRepository) {
//         this.processPdfService = processPdfService;
//         this.chatGptBoletoService = chatGptBoletoService;
//         this.boletoRepository = boletoRepository;
//     }

//     @EventListener(ApplicationReadyEvent.class)
//     public void run() {
//         System.out.println("[DEBUG] PdfStartupRunner iniciado!");
//         try {
//             // Buscar todos os arquivos PDF na raiz do diretório
//             File directory = new File(basePath);
//             File[] pdfFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));
//             if (pdfFiles == null || pdfFiles.length == 0) {
//                 System.out.println("Nenhum PDF encontrado em " + basePath);
//                 return;
//             }
//             for (File pdf : pdfFiles) {
//                 List<String> imagensBase64 = processPdfService.convertPdfToBase64Images(pdf);
//                 for (int i = 0; i < imagensBase64.size(); i++) {
//                     String fileName = String.format("%s_pagina_%04d.txt", removeExtension(pdf.getName()), i + 1);
//                     Files.write(Paths.get(basePath, fileName), imagensBase64.get(i).getBytes());

//                     // Chama a API do ChatGPT para cada base64
//                     List<BoletoDTO> boletos = chatGptBoletoService.analisarImagens(List.of(imagensBase64.get(i)));
//                     for (BoletoDTO dto : boletos) {
//                         // Converte DTO para Entity
//                         BoletoEntity entity = converterParaEntity(dto);
//                         boletoRepository.save(entity);
//                         System.out.println("Boleto persistido: " + entity.getNumeroDocumento());
//                     }
//                 }
//             }
//             System.out.println("Arquivos nome_arquivo_pagina_xxxx.txt gerados e boletos persistidos com sucesso em " + basePath);
//         } catch (Exception e) {
//             System.out.println("[ERRO] Falha ao processar PDFs: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }

//     private String removeExtension(String fileName) {
//         int lastDot = fileName.lastIndexOf('.');
//         return (lastDot == -1) ? fileName : fileName.substring(0, lastDot);
//     }

//     private BoletoEntity converterParaEntity(BoletoDTO dto) {
//         BoletoEntity entity = new BoletoEntity();
//         entity.setValorBoleto(dto.getValor_boleto());
//         entity.setDataVencimento(dto.getData_vencimento());
//         entity.setItensValidados(dto.getItens_validados());
//         entity.setCnpjPagador(dto.getCnpj_pagador());
//         entity.setCnpjBeneficiario(dto.getCnpj_beneficiario());
//         entity.setCodigoBarras(dto.getCodigo_barras());
//         entity.setNumeroDocumento(dto.getNumero_documento());
//         // Conversão dos itens
//         if (dto.getItens() != null && !dto.getItens().isEmpty()) {
//             List<ItemEntity> itensEntity = new java.util.ArrayList<>();
//             for (BoletoDTO.ItemDTO itemDTO : dto.getItens()) {
//                 ItemEntity itemEntity = new ItemEntity();
//                 itemEntity.setNome(itemDTO.getNome());
//                 itemEntity.setValor(itemDTO.getValor());
//                 itemEntity.setBoleto(entity); // Relaciona o item ao boleto
//                 itensEntity.add(itemEntity);
//             }
//             entity.setItens(itensEntity);
//         }
//         return entity;
//     }

//     //@EventListener(ApplicationReadyEvent.class)
//     public void testBoletoJsonConverter() {
//         System.out.println("[DEBUG] Teste de conversão JSON!");
//                 List<String> jsons = Arrays.asList
//                 ("```json\n{\n  \"valor_boleto\": 36543.20,\n  \"data_vencimento\": \"01/07/2025\",\n  \"itens\": [\n    {\"nome\": \"Aluguel Mínimo - 06/2025\", \"valor\": 16324.70},\n    {\"nome\": \"Aluguel Atual - R$\", \"valor\": 16324.70},\n    {\"nome\": \"Encargos Comuns - 07/2025\", \"valor\": 13979.77},\n    {\"nome\": \"Energia Elétrica - 06/2025\", \"valor\": 1758.30},\n    {\"nome\": \"Ar Condicionado - 06/2025\", \"valor\": 627.93},\n    {\"nome\": \"IPTU - 07/2025\", \"valor\": 425.60},\n    {\"nome\": \"Água - 06/2025\", \"valor\": 23.83},\n    {\"nome\": \"Dedetização - 06/2025\", \"valor\": 18.13},\n    {\"nome\": \"Fundo de Promoção - 07/2025\", \"valor\": 3264.94},\n    {\"nome\": \"Associação Lojistas - 07/2025\", \"valor\": 80.00}\n  ],\n  \"itens_validados\": true,\n  \"cnpj_pagador\": \"24.276.833/0001-48\",\n  \"cnpj_beneficiario\": \"51305808000118\",\n  \"numero_documento\": \"109/0001010420\",\n  \"codigo_barras\": \"341919090160104208293185986180009911290003654320\"\n}\n```");
//                 //("```json\\n{\\n  valor_boleto: 23359.35,\\n  \"data_vencimento\": \"01.05.2024\",\\n  \"itens\": [\\n    {\\n      \"nome\": \"Fundo de Promoção\",\\n      \"valor\": 2273.18\\n    },\\n    {\\n      \"nome\": \"Aluguel Mínimo\",\\n      \"valor\": 10824.65\\n    },\\n    {\\n      \"nome\": \"Encargos Comuns\",\\n      \"valor\": 7656.79\\n    },\\n    {\\n      \"nome\": \"Ar Condicionado\",\\n      \"valor\": 1310.67\\n    },\\n    {\\n      \"nome\": \"Energia Elétrica\",\\n      \"valor\": 1051.32\\n    },\\n    {\\n      \"nome\": \"Seguro\",\\n      \"valor\": 16.95\\n    },\\n    {\\n      \"nome\": \"Taxa de Incêndio\",\\n      \"valor\": 16.11\\n    },\\n    {\\n      \"nome\": \"IPTU\",\\n      \"valor\": 215.68\\n    }\\n  ],\\n  \"cnpj_pagador\": \"24.276.833/0001-48\",\\n  \"cnpj_beneficiario\": \"14.551.970/0001-90\",\\n  \"numero_documento\": \"PQM0405452\",\\n  \"nosso_numero\": \"109-00006201-3\",\\n  \"codigo_barras\": \"34191090080062013293685856310009197030002335935\"\\n}\\n```");
//         for (String json : jsons) {
//             //String j = json.replace("```json\\n", "").replace("\\n```", "").replace("\\n", "").replace("\"", "");
//             // String j = json;
//             String j = json.replaceAll("(?s)^```json\\s*|\\s*```$", "");;
//             System.out.println("JSON: " + j);
//             try {
//                 BoletoDTO dto = BoletoJsonConverter.fromJson(j);
//                 System.out.println(dto);
//             } catch (Exception e) {
//                 System.out.println("Falha ao converter: " + e.getMessage() + "\n" + e.getCause());
//             }
//         }
//     }
// }
