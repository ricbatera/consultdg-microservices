import { Component, OnInit } from '@angular/core';
import { NgIf, NgFor } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { BoletoApiService } from '../boleto-api.service';

export interface Protocolo {
  id: number;
  dataHoraCriacao: string;
  nomeArquivo: string;
  statusProtocolo: string;
  mensagemErro: string | null;
}

export interface BoletoItem {
  descricao: string;
  valor: number;
}

export interface BoletoDetails {
  protocoloId: number;
  numeroDocumento: string;
  codigoBarras: string;
  dataVencimento: string;
  valor: number;
  itens: BoletoItem[];
}

@Component({
  selector: 'app-protocolos',
  standalone: true,
  imports: [NgIf, NgFor, FormsModule, RouterModule],
  templateUrl: './protocolos.component.html',
  styleUrl: './protocolos.component.css'
})
export class ProtocolosComponent implements OnInit {
  protocolos: Protocolo[] = [];
  loading = false;
  error: string | null = null;
  
  dataInicial: string = '';
  dataFinal: string = '';
  
  selectedProtocolo: Protocolo | null = null;
  boletoDetails: BoletoDetails | null = null;
  loadingDetails = false;
  detailsError: string | null = null;

  // Propriedades para o modal de comparação
  showCompareModal = false;
  boletoImageBase64: string | null = null;
  boletoImageFormat: string | null = null;
  loadingImage = false;
  imageError: string | null = null;

  // Propriedades para zoom e pan
  imageZoom: number = 1;
  imagePanX: number = 0;
  imagePanY: number = 0;
  isDragging: boolean = false;
  lastMouseX: number = 0;
  lastMouseY: number = 0;
  Math = Math; // Disponibiliza Math para o template

  // Propriedade para mostrar mensagem de sucesso
  reloadSuccess: boolean = false;

  // Propriedade para filtro de status
  statusFilter: string | null = null;

  constructor(private boletoApi: BoletoApiService) {}

  ngOnInit(): void {
    this.initializeDates();
    this.buscarProtocolos();
  }

  private initializeDates(): void {
    const today = new Date();
    
    // Primeiro dia do mês atual
    const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
    
    // Último dia do terceiro mês a partir da data atual
    const thirdMonth = new Date(today.getFullYear(), today.getMonth() + 3, 0);
    
    this.dataInicial = this.formatDateForInput(firstDayOfMonth);
    this.dataFinal = this.formatDateForInput(thirdMonth);
  }

  private formatDateForInput(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  async buscarProtocolos(): Promise<void> {
    if (!this.dataInicial || !this.dataFinal) {
      this.error = 'Por favor, selecione as datas inicial e final';
      return;
    }

    this.loading = true;
    this.error = null;

    try {
      this.protocolos = await this.boletoApi.getAllProtocolos(this.dataInicial, this.dataFinal);
    } catch (error: any) {
      this.error = error.message || 'Erro ao buscar protocolos';
      this.protocolos = [];
    } finally {
      this.loading = false;
    }
  }

  onProtocoloClick(protocolo: Protocolo): void {
    this.selectedProtocolo = protocolo;
    this.buscarDetalhes(protocolo.id);
  }

  async buscarDetalhes(protocoloId: number): Promise<void> {
    this.loadingDetails = true;
    this.detailsError = null;
    this.boletoDetails = null;

    try {
      this.boletoDetails = await this.boletoApi.getBoletoDetails(protocoloId);
    } catch (error: any) {
      this.detailsError = error.message || 'Erro ao buscar detalhes do boleto';
    } finally {
      this.loadingDetails = false;
    }
  }

  formatDateTime(dateTimeString: string): string {
    const date = new Date(dateTimeString);
    return date.toLocaleString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  }

  getStatusColor(status: string): string {
    switch (status.toUpperCase()) {
      case 'FINALIZADO': return '#28a745';
      case 'PROCESSANDO': return '#ffc107';
      case 'ERRO': return '#dc3545';
      case 'PENDENTE': return '#17a2b8';
      default: return '#6c757d';
    }
  }

  getStatusText(status: string): string {
    switch (status.toUpperCase()) {
      case 'FINALIZADO': return 'Finalizado';
      case 'PROCESSANDO': return 'Processando';
      case 'ERRO': return 'Erro';
      case 'PENDENTE': return 'Pendente';
      default: return status;
    }
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  }

  formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('pt-BR');
  }

  formatBarcode(barcode: string): string {
    // Formata o código de barras no padrão: 23790.27200 90000.008145 82063.480006 7 10310000007875
    if (barcode.length >= 44) {
      const campo1 = barcode.substring(0, 5) + '.' + barcode.substring(5, 10);
      const campo2 = barcode.substring(10, 15) + '.' + barcode.substring(15, 21);
      const campo3 = barcode.substring(21, 26) + '.' + barcode.substring(26, 32);
      const campo4 = barcode.substring(32, 33);
      const campo5 = barcode.substring(33, 47);
      
      return `${campo1} ${campo2} ${campo3} ${campo4} ${campo5}`;
    }
    // Fallback para códigos menores
    return barcode.replace(/(.{5})/g, '$1 ').trim();
  }

  async baixarPdf(protocoloId: number, nomeArquivo: string): Promise<void> {
    try {
      await this.boletoApi.downloadPdf(protocoloId);
    } catch (error) {
      console.error('Erro ao baixar PDF:', error);
      alert('Erro ao baixar o arquivo PDF');
    }
  }

  onBaixarPdf(event: Event, protocoloId: number, nomeArquivo: string): void {
    event.stopPropagation(); // Evita que o clique propague para o card
    this.baixarPdf(protocoloId, nomeArquivo);
  }

  async onComparar(event: Event, protocoloId: number): Promise<void> {
    event.stopPropagation(); // Evita que o clique propague para o card
    
    this.showCompareModal = true;
    this.loadingImage = true;
    this.imageError = null;
    this.boletoImageBase64 = null;
    this.boletoImageFormat = null;

    try {
      const images = await this.boletoApi.downloadImagensBoleto(protocoloId);
      if (images && images.length > 0) {
        // Usa a primeira imagem
        const firstImage = images[0];
        this.boletoImageBase64 = firstImage.imagemBase64;
        this.boletoImageFormat = firstImage.formatoImagem;
      } else {
        this.imageError = 'Nenhuma imagem encontrada para este boleto';
      }
    } catch (error: any) {
      this.imageError = error.message || 'Erro ao carregar imagem do boleto';
    } finally {
      this.loadingImage = false;
    }
  }

  closeCompareModal(): void {
    this.showCompareModal = false;
    this.boletoImageBase64 = null;
    this.boletoImageFormat = null;
    this.imageError = null;
    this.imageZoom = 1; // Reset zoom ao fechar modal
    this.imagePanX = 0;
    this.imagePanY = 0;
    this.isDragging = false;
  }

  onImageWheel(event: WheelEvent): void {
    event.preventDefault();
    
    const zoomStep = 0.1;
    const minZoom = 0.5;
    const maxZoom = 3.0;
    
    if (event.deltaY < 0) {
      // Scroll para cima - zoom in
      this.imageZoom = Math.min(this.imageZoom + zoomStep, maxZoom);
    } else {
      // Scroll para baixo - zoom out
      this.imageZoom = Math.max(this.imageZoom - zoomStep, minZoom);
    }
  }

  resetZoom(): void {
    this.imageZoom = 1;
    this.imagePanX = 0;
    this.imagePanY = 0;
    this.isDragging = false;
  }

  // Métodos para pan da imagem
  onMouseDown(event: MouseEvent): void {
    if (this.imageZoom > 1) {
      this.isDragging = true;
      this.lastMouseX = event.clientX;
      this.lastMouseY = event.clientY;
      event.preventDefault();
    }
  }

  onMouseMove(event: MouseEvent): void {
    if (this.isDragging && this.imageZoom > 1) {
      const deltaX = event.clientX - this.lastMouseX;
      const deltaY = event.clientY - this.lastMouseY;
      
      this.imagePanX += deltaX / this.imageZoom;
      this.imagePanY += deltaY / this.imageZoom;
      
      this.lastMouseX = event.clientX;
      this.lastMouseY = event.clientY;
      
      event.preventDefault();
    }
  }

  onMouseUp(event: MouseEvent): void {
    this.isDragging = false;
  }

  onMouseLeave(): void {
    this.isDragging = false;
  }

  // Método para recarregar protocolos
  async recarregarProtocolos(): Promise<void> {
    // Limpar protocolo selecionado
    this.selectedProtocolo = null;
    this.boletoDetails = null;
    this.detailsError = null;
    this.reloadSuccess = false;
    this.statusFilter = null; // Limpar filtro de status
    
    // Recarregar a lista
    await this.buscarProtocolos();
    
    // Mostrar feedback de sucesso
    if (!this.error) {
      this.reloadSuccess = true;
      // Esconder mensagem após 3 segundos
      setTimeout(() => {
        this.reloadSuccess = false;
      }, 3000);
    }
  }

  // Método para calcular estatísticas por status
  getStatusStats() {
    const stats = {
      total: this.protocolos.length,
      processando: 0,
      concluido: 0,
      erro: 0,
      pendente: 0
    };

    this.protocolos.forEach(protocolo => {
      const status = protocolo.statusProtocolo?.toLowerCase() || 'pendente';
      
      if (status.includes('proces') || status.includes('andamento') || status.includes('executando')) {
        stats.processando++;
      } else if (status.includes('concluido') || status.includes('sucesso') || status.includes('finalizado') || 
                 status.includes('completed') || status.includes('done') || status.includes('ok')) {
        stats.concluido++;
      } else if (status.includes('erro') || status.includes('falha') || status.includes('failed') || 
                 status.includes('error') || protocolo.mensagemErro) {
        stats.erro++;
      } else {
        stats.pendente++;
      }
    });

    return stats;
  }

  // Método para filtrar por status
  filterByStatus(status: string): void {
    this.statusFilter = this.statusFilter === status ? null : status;
  }

  // Método para obter protocolos filtrados
  getFilteredProtocolos(): Protocolo[] {
    if (!this.statusFilter) {
      return this.protocolos;
    }

    return this.protocolos.filter(protocolo => {
      const status = protocolo.statusProtocolo?.toLowerCase() || 'pendente';
      
      switch (this.statusFilter) {
        case 'processando':
          return status.includes('proces') || status.includes('andamento') || status.includes('executando');
        case 'concluido':
          return status.includes('concluido') || status.includes('sucesso') || status.includes('finalizado') || 
                 status.includes('completed') || status.includes('done') || status.includes('ok');
        case 'erro':
          return status.includes('erro') || status.includes('falha') || status.includes('failed') || 
                 status.includes('error') || protocolo.mensagemErro;
        case 'pendente':
          return !status.includes('proces') && !status.includes('concluido') && !status.includes('sucesso') && 
                 !status.includes('erro') && !status.includes('falha') && !protocolo.mensagemErro;
        default:
          return true;
      }
    });
  }
}
