package br.com.consultdg.api_boleto_service.dto;

public class NovoBoletoRequest {
    private String nomeArquivo;
    private String arquivoBase64;

    public NovoBoletoRequest() {}

    public NovoBoletoRequest(String nomeArquivo, String arquivoBase64) {
        this.nomeArquivo = nomeArquivo;
        this.arquivoBase64 = arquivoBase64;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getArquivoBase64() {
        return arquivoBase64;
    }

    public void setArquivoBase64(String arquivoBase64) {
        this.arquivoBase64 = arquivoBase64;
    }
}
