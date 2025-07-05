package br.com.consultdg.chatgpt_service.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "boleto")
public class BoletoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal valorBoleto;
    private String dataVencimento;
    private Boolean itensValidados;
    private String cnpjPagador;
    private String cnpjBeneficiario;
    private String numeroDocumento;
    private String codigoBarras;

    @OneToMany(mappedBy = "boleto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ItemEntity> itens;

    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getValorBoleto() { return valorBoleto; }
    public void setValorBoleto(BigDecimal valorBoleto) { this.valorBoleto = valorBoleto; }
    public String getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(String dataVencimento) { this.dataVencimento = dataVencimento; }
    public Boolean getItensValidados() { return itensValidados; }
    public void setItensValidados(Boolean itensValidados) { this.itensValidados = itensValidados; }
    public String getCnpjPagador() { return cnpjPagador; }
    public void setCnpjPagador(String cnpjPagador) { this.cnpjPagador = cnpjPagador; }
    public String getCnpjBeneficiario() { return cnpjBeneficiario; }
    public void setCnpjBeneficiario(String cnpjBeneficiario) { this.cnpjBeneficiario = cnpjBeneficiario; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }
    public List<ItemEntity> getItens() { return itens; }
    public void setItens(List<ItemEntity> itens) { this.itens = itens; }
}
