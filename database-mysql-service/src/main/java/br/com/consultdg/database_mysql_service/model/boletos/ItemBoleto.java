package br.com.consultdg.database_mysql_service.model.boletos;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class ItemBoleto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String nome;

    @Column(precision = 15, scale = 2)
    private BigDecimal valor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "boleto_id")
    private Boleto boleto;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public Boleto getBoleto() { return boleto; }
    public void setBoleto(Boleto boleto) { this.boleto = boleto; }
}
