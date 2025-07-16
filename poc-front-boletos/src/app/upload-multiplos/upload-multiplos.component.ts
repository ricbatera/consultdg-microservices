import { Component } from '@angular/core';
import { NgIf, NgFor } from '@angular/common';
import { RouterModule } from '@angular/router';
import { BoletoApiService } from '../boleto-api.service';

export interface BoletoUpload {
  id: string;
  file: File;
  status: 'aguardando' | 'enviando' | 'sucesso' | 'erro';
  message?: string;
  progress?: number;
}

@Component({
  selector: 'app-upload-multiplos',
  standalone: true,
  imports: [NgIf, NgFor, RouterModule],
  templateUrl: './upload-multiplos.component.html',
  styleUrl: './upload-multiplos.component.css'
})
export class UploadMultiplosComponent {
  boletos: BoletoUpload[] = [];
  uploading = false;

  constructor(private boletoApi: BoletoApiService) {}

  onFileSelected(event: any): void {
    const files: FileList = event.target.files;
    if (!files || files.length === 0) return;

    // Adicionar novos boletos à lista
    for (let i = 0; i < files.length; i++) {
      const file = files[i];
      if (file.type === 'application/pdf') {
        const boleto: BoletoUpload = {
          id: this.generateId(),
          file: file,
          status: 'aguardando',
          progress: 0
        };
        this.boletos.push(boleto);
      }
    }
    
    // Limpar o input
    event.target.value = '';
  }

  private generateId(): string {
    return Date.now().toString() + Math.random().toString(36).substr(2, 9);
  }

  removeBoleto(id: string): void {
    this.boletos = this.boletos.filter(b => b.id !== id);
  }

  async enviarTodos(): Promise<void> {
    if (this.boletos.length === 0) return;

    this.uploading = true;
    
    for (const boleto of this.boletos) {
      if (boleto.status === 'aguardando') {
        await this.enviarBoleto(boleto);
      }
    }

    this.uploading = false;
  }

  private async enviarBoleto(boleto: BoletoUpload): Promise<void> {
    boleto.status = 'enviando';
    boleto.progress = 0;

    try {
      // Simular progresso
      const progressInterval = setInterval(() => {
        if (boleto.progress! < 90) {
          boleto.progress! += 10;
        }
      }, 200);

      const response = await this.boletoApi.uploadBoletoMultiplo(boleto.file);
      
      clearInterval(progressInterval);
      boleto.progress = 100;
      boleto.status = 'sucesso';
      boleto.message = response.message || 'Upload realizado com sucesso!';
      
    } catch (error: any) {
      boleto.status = 'erro';
      boleto.message = error.message || 'Erro ao enviar boleto';
      boleto.progress = 0;
    }
  }

  limparLista(): void {
    this.boletos = [];
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'aguardando': return '#ffc107';
      case 'enviando': return '#17a2b8';
      case 'sucesso': return '#28a745';
      case 'erro': return '#dc3545';
      default: return '#6c757d';
    }
  }

  getStatusText(status: string): string {
    switch (status) {
      case 'aguardando': return 'Aguardando envio';
      case 'enviando': return 'Enviando...';
      case 'sucesso': return 'Sucesso';
      case 'erro': return 'Erro';
      default: return 'Desconhecido';
    }
  }

  getBoletosCount(status: string): number {
    return this.boletos.filter(b => b.status === status).length;
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }
}
