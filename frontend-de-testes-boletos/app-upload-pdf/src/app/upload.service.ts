import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { map, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UploadService {
  private backendUrl = 'http://localhost:8090';

  constructor(private http: HttpClient) {}

  getPresignedUrl(fileName: string): Observable<string> {
    return this.http.get<{ url: string }>(
      `${this.backendUrl}/s3/generate-presigned-url?fileName=${encodeURIComponent(fileName)}`
    ).pipe(
      map(response => response.url)
    );
  }

  uploadToS3(presignedUrl: string, file: File): Observable<any> {
    console.log('Uploading file to S3:', file.name);
    return this.http.put(presignedUrl, file);
  }
}
