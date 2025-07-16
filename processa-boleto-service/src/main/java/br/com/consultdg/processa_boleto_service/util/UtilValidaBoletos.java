package br.com.consultdg.processa_boleto_service.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UtilValidaBoletos {

    public static String getPadrao(String dados, String regex) {
        if (dados == null || regex == null) {
            return null;
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(dados);
        if (matcher.find()) {
            String res = matcher.group(1);
            return res;
        }
        return null;
    }

    public static String removePontosEspaco(String codigoBarras) {
        if (codigoBarras == null)return null;
        
        return codigoBarras.replaceAll("[.\\s\\-]", "");
    }

    public static LocalDate getDataVencimento(String dados) {
        if (dados.length() == 47) {
            dados = dados.substring(33, 37);
            int i = Integer.parseInt(dados);
            String dataEncontrada = null;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            if (i >= 8999) {
                LocalDate initialDate = LocalDate.parse("1997-10-07", formatter);
                LocalDate newDate = initialDate.plusDays(Integer.parseInt(dados));
                return newDate;
            } else {
                i = i - 1000;
                LocalDate initialDate = LocalDate.parse("2025-02-22", formatter);
                LocalDate newDate = initialDate.plusDays(i);
                return newDate;
                // dataEncontrada = newDate.format(formatter);
            }

        } else {
            return null;
        }
    }

    public static BigDecimal getValorBoletoComum(String codigoBarras) {
        if (codigoBarras.length() == 47) {
            codigoBarras = codigoBarras.substring(codigoBarras.length() - 10);
            codigoBarras = codigoBarras.replaceFirst("^0+", "");

            // FORMATA O VALOR COM "." PARA SEPARAR REAIS DE CENTAVOS
            String reais = codigoBarras.substring(0, codigoBarras.length() - 2);
            String centavos = codigoBarras.substring(codigoBarras.length() - 2);
            return new BigDecimal(reais + "." + centavos);

        } else {
            return null;
        }
    }

    public static BigDecimal getValorBoletoServico(String codBarras) {
        if (codBarras.length() == 48) {
            String valor = "";
            valor = codBarras.substring(0, 11) + codBarras.substring(12);
            valor = valor.substring(5, 15);
            valor = valor.replaceFirst("^0+", "");
            String reais = valor.substring(0, valor.length() - 2);
            String centavos = valor.substring(valor.length() - 2);
            System.out.println("Valor encontrado: R$ " + reais + "," + centavos);
            return new BigDecimal(reais + "." + centavos);
        } else {
            return null;
        }
    }
}
