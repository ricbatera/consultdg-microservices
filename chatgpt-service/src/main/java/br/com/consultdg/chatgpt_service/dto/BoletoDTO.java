package br.com.consultdg.chatgpt_service.dto;

import java.math.BigDecimal;
import java.util.List;

public class BoletoDTO {
    private BigDecimal valor_boleto;
    private String data_vencimento;
    private List<ItemDTO> itens;
    private Boolean itens_validados;
    private String cnpj_pagador;
    private String cnpj_beneficiario;
    private String numero_documento;
    private String codigo_barras;

    // Getters e setters
    public BigDecimal getValor_boleto() { return valor_boleto; }
    public void setValor_boleto(BigDecimal valor_boleto) { this.valor_boleto = valor_boleto; }
    public String getData_vencimento() { return data_vencimento; }
    public void setData_vencimento(String data_vencimento) { this.data_vencimento = data_vencimento; }
    public List<ItemDTO> getItens() { return itens; }
    public void setItens(List<ItemDTO> itens) { this.itens = itens; }
    public Boolean getItens_validados() { return itens_validados; }
    public void setItens_validados(Boolean itens_validados) { this.itens_validados = itens_validados; }
    public String getCnpj_pagador() { return cnpj_pagador; }
    public void setCnpj_pagador(String cnpj_pagador) { this.cnpj_pagador = cnpj_pagador; }
    public String getCnpj_beneficiario() { return cnpj_beneficiario; }
    public void setCnpj_beneficiario(String cnpj_beneficiario) { this.cnpj_beneficiario = cnpj_beneficiario; }
    public String getNumero_documento() { return numero_documento; }
    public void setNumero_documento(String numero_documento) { this.numero_documento = numero_documento; }
    public String getCodigo_barras() { return codigo_barras; }
    public void setCodigo_barras(String codigo_barras) { this.codigo_barras = codigo_barras; }

    @Override
    public String toString() {
        return "BoletoDTO{" +
                "valor_boleto=" + valor_boleto +
                ", data_vencimento='" + data_vencimento + '\'' +
                ", itens=" + itens +
                ", itens_validados=" + itens_validados +
                ", cnpj_pagador='" + cnpj_pagador + '\'' +
                ", cnpj_beneficiario='" + cnpj_beneficiario + '\'' +
                ", numero_documento='" + numero_documento + '\'' +
                ", codigo_barras='" + codigo_barras + '\'' +
                '}';
    }

    public static class ItemDTO {
        private String nome;
        private BigDecimal valor;
        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }
        public BigDecimal getValor() { return valor; }
        public void setValor(BigDecimal valor) { this.valor = valor; }

        @Override
        public String toString() {
            return "ItemDTO{" +
                    "nome='" + nome + '\'' +
                    ", valor=" + valor +
                    '}';
        }
    }
}
