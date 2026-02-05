package sn.gtech.sgle.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Endpoints publics
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                
                // Swagger / OpenAPI endpoints
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/v3/api-docs.yaml").permitAll()
                .requestMatchers("/swagger-resources/**").permitAll()
                .requestMatchers("/webjars/**").permitAll()
                
                // Endpoints pour les logements (lecture publique)
                .requestMatchers(HttpMethod.GET, "/api/logements/**").permitAll()
                
                // Endpoints réservés aux administrateurs
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/rapports/**").hasRole("ADMIN")
                .requestMatchers("/api/utilisateurs/**").hasRole("ADMIN")
                
                // Endpoints réservés aux gestionnaires
                .requestMatchers("/api/gestionnaire/**").hasAnyRole("ADMIN", "GESTIONNAIRE")
                .requestMatchers(HttpMethod.POST, "/api/logements/**").hasAnyRole("ADMIN", "GESTIONNAIRE")
                .requestMatchers(HttpMethod.PUT, "/api/logements/**").hasAnyRole("ADMIN", "GESTIONNAIRE")
                .requestMatchers(HttpMethod.DELETE, "/api/logements/**").hasAnyRole("ADMIN", "GESTIONNAIRE")
                .requestMatchers("/api/demandes/traiter/**").hasAnyRole("ADMIN", "GESTIONNAIRE")
                .requestMatchers("/api/attributions/**").hasAnyRole("ADMIN", "GESTIONNAIRE")
                .requestMatchers("/api/maintenances/**").hasAnyRole("ADMIN", "GESTIONNAIRE")
                
                // Endpoints réservés aux étudiants
                .requestMatchers("/api/etudiant/**").hasAnyRole("ADMIN", "GESTIONNAIRE", "ETUDIANT")
                .requestMatchers("/api/demandes/soumettre").hasRole("ETUDIANT")
                .requestMatchers("/api/incidents/signaler").hasRole("ETUDIANT")
                
                // Endpoints pour les paiements
                .requestMatchers("/api/paiements/**").hasAnyRole("ADMIN", "GESTIONNAIRE", "ETUDIANT")
                
                // Endpoints pour les incidents
                .requestMatchers("/api/incidents/**").hasAnyRole("ADMIN", "GESTIONNAIRE", "ETUDIANT")
                
                // Endpoints pour les notifications
                .requestMatchers("/api/notifications/**").authenticated()
                
                // Tout le reste nécessite une authentification
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
