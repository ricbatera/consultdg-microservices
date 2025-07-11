package br.com.consultdg.database_mysql_service.model.boletos;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import br.com.consultdg.database_mysql_service.enums.TipoBoleto;
import br.com.consultdg.database_mysql_service.model.Protocolo;

@Entity
@Table(name = "boleto")
public class Boleto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Integer version;

    @Column(length = 255, name = "nome_arquivo")
    private String nomeArquivo;

    @Column(length = 100, name = "codigo_barras")
    private String codigoBarras;

    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;

    @Column(precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(length = 50, name = "numero_documento")
    private String numeroDocumento;

    @Column(length = 20, name = "cnpj_pagador")
    private String cnpjPagador;

    @Column(length = 20, name = "cnpj_recebedor")
    private String cnpjRecebedor;

    @Enumerated(EnumType.STRING)
    @Column(length = 30, name = "tipo_boleto")
    private TipoBoleto tipoBoleto;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Lob
    @Column(columnDefinition = "LONGTEXT", name = "arquivo_pdf_base64")
    private String arquivoPdfBase64;

    @Lob
    @Column(columnDefinition = "LONGTEXT", name = "arquivo_txt_base64")
    private String arquivoTxtBase64;

    @OneToMany(mappedBy = "boleto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemBoleto> itens;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protocolo_id")
    private Protocolo protocolo;

    @Column(name = "itens_validados")
    private Boolean itensValidados;

    @Column(name = "codigo_barras_validado")
    private Boolean codigoBarrasValidado = false;

    @Column(name = "data_vencimento_validado")
    private Boolean dataVencimentoValidado = false;

    @Column(name = "valor_validado")
    private Boolean valorValidado = false;


    @Lob
    @Column(columnDefinition = "LONGTEXT", name = "json_chatgpt")
    private String json;

    @Lob
    @Column(columnDefinition = "LONGTEXT", name = "string_extraida_tesseract")
    private String stringExtraidaTesseract;

    @Column(name = "tesseract_finalizado")
    private Boolean tesseractFinalizado = false;

    @Column(name = "chatgpt_finalizado")
    private Boolean chatgptFinalizado = false;



    public String getJson() { return json; }
    public void setJson(String json) { this.json = json; }

    public String getStringExtraidaTesseract() { return stringExtraidaTesseract; }
    public void setStringExtraidaTesseract(String stringExtraidaTesseract) { this.stringExtraidaTesseract = stringExtraidaTesseract; }

    public Boolean getTesseractFinalizado() { return tesseractFinalizado; }
    public void setTesseractFinalizado(Boolean tesseractFinalizado) { this.tesseractFinalizado = tesseractFinalizado; }

    public Boolean getChatgptFinalizado() { return chatgptFinalizado; }
    public void setChatgptFinalizado(Boolean chatgptFinalizado) { this.chatgptFinalizado = chatgptFinalizado; }

    public Boolean getItensValidados() { return itensValidados;}
    public void setItensValidados(Boolean itensValidados) { this.itensValidados = itensValidados;}

    public Boolean getCodigoBarrasValidado() { return codigoBarrasValidado; }
    public void setCodigoBarrasValidado(Boolean codigoBarrasValidado) { this.codigoBarrasValidado = codigoBarrasValidado; }

    public Boolean getDataVencimentoValidado() { return dataVencimentoValidado; }
    public void setDataVencimentoValidado(Boolean dataVencimentoValidado) { this.dataVencimentoValidado = dataVencimentoValidado; }

    public Boolean getValorValidado() { return valorValidado; }
    public void setValorValidado(Boolean valorValidado) { this.valorValidado = valorValidado; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeArquivo() { return nomeArquivo; }
    public void setNomeArquivo(String nomeArquivo) { this.nomeArquivo = nomeArquivo; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getCnpjPagador() { return cnpjPagador; }
    public void setCnpjPagador(String cnpjPagador) { this.cnpjPagador = cnpjPagador; }

    public String getCnpjRecebedor() { return cnpjRecebedor; }
    public void setCnpjRecebedor(String cnpjRecebedor) { this.cnpjRecebedor = cnpjRecebedor; }

    public TipoBoleto getTipoBoleto() { return tipoBoleto; }
    public void setTipoBoleto(TipoBoleto tipoBoleto) { this.tipoBoleto = tipoBoleto; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public String getArquivoPdfBase64() { return arquivoPdfBase64; }
    public void setArquivoPdfBase64(String arquivoPdfBase64) { this.arquivoPdfBase64 = arquivoPdfBase64; }

    public String getArquivoTxtBase64() { return arquivoTxtBase64; }
    public void setArquivoTxtBase64(String arquivoTxtBase64) { this.arquivoTxtBase64 = arquivoTxtBase64; }

    public List<ItemBoleto> getItens() { return itens; }
    public void setItens(List<ItemBoleto> itens) { this.itens = itens; }

    public Protocolo getProtocolo() { return protocolo; }
    public void setProtocolo(Protocolo protocolo) { this.protocolo = protocolo; }
}
