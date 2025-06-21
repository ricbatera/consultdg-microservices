import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { UploadService } from './upload.service';

@Component({
  selector: 'app-root',
  imports: [CommonModule, HttpClientModule],
  template: `
    <div class="container">
      <h1>{{ title }}</h1>
      <div class="upload-box">
        <label for="pdf-upload" class="upload-label">
          Selecione um ou mais arquivos PDF
          <input id="pdf-upload" type="file" accept="application/pdf" multiple (change)="onFilesSelected($event)" />
        </label>
        <button (click)="uploadFiles()" [disabled]="selectedFiles.length === 0 || uploading">Enviar PDFs</button>
        <div *ngIf="selectedFiles.length > 0" class="file-list">
          <h3>Arquivos selecionados:</h3>
          <ul>
            <li *ngFor="let file of selectedFiles">{{ file.name }}</li>
          </ul>
        </div>
        <div *ngIf="uploading" class="status">Enviando arquivos...</div>
        <div *ngIf="uploadSuccess" class="status success">Todos os arquivos foram enviados com sucesso!</div>
        <div *ngIf="uploadError" class="status error">Erro ao enviar arquivos: {{ uploadError }}</div>
      </div>
    </div>
  `,
  styles: [`
    .container {
      max-width: 500px;
      margin: 40px auto;
      padding: 32px;
      background: #fff;
      border-radius: 12px;
      box-shadow: 0 2px 16px rgba(0,0,0,0.08);
      text-align: center;
    }
    .upload-box {
      margin-top: 24px;
    }
    .upload-label {
      display: inline-block;
      padding: 16px 32px;
      background: #1976d2;
      color: #fff;
      border-radius: 8px;
      cursor: pointer;
      font-size: 1.1rem;
      transition: background 0.2s;
    }
    .upload-label:hover {
      background: #1565c0;
    }
    input[type="file"] {
      display: none;
    }
    .file-list {
      margin-top: 20px;
      text-align: left;
    }
    .file-list ul {
      padding-left: 20px;
    }
    button {
      margin-top: 18px;
      padding: 10px 24px;
      font-size: 1rem;
      border: none;
      border-radius: 6px;
      background: #43a047;
      color: #fff;
      cursor: pointer;
      transition: background 0.2s;
    }
    button:disabled {
      background: #bdbdbd;
      cursor: not-allowed;
    }
    .status {
      margin-top: 18px;
      font-size: 1rem;
    }
    .success {
      color: #388e3c;
    }
    .error {
      color: #d32f2f;
    }
  `],
  standalone: true,
  providers: [UploadService],
})
export class AppComponent {
  title = 'Upload de PDFs';
  selectedFiles: File[] = [];
  uploading = false;
  uploadSuccess = false;
  uploadError: string | null = null;

  constructor(private uploadService: UploadService) {}

  onFilesSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files) {
      this.selectedFiles = Array.from(input.files);
    }
  }

  async uploadFiles() {
    this.uploading = true;
    this.uploadSuccess = false;
    this.uploadError = null;
    for (const file of this.selectedFiles) {
      try {
        const fileName = `boletos/recebidos/${file.name}`;
        // const presigned = await this.uploadService.getPresignedUrl(fileName).toPromise();
        // const presigned = "";
        this.uploadService.getPresignedUrl(fileName).subscribe(res=>{
          console.log('URL pré-assinada recebida:', res);
          const presigned = res;
          if (!presigned) throw new Error('URL pré-assinada não recebida');
          this.uploadService.uploadToS3(presigned, file).subscribe({
            next: () => {
              console.log(`Arquivo ${file.name} enviado com sucesso!`);
            },
            error: (err) => {
              throw new Error(`Erro ao enviar arquivo ${file.name}: ${err.message}`);
            }
          });
        }); 
        
      } catch (err: any) {
        this.uploading = false;
        this.uploadError = err?.message || 'Erro desconhecido';
        return;
      }
    }
    this.uploading = false;
    this.uploadSuccess = true;
    this.selectedFiles = [];
  }
}
