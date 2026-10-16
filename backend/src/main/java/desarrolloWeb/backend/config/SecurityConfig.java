
package desarrolloWeb.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder) {

        UserDetails administrador = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("Admin123"))
                .roles("ADMIN")
                .build();

        UserDetails rrhh = User.builder()
                .username("rrhh1")
                .password(passwordEncoder.encode("Rrhh123"))
                .roles("RRHH")
                .build();

        UserDetails contabilidad = User.builder()
                .username("contabilidad1")
                .password(passwordEncoder.encode("Conta123"))
                .roles("CONTABILIDAD")
                .build();

        UserDetails administracion = User.builder()
                .username("administracion1")
                .password(passwordEncoder.encode("AdminArea123"))
                .roles("ADMINISTRACION")
                .build();

        return new InMemoryUserDetailsManager(
                administrador,
                rrhh,
                contabilidad,
                administracion
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

          
                .requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()

       
                .requestMatchers("/api/auth/me")
                    .authenticated()

             
                .requestMatchers(
                    "/api/personal", "/api/personal/**",
                    "/api/asistencia", "/api/asistencia/**",
                    "/api/documentos", "/api/documentos/**"
                ).hasAnyRole("ADMIN", "RRHH")

                
                .requestMatchers(
                    "/api/ventas", "/api/ventas/**"
                ).hasAnyRole(
                    "ADMIN", "CONTABILIDAD", "ADMINISTRACION"
                )

            
                .requestMatchers(
                    "/api/pagos", "/api/pagos/**"
                ).hasAnyRole("ADMIN", "CONTABILIDAD")

             
                .requestMatchers(
                    "/api/proveedores", "/api/proveedores/**"
                ).hasAnyRole("ADMIN", "ADMINISTRACION")

               
                .requestMatchers("/api/**")
                    .hasRole("ADMIN")

                
                .anyRequest().authenticated()
            )

           
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
