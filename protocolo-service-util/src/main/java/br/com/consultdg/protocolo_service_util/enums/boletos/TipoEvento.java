package br.com.consultdg.protocolo_service_util.enums.boletos;

public enum TipoEvento {
    SOLICITACAO_RECEBIDA(1, "SOLICITACAO_RECEBIDA", "Solicitação recebida para processamento"),
    GERANDO_TXT(2, "GERANDO_TXT", "Gerando arquivo TXT"),
    ENVIO_S3(3, "ENVIO_S3", "Envio do arquivo para o S3"),
    ENVIO_FTP(4, "ENVIO_FTP", "Envio do arquivo via FTP"),
    ENVIO_FTP_PDF(5, "ENVIO_FTP_PDF", "Envio do arquivo PDF via FTP"),
    ENVIO_FTP_TXT(6, "ENVIO_FTP_TXT", "Envio do arquivo TXT via FTP"),
    PROCESSAMENTO_OCR(7, "PROCESSAMENTO_OCR", "Processamento OCR do arquivo"),
    ENVIO_EMAIL(8, "ENVIO_EMAIL", "Envio do arquivo por e-mail"),
    GET_URL_S3(9, "GET_URL_S3", "Obtendo URL do S3 para upload");

    private final int indice;
    private final String nome;
    private final String descricao;

    TipoEvento(int indice, String nome, String descricao) {
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
