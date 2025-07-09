package br.com.consultdg.chatgpt_service.dto;

import java.math.BigDecimal;
import java.util.List;

public class BoletoDTO {
    private Long id;
    private String nome_arquivo;
    private String codigo_barras;
    private String data_vencimento;
    private BigDecimal valor_boleto;
    private String numero_documento;
    private String cnpj_pagador;
    private String cnpj_beneficiario;
    private String tipo_boleto;
    private String data_criacao;
    private String arquivo_pdf_base64;
    private String arquivo_txt_base64;
    private List<ItemDTO> itens;
    private Boolean itens_validados;
    private Long protocolo_id;
    private String json;

    public String getJson() { return json; }
    public void setJson(String json) { this.json = json; }

    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome_arquivo() { return nome_arquivo; }
    public void setNome_arquivo(String nome_arquivo) { this.nome_arquivo = nome_arquivo; }

    public String getCodigo_barras() { return codigo_barras; }
    public void setCodigo_barras(String codigo_barras) { this.codigo_barras = codigo_barras; }

    public String getData_vencimento() { return data_vencimento; }
    public void setData_vencimento(String data_vencimento) { this.data_vencimento = data_vencimento; }

    public BigDecimal getValor_boleto() { return valor_boleto; }
    public void setValor_boleto(BigDecimal valor_boleto) { this.valor_boleto = valor_boleto; }

    public String getNumero_documento() { return numero_documento; }
    public void setNumero_documento(String numero_documento) { this.numero_documento = numero_documento; }

    public String getCnpj_pagador() { return cnpj_pagador; }
    public void setCnpj_pagador(String cnpj_pagador) { this.cnpj_pagador = cnpj_pagador; }

    public String getCnpj_beneficiario() { return cnpj_beneficiario; }
    public void setCnpj_beneficiario(String cnpj_beneficiario) { this.cnpj_beneficiario = cnpj_beneficiario; }

    public String getTipo_boleto() { return tipo_boleto; }
    public void setTipo_boleto(String tipo_boleto) { this.tipo_boleto = tipo_boleto; }

    public String getData_criacao() { return data_criacao; }
    public void setData_criacao(String data_criacao) { this.data_criacao = data_criacao; }

    public String getArquivo_pdf_base64() { return arquivo_pdf_base64; }
    public void setArquivo_pdf_base64(String arquivo_pdf_base64) { this.arquivo_pdf_base64 = arquivo_pdf_base64; }

    public String getArquivo_txt_base64() { return arquivo_txt_base64; }
    public void setArquivo_txt_base64(String arquivo_txt_base64) { this.arquivo_txt_base64 = arquivo_txt_base64; }

    public List<ItemDTO> getItens() { return itens; }
    public void setItens(List<ItemDTO> itens) { this.itens = itens; }

    public Boolean getItens_validados() { return itens_validados; }
    public void setItens_validados(Boolean itens_validados) { this.itens_validados = itens_validados; }

    public Long getProtocolo_id() { return protocolo_id; }
    public void setProtocolo_id(Long protocolo_id) { this.protocolo_id = protocolo_id; }

    @Override
    public String toString() {
        return "BoletoDTO{" +
                "id=" + id +
                ", nome_arquivo='" + nome_arquivo + '\'' +
                ", codigo_barras='" + codigo_barras + '\'' +
                ", data_vencimento='" + data_vencimento + '\'' +
                ", valor_boleto=" + valor_boleto +
                ", numero_documento='" + numero_documento + '\'' +
                ", cnpj_pagador='" + cnpj_pagador + '\'' +
                ", cnpj_beneficiario='" + cnpj_beneficiario + '\'' +
                ", tipo_boleto='" + tipo_boleto + '\'' +
                ", data_criacao='" + data_criacao + '\'' +
                ", arquivo_pdf_base64='" + arquivo_pdf_base64 + '\'' +
                ", arquivo_txt_base64='" + arquivo_txt_base64 + '\'' +
                ", itens=" + itens +
                ", itens_validados=" + itens_validados +
                ", protocolo_id=" + protocolo_id +
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
