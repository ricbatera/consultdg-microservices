import { Component, ViewChild, ElementRef } from '@angular/core';
import { NgIf } from '@angular/common';
import { BoletoApiService } from '../boleto-api.service';

@Component({
  selector: 'app-upload-pdf',
  standalone: true,
  imports: [NgIf],
  templateUrl: './upload-pdf.component.html',
  styleUrl: './upload-pdf.component.css'
})
export class UploadPdfComponent {
  base64Pdf: string | null = null;
  nomeArquivo: string | null = null;
  @ViewChild('pdfCanvas', { static: false }) pdfCanvas!: ElementRef<HTMLCanvasElement>;
  enviando = false;
  mensagem: string | null = null;
  helloMsg: string | null = null;
  urlS3: string | null = null;
  buscandoUrl = false;

  constructor(private boletoApi: BoletoApiService) {}

  async onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;
    const file = input.files[0];
    if (file.type !== 'application/pdf') return;
    this.nomeArquivo = file.name;
    const reader = new FileReader();
    reader.onload = async () => {
      const result = reader.result as string;
      this.base64Pdf = result.split(',')[1];
      await this.renderThumbnail();
    };
    reader.readAsDataURL(file);
  }

  async enviarPdf() {
    if (!this.base64Pdf || !this.nomeArquivo) return;
    this.enviando = true;
    this.mensagem = null;
    try {
      await this.boletoApi.enviarBoleto(this.nomeArquivo, this.base64Pdf);
      this.mensagem = 'PDF enviado com sucesso!';
    } catch (e: any) {
      this.mensagem = 'Erro ao enviar PDF: ' + (e.message || e);
    } finally {
      this.enviando = false;
    }
  }

  async renderThumbnail() {
    if (!this.base64Pdf) return;
    const pdfjsLib = await import('pdfjs-dist');
    // Força o uso do fake worker para compatibilidade com Vite/Angular
    (pdfjsLib as any).GlobalWorkerOptions.workerSrc = undefined;
    (pdfjsLib as any).GlobalWorkerOptions.workerPort = null;
    (pdfjsLib as any).GlobalWorkerOptions.disableWorker = true;
    const loadingTask = (pdfjsLib as any).getDocument({ data: this.base64ToUint8Array(this.base64Pdf) });
    const pdf = await loadingTask.promise;
    const page = await pdf.getPage(1);
    const viewport = page.getViewport({ scale: 0.3 });
    const canvas = this.pdfCanvas.nativeElement;
    const context = canvas.getContext('2d');
    canvas.width = viewport.width;
    canvas.height = viewport.height;
    await page.render({ canvasContext: context, viewport }).promise;
  }

  base64ToUint8Array(base64: string): Uint8Array {
    const raw = atob(base64);
    const uint8Array = new Uint8Array(raw.length);
    for (let i = 0; i < raw.length; i++) {
      uint8Array[i] = raw.charCodeAt(i);
    }
    return uint8Array;
  }

  async chamarHello() {
    this.helloMsg = null;
    try {
      this.helloMsg = await this.boletoApi.hello();
    } catch (e: any) {
      this.helloMsg = 'Erro: ' + (e.message || e);
    }
  }

  async buscarUrlS3() {
    if (!this.nomeArquivo) return;
    this.buscandoUrl = true;
    this.urlS3 = null;
    try {
      this.urlS3 = await this.boletoApi.getUrlS3(this.nomeArquivo);
    } catch (e: any) {
      this.urlS3 = 'Erro: ' + (e.message || e);
    } finally {
      this.buscandoUrl = false;
    }
  }
}
