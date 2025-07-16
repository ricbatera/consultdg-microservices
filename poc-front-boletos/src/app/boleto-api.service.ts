import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class BoletoApiService {
  private apiUrl = 'http://localhost:8765/api/boletos';

  async enviarBoleto(nomeArquivo: string, arquivoBase64: string): Promise<any> {
    const response = await fetch(`${this.apiUrl}/processar-novo-boleto`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ nomeArquivo, arquivoBase64 }),
    });
    if (!response.ok) {
      throw new Error('Erro ao enviar boleto: ' + response.statusText);
    }
    return response.json();
  }

  async hello(): Promise<any> {
    const response = await fetch(`${this.apiUrl}/hello`);
    if (!response.ok) {
      throw new Error('Erro ao chamar hello: ' + response.statusText);
    }
    return response.text();
  }

  async getUrlS3(nomeArquivo: string): Promise<string> {
    const url = `${this.apiUrl}/get-url-to-upload-s3?fileName=${encodeURIComponent(nomeArquivo)}`;
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error('Erro ao obter URL S3: ' + response.statusText);
    }
    const data = await response.json();
    return data.url || data; // compatível com diferentes formatos de resposta
  }
  async downloadPdf(id: number): Promise<void> {
    const response = await fetch(`${this.apiUrl}/download-pdf?id=${id}`);
    if (!response.ok) {
      throw new Error('Boleto não encontrado ou erro no download.');
    }
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = this.getFileNameFromResponse(response) || `boleto-${id}.pdf`;
    document.body.appendChild(a);
    a.click();
    a.remove();
    window.URL.revokeObjectURL(url);
  }

  async downloadImagensBoleto(id: number): Promise<Array<{ imagemBase64: string, formatoImagem: string, numeroPagina: number }>> {
    const response = await fetch(`${this.apiUrl}/download-imagem-boleto?id=${id}`);
    if (!response.ok) {
      throw new Error('Imagens não encontradas ou erro no download.');
    }
    return response.json();
  }

  async uploadBoletoMultiplo(file: File): Promise<any> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      reader.onload = async () => {
        try {
          const base64String = reader.result as string;
          const arquivoBase64 = base64String.split(',')[1]; // Remove o prefixo data:application/pdf;base64,
          
          const response = await this.enviarBoleto(file.name, arquivoBase64);
          resolve({
            success: true,
            message: 'Boleto enviado com sucesso!',
            data: response
          });
        } catch (error: any) {
          reject({
            success: false,
            message: error.message || 'Erro ao enviar boleto'
          });
        }
      };
      reader.onerror = () => {
        reject({
          success: false,
          message: 'Erro ao ler o arquivo'
        });
      };
      reader.readAsDataURL(file);
    });
  }

  private getFileNameFromResponse(response: Response): string | null {
    const disposition = response.headers.get('Content-Disposition');
    if (disposition) {
      const match = disposition.match(/filename="?([^";]+)"?/);
      if (match) return match[1];
    }
    return null;
  }
}
