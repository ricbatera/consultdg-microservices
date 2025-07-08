package br.com.consultdg.database_mysql_service.model.boletos;

import jakarta.persistence.*;

@Entity
@Table(name = "imagem_base64")
public class ImagemBase64 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "imagem_base64", nullable = false, columnDefinition = "LONGTEXT")
    private String imagemBase64;

    @Column(name = "formato_imagem", nullable = false, length = 10)
    private String formatoImagem;

    @Column(name = "numero_pagina", nullable = false)
    private Integer numeroPagina;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boleto_id", nullable = false)
    private Boleto boleto;

    public ImagemBase64() {
    }

    public ImagemBase64(String imagemBase64, String formatoImagem, Integer numeroPagina, Boleto boleto) {
        this.imagemBase64 = imagemBase64;
        this.formatoImagem = formatoImagem;
        this.numeroPagina = numeroPagina;
        this.boleto = boleto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImagemBase64() {
        return imagemBase64;
    }

    public void setImagemBase64(String imagemBase64) {
        this.imagemBase64 = imagemBase64;
    }

    public String getFormatoImagem() {
        return formatoImagem;
    }

    public void setFormatoImagem(String formatoImagem) {
        this.formatoImagem = formatoImagem;
    }

    public Integer getNumeroPagina() {
        return numeroPagina;
    }

    public void setNumeroPagina(Integer numeroPagina) {
        this.numeroPagina = numeroPagina;
    }

    public Boleto getBoleto() {
        return boleto;
    }

    public void setBoleto(Boleto boleto) {
        this.boleto = boleto;
    }
}
