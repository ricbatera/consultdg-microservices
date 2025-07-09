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
    GET_URL_S3(9, "GET_URL_S3", "Obtendo URL do S3 para upload"),
    PROCESSA_BOLETO_NOVO(10, "PROCESSA_BOLETO_NOVO", "Processamento de novo boleto"),
    PROCESSA_BOLETO_CRIADO(11, "PROCESSA_BOLETO_CRIADO", "Boleto criado com sucesso"),
    PROCESSA_BOLETO_ERRO(12, "PROCESSA_BOLETO_ERRO", "Erro ao processar boleto"),
    PROCESSA_BOLETO_ENVIADO_FILA(13, "PROCESSA_BOLETO_ENVIADO_FILA", "Boleto enviado para a fila de processamento"),
    PROCESSA_BOLETO_PROCESSANDO(14, "PROCESSA_BOLETO_PROCESSANDO", "Boleto em processamento"),
    PROCESSA_BOELETO_FINALIZADO(15, "PROCESSA_BOLETO_FINALIZADO", "Boleto processado e finalizado"),
    PROCESSA_BOLETO_ENVIO_CHATGPT(16, "PROCESSA_BOLETO_ENVIO_CHATGPT", "Boleto enviado para o ChatGPT para processamento"),
    PROCESSA_BOLETO_RECEBIDO_CHATGPT(17, "PROCESSA_BOLETO_RECEBIDO_CHATGPT", "Resposta do ChatGPT recebida para o boleto"),
    PROCESSA_BOLETO_EM_ANDAMENTO_CHATGPT(18, "PROCESSA_BOLETO_EM_ANDAMENTO_CHATGPT", "Processamento do boleto em andamento com ChatGPT"),
    PROCESSA_BOLETO_FINALIZADO_CHATGPT(19, "PROCESSA_BOLETO_FINALIZADO_CHATGPT", "Boleto processado e finalizado com ChatGPT"),
    PROCESSA_BOLETO_ERRO_CHATGPT(20, "PROCESSA_BOLETO_ERRO_CHATGPT", "Erro ao processar boleto com ChatGPT"),
    TESSERACT_INICIADO(21, "TESSERACT_INICIADO", "Início do processamento com Tesseract"),
    TESSERACT_BUSCANDO_CODIGO_DE_BARRAS(22, "TESSERACT_BUSCANDO_CODIGO_DE_BARRAS", "Buscando código de barras com Tesseract"),
    TESSERACT_BUSCANDO_DATA_VENCIMENTO(23, "TESSERACT_BUSCANDO_DATA_VENCIMENTO", "Buscando data de vencimento com Tesseract"),
    TESSERACT_BUSCANDO_VALOR(24, "TESSERACT_BUSCANDO_VALOR", "Buscando valor do boleto com Tesseract"),
    TESSERACT_BUSCANDO_ITENS(25, "TESSERACT_BUSCANDO_ITENS", "Buscando itens do boleto com Tesseract"),
    TESSERACT_VALIDANDO_ITENS(26, "TESSERACT_VALIDANDO_ITENS", "Validando itens do boleto com Tesseract"),
    TESSERACT_FINALIZADO(27, "TESSERACT_FINALIZADO", "Processamento com Tesseract finalizado"),
    TESSERACT_ERRO(28, "TESSERACT_ERRO", "Erro ao processar boleto com Tesseract"),
    TESSERACT_EXTRAINDO_TEXTO(29, "TESSERACT_EXTRAINDO_TEXTO", "Extraindo texto do boleto com Tesseract"),
    TESSERACT_SUCESO_TEXTO_EXTRAIDO(30, "TESSERACT_SUCESO_TEXTO_EXTRAIDO", "Texto extraído com sucesso do boleto com Tesseract")
    ;

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
