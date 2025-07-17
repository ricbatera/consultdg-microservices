package br.com.consultdg.email_to_ftp_service.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class DotenvConfig {

    @Autowired
    private Environment environment;

    @PostConstruct
    public void loadDotenv() {
        String[] activeProfiles = environment.getActiveProfiles();
        String profileInfo = activeProfiles.length > 0 ? 
            String.join(", ", activeProfiles) : "default (dev)";
        
        System.out.println("=================================================");
        System.out.println("🚀 EMAIL TO FTP SERVICE - INICIANDO");
        System.out.println("=================================================");
        System.out.println("📋 Profile(s) ativo(s): " + profileInfo);
        
        // Em produção, prioriza variáveis de ambiente do sistema
        boolean isProduction = java.util.Arrays.stream(activeProfiles)
                .anyMatch(profile -> profile.equals("prod") || profile.equals("production"));
        
        if (isProduction) {
            System.out.println("🏭 Ambiente: PRODUÇÃO");
            System.out.println("🔒 Usando APENAS variáveis de ambiente do sistema");
            System.out.println("❌ Arquivo .env será IGNORADO");
            System.out.println("=================================================");
            return;
        }
        
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")
                    .filename(".env")
                    .ignoreIfMissing()
                    .load();

            int loadedVars = 0;
            // Carrega todas as variáveis do .env como propriedades do sistema
            for (var entry : dotenv.entries()) {
                String key = entry.getKey();
                String value = entry.getValue();
                
                // Só define se não já estiver definida como variável de ambiente do sistema
                if (System.getProperty(key) == null && System.getenv(key) == null) {
                    System.setProperty(key, value);
                    loadedVars++;
                }
            }
            
            System.out.println("💻 Ambiente: DESENVOLVIMENTO/HOMOLOGAÇÃO");
            System.out.println("✅ Arquivo .env carregado com sucesso!");
            System.out.println("📊 Variáveis carregadas do .env: " + loadedVars);
            System.out.println("ℹ️  Variáveis de ambiente do sistema têm precedência");
            
        } catch (Exception e) {
            System.out.println("💻 Ambiente: DESENVOLVIMENTO/HOMOLOGAÇÃO");
            System.out.println("⚠️  Arquivo .env não encontrado ou erro ao carregar: " + e.getMessage());
            System.out.println("ℹ️  Usando apenas variáveis de ambiente disponíveis");
        }
        
        System.out.println("=================================================");
    }
}
