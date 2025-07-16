package br.com.consultdg.processa_boleto_service.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.consultdg.database_mysql_service.enums.TipoBoleto;
import br.com.consultdg.database_mysql_service.model.boletos.Boleto;
import br.com.consultdg.database_mysql_service.model.boletos.ItemBoleto;
import br.com.consultdg.database_mysql_service.repository.boletos.BoletoRepository;
import br.com.consultdg.processa_boleto_service.util.UtilValidaBoletos;
import br.com.consultdg.protocolo_service_util.enums.StatusProtocolo;
import br.com.consultdg.protocolo_service_util.enums.boletos.SubStatusEventosBoleto;
import br.com.consultdg.protocolo_service_util.enums.boletos.TipoEvento;

@Service
public class ValidaBoletoService {
    Logger logger = LoggerFactory.getLogger(ValidaBoletoService.class);

    @Autowired
    private BoletoRepository boletoRepository;

    @Autowired
    private RegistraProtocoloService registraProtocoloService;

    Boleto boleto;

    private final String REGEX_PADRAO_CODIGO_BARRAS = "((\\d{5}(\\.)?(\\s)?\\d{5}(\\.)?(\\s)?\\d{5}(\\s)?(\\.)?\\d{6}(\\.)?(\\s)?\\d{5}(\\.)?(\\s)?\\d{6}(\\.)?(\\s)?\\d(\\.)?(\\s)?\\d{14})|(\\d{11}\\-?\\s?\\.?\\d\\s?\\.?\\-?\\d{11}\\-?\\.?\\s?\\d\\s\\d{11}\\-?\\s?\\.?\\d\\s\\d{11}\\-?\\.?\\s?\\d)|(\\d{12}\\s\\d{12}\\s\\d{12}\\s\\d{12}))";

    public void validaBoleto(Long protocoloId) {
        try {
            boleto = boletoRepository.findByProtocoloId(protocoloId)
                    .orElseThrow(
                            () -> new RuntimeException("Boleto não encontrado para o protocolo ID: " + protocoloId));
            // verifica se o boleto já foi processado pelo Tesseract e ChatGPT
            if (boleto.getChatgptFinalizado() && boleto.getTesseractFinalizado()) {
                logger.info("Boleto processado pelo Tesseract e ChatGPT. Validando boleto. Protocolo: {}", protocoloId);
                boolean codBarrasIsValid = validaCodigoBarras(boleto);
                boolean dataVencimentoIsValid = validaDataVencimento(boleto);
                boolean valorIsValid = validaValor(boleto);
                boolean itensIsValid = validaItens(boleto);
                if (codBarrasIsValid && dataVencimentoIsValid && valorIsValid && itensIsValid) {
                    logger.info("Boleto válido. Protocolo: {}", protocoloId);
                    registraProtocoloService.atualizaProtocolo(protocoloId, StatusProtocolo.FINALIZADO, null);
                } else {
                    logger.warn("Boleto inválido. Protocolo: {}", protocoloId);
                    registraProtocoloService.atualizaProtocolo(protocoloId, StatusProtocolo.BOLETO_NAO_VALIDADO, getItemNaoValidado());
                }
                boletoRepository.save(boleto);
            } else {
                logger.warn("Boleto ainda não processado pelo Tesseract ou ChatGPT. Aguarde. Protocolo: {}",
                        protocoloId);
                return;
            }

        } catch (RuntimeException e) {
            logger.error("Erro ao validar boleto: {}", e.getMessage());
            logger.error("Pilha de erros: {}", e);
            registraProtocoloService.registraEventoErroProtocolo(null, protocoloId, SubStatusEventosBoleto.ERRO,
                    TipoEvento.PROCESSA_BOLETO_ERRO,
                    "Erro ao validar boleto: " + e.getMessage());
            registraProtocoloService.atualizaProtocolo(protocoloId, StatusProtocolo.COM_ERRO,
                    "Erro ao validar boleto: " + e.getMessage());
        }
    }

    private String getItemNaoValidado(){
        StringBuilder sb = new StringBuilder();
        sb.append("O que não foi validado no boleto: ");
        if (!validaCodigoBarras(boleto)) {
            sb.append("\nCódigo de barras inválido. ");
        }
        if (!validaDataVencimento(boleto)) {
            sb.append("\nData de vencimento inválida. ");
        }
        if (!validaValor(boleto)) {
            sb.append("\nValor inválido. ");
        }
        if (!validaItens(boleto)) {
            sb.append("\nItens inválidos ou a somatória está incorreta. ");
        }
        return sb.toString();
    }

    private boolean validaCodigoBarras(Boleto boleto) {
        String codBarrasString = UtilValidaBoletos.getPadrao(boleto.getStringExtraidaTesseract(),
                REGEX_PADRAO_CODIGO_BARRAS);
        codBarrasString = UtilValidaBoletos.removePontosEspaco(codBarrasString);
        logger.info("Código de barras encontrado na string do Tesseract: {}", codBarrasString);
        if (codBarrasString != null && codBarrasString.length() == 47) {
            boleto.setTipoBoleto(TipoBoleto.BOLETO_COMUM);
            logger.info("[BOLETO COMUM] Código de barras encontrado na string do Tesseract: {}", codBarrasString);
        } else if (codBarrasString != null && codBarrasString.length() == 48) {
            boleto.setTipoBoleto(TipoBoleto.BOLETO_SERVICO);
            logger.info("[BOLETO SERVIÇO] Código de barras encontrado na string do Tesseract: {}", codBarrasString);
        }

        String codBarrasChatGpt = boleto.getCodigoBarras();
        if (codBarrasChatGpt != null && codBarrasChatGpt.length() == 47 && codBarrasChatGpt.charAt(0) != '8') {
            boleto.setTipoBoleto(TipoBoleto.BOLETO_COMUM);
            logger.info("[BOLETO COMUM] Código de barras do ChatGPT: {}", codBarrasChatGpt);
        } else if (codBarrasChatGpt != null && codBarrasChatGpt.length() == 48) {
            boleto.setTipoBoleto(TipoBoleto.BOLETO_SERVICO);
            logger.info("[BOLETO SERVIÇO] Código de barras do ChatGPT: {}", codBarrasChatGpt);
        }

        logger.info("Código de barras do ChatGPT: {}", codBarrasChatGpt);
        if (codBarrasChatGpt != null && codBarrasChatGpt.equals(codBarrasString)) {
            logger.info("Códigos de barras são iguais.");
            boleto.setCodigoBarrasValidado(true);
            return true;
        } else if (codBarrasString != null) {
            boleto.setCodigoBarras(codBarrasString);
            boleto.setCodigoBarrasValidado(true);
            return true;
        } else {
            boleto.setCodigoBarrasValidado(false);
            return false;
        }
    }

    private boolean validaDataVencimento(Boleto boleto) {
        LocalDate dataVencimento = boleto.getDataVencimento();
        logger.info("Data de vencimento do boleto extraida do ChatGpt: {}", dataVencimento);
        LocalDate dataVencimentoCodBarras = null;
        if (boleto.getTipoBoleto() != null && boleto.getTipoBoleto().equals(TipoBoleto.BOLETO_COMUM)) {
            dataVencimentoCodBarras = UtilValidaBoletos.getDataVencimento(boleto.getCodigoBarras());

            logger.info("Data de vencimento do boleto extraida do código de barras: {}", dataVencimentoCodBarras);

            if (dataVencimento != null && dataVencimentoCodBarras != null) {
                boolean isValid = dataVencimento.equals(dataVencimentoCodBarras);
                if (!isValid) {
                    boleto.setDataVencimento(dataVencimentoCodBarras);
                    boleto.setDataVencimentoValidado(true);
                    return true;
                } else
                    boleto.setDataVencimentoValidado(true);
                return true;
            } else if (dataVencimento != null) {
                boleto.setDataVencimentoValidado(true);
                return true;
            } else if (dataVencimentoCodBarras != null) {
                boleto.setDataVencimento(dataVencimentoCodBarras);
                boleto.setDataVencimentoValidado(true);
                return true;
            } else {
                boleto.setDataVencimentoValidado(false);
                return false;
            }
        }else if (boleto.getTipoBoleto() != null && boleto.getTipoBoleto().equals(TipoBoleto.BOLETO_SERVICO)) {
            // Para boletos de serviço, não valida a data de vencimento
            logger.info("Boleto de serviço, não valida a data de vencimento.");
            boleto.setDataVencimentoValidado(true);
            return true;
        } else return false; // Se o tipo de boleto não for reconhecido, retorna falso
    }

    private boolean validaValor(Boleto boleto) {
        BigDecimal valorChatGpt = boleto.getValor();
        logger.info("Valor do boleto extraido do ChatGPT: {}", valorChatGpt);
        BigDecimal valorCodBarras = null;
        if (boleto.getTipoBoleto() != null && boleto.getTipoBoleto().equals(TipoBoleto.BOLETO_COMUM)) {
            valorCodBarras = UtilValidaBoletos.getValorBoletoComum(boleto.getCodigoBarras());
        } else if (boleto.getTipoBoleto() != null && boleto.getTipoBoleto().equals(TipoBoleto.BOLETO_SERVICO)) {
            valorCodBarras = UtilValidaBoletos.getValorBoletoServico(boleto.getCodigoBarras());
        }
        logger.info("Valor do boleto extraido do código de barras: {}", valorCodBarras);
        if (valorChatGpt != null && valorCodBarras != null) {
            boolean isValid = valorChatGpt.equals(valorCodBarras);
            if (!isValid && boleto.getCodigoBarrasValidado()) { // aqui eu só seto o valor se o código de barras foi validado
                boleto.setValor(valorCodBarras);
                boleto.setValorValidado(true);
                return true;
            } else { // aqui eu assumo que o valor do ChatGPT é o correto
                boleto.setValorValidado(true);
                return true;
            }
        } else if (valorChatGpt != null) {
            boleto.setValorValidado(true);
            return true;
        } else if (valorCodBarras != null) {
            boleto.setValor(valorCodBarras);
            boleto.setValorValidado(true);
            return true;
        }
        boleto.setValorValidado(false);
        return false;
    }

    private boolean validaItens(Boleto boleto) {
        if (boleto.getTipoBoleto() != null && boleto.getTipoBoleto().equals(TipoBoleto.BOLETO_SERVICO)) {
            logger.info("Removendo itens do boleto de serviço.");
            boleto.getItens().clear();
            boleto.setItensValidados(true);
            return true; // Não valida itens para boletos de serviço
        }
        BigDecimal valorBoleto = boleto.getValor();
        List<ItemBoleto> itens = boleto.getItens();
        if (itens != null && !itens.isEmpty()) {
            BigDecimal valorTotalItens = itens.stream()
                    .map(ItemBoleto::getValor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            logger.info("Valor total dos itens: {}", valorTotalItens);
            if (valorBoleto.compareTo(valorTotalItens) == 0) {
                boleto.setItensValidados(true);
                return true;
            } else {
                boleto.setItensValidados(false);
                return false;
            }
        } else {
            boleto.setItensValidados(false);
            return false;
        }

    }

}
