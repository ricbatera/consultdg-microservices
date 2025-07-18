package br.com.consultdg;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // String codigoBarras = "836300000081422780005289571201492784200000000000";
        // String codigoBarras2 = "836300000814227800052895712014927842000000000000";
        // BigDecimal valor = getValorBoletoServico(codigoBarras2);
        // System.out.println("Valor do boleto de serviço: " + valor);
        teste();
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

    private static void teste() {
        List<String> nomes = new ArrayList<>();
        nomes.add("Ricardo");
        nomes.add("Alves");
        nomes.add("Roberto");
        nomes.add("Rodolfo");

        nomes.stream().filter(a-> a.contains("o")).map(String::toUpperCase).forEach(System.out::println);
    }
}