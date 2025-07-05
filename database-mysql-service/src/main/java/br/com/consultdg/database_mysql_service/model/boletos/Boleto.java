package br.com.consultdg.database_mysql_service.model.boletos;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import br.com.consultdg.database_mysql_service.enums.TipoBoleto;
import br.com.consultdg.database_mysql_service.model.Protocolo;

@Entity
public class Boleto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String nomeArquivo;

    @Column(length = 100)
    private String codigoBarras;

    @Column
    private LocalDate dataVencimento;

    @Column(precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(length = 50)
    private String numeroDocumento;

    @Column(length = 20)
    private String cnpjPagador;

    @Column(length = 20)
    private String cnpjRecebedor;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private TipoBoleto tipoBoleto;

    @Column
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String arquivoPdfBase64;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String arquivoTxtBase64;

    @OneToMany(mappedBy = "boleto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemBoleto> itens;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "protocolo_id")
    private Protocolo protocolo;

    // Getters e Setters
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
