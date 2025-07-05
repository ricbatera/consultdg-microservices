import { Component } from '@angular/core';
import { NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterOutlet } from '@angular/router';
import { UploadPdfComponent } from './upload-pdf/upload-pdf.component';
import { BoletoApiService } from './boleto-api.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [NgIf, FormsModule, RouterOutlet, UploadPdfComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'poc-front-boletos';
  helloMsg: string | null = null;
  downloadId: string = '';
  downloadError: string | null = null;

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
}
