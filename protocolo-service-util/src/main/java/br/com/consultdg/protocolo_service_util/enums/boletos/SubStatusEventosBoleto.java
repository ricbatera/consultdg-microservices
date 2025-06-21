package br.com.consultdg.protocolo_service_util.enums.boletos;

public enum SubStatusEventosBoleto {
    CRIADO(1, "CRIADO", "Protocolo criado, aguardando processamento"),
    ABERTO(2, "ABERTO", "Protocolo aberto e em análise"),
    FINALIZADO(3, "FINALIZADO", "Protocolo finalizado com sucesso"),
    EM_ANDAMENTO(4, "EM_ANDAMENTO", "Protocolo em andamento"),
    ERRO(5, "ERRO", "Protocolo com erro no processamento");

    private final int indice;
    private final String nome;
    private final String descricao;

    SubStatusEventosBoleto(int indice, String nome, String descricao) {
        this.indice = indice;
        this.nome = nome;
        this.descricao = descricao;
    }

    public int getIndice() {
        return indice;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return nome + " (" + indice + "): " + descricao;
    }
}
