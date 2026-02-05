package sn.gtech.sgle.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8081}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("SGLE - Système de Gestion des Logements Étudiants")
                        .description("""
                                API REST pour la gestion des logements étudiants.
                                
                                ## Fonctionnalités principales :
                                - **Authentification** : Inscription, connexion, gestion des tokens JWT
                                - **Gestion des logements** : CRUD complet des logements
                                - **Demandes de logement** : Soumission et traitement des demandes
                                - **Attributions** : Gestion des contrats de location
                                - **Paiements** : Suivi des paiements de loyer
                                - **Incidents** : Signalement et suivi des problèmes
                                - **Notifications** : Système de notifications multi-canal
                                
                                ## Rôles utilisateurs :
                                - **ADMIN** : Accès complet au système
                                - **GESTIONNAIRE** : Gestion des logements et demandes
                                - **ETUDIANT** : Consultation et soumission de demandes
                                - **INVITE** : Consultation publique limitée
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("GTech Sénégal")
                                .email("contact@gtech.sn")
                                .url("https://gtech.sn"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Serveur de développement"),
                        new Server()
                                .url("https://api.sgle.sn")
                                .description("Serveur de production")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Entrez votre token JWT. Exemple: eyJhbGciOiJIUzI1NiIs...")));
    }
}
