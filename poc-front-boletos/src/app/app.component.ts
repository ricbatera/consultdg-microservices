// ...existing code...
import { Component } from '@angular/core';
import { NgIf, NgFor } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterOutlet } from '@angular/router';
import { UploadPdfComponent } from './upload-pdf/upload-pdf.component';
import { BoletoApiService } from './boleto-api.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [NgIf, NgFor, FormsModule, RouterOutlet, UploadPdfComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})

export class AppComponent {
  title = 'poc-front-boletos';
  helloMsg: string | null = null;
  downloadId: string = '';
  downloadError: string | null = null;

  downloadImagensError: string | null = null;
  imagensBoleto: Array<{ imagemBase64: string, formatoImagem: string, numeroPagina: number }> = [];

  imagemModalAberto: boolean = false;
  imagemModalSrc: string | null = null;
  abrirImagemModal(img: { imagemBase64: string, formatoImagem: string }): void {
    this.imagemModalSrc = `data:image/${img.formatoImagem};base64,${img.imagemBase64}`;
    this.imagemModalAberto = true;
  }

  fecharImagemModal(): void {
    this.imagemModalAberto = false;
    this.imagemModalSrc = null;
  }

  constructor(private boletoApi: BoletoApiService) {}

  async chamarHello() {
    this.helloMsg = null;
    try {
      this.helloMsg = await this.boletoApi.hello();
    } catch (e: any) {
      this.helloMsg = 'Erro: ' + (e.message || e);
    }
  }

  async baixarPdf() {
    this.downloadError = null;
    const id = Number(this.downloadId);
    if (!id) {
      this.downloadError = 'Digite um ID válido.';
      return;
    }
    try {
      await this.boletoApi.downloadPdf(id);
    } catch (e: any) {
      this.downloadError = e.message || 'Erro ao baixar PDF.';
    }
  }

  async baixarImagensBoleto() {
    this.downloadImagensError = null;
    this.imagensBoleto = [];
    const id = Number(this.downloadId);
    if (!id) {
      this.downloadImagensError = 'Digite um ID válido.';
      return;
    }
    try {
      this.imagensBoleto = await this.boletoApi.downloadImagensBoleto(id);
      if (!this.imagensBoleto || !this.imagensBoleto.length) {
        this.downloadImagensError = 'Nenhuma imagem encontrada para este boleto.';
      }
    } catch (e: any) {
      this.downloadImagensError = e.message || 'Erro ao baixar imagens.';
    }
  }
}
