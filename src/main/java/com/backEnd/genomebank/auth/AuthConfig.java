package com.backEnd.genomebank.auth;

import com.backEnd.genomebank.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * Configuración de autenticación y seguridad para la aplicación.
 * Define beans para la codificación de contraseñas y la carga de detalles de usuario.
 * Utiliza BCrypt para la seguridad de contraseñas y conecta el repositorio de usuarios para la autenticación.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthConfig {
    /**
     * Repositorio para la gestión de usuarios en la autenticación.
     */
    private final UserRepository usuerRepository;

    /**
     * Bean para la codificación de contraseñas usando BCrypt.
     * @return PasswordEncoder seguro para almacenar contraseñas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean para cargar los detalles de usuario desde la base de datos.
     * Utilizado por Spring Security para autenticar usuarios.
     * @return UserDetailsService que busca usuarios por nombre de usuario
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> usuerRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
}
