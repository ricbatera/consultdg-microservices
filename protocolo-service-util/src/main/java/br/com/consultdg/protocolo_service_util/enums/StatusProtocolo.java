package br.com.consultdg.protocolo_service_util.enums;

public enum StatusProtocolo {
    CRIADO(1, "CRIADO", "Protocolo criado com sucesso"),
    ABERTO(2, "ABERTO", "Protocolo aberto com sucesso"),
    EM_ANDAMENTO(3, "EM_ANDAMENTO", "Protocolo está em andamento"),
    COM_ERRO(4, "COM_ERRO", "Protocolo está no estado de ERRO"),
    FINALIZADO(5, "FINALIZADO", "Protocolo finalizado com sucesso");

    private final int indice;
    private final String nome;
    private final String descricao;

    StatusProtocolo(int indice, String nome, String descricao) {
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
}