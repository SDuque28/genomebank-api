package com.backEnd.genomebank.auth;

// Importa las clases necesarias para trabajar con JWT

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Servicio para la gestión de tokens JWT.
 * Permite generar y validar tokens para autenticación y autorización.
 * Utiliza una clave secreta y un tiempo de expiración configurable.
 */
@Service // Indica que esta clase es un servicio gestionado por Spring
public class JwtService {
    /**
     * Clave secreta utilizada para firmar y verificar los tokens JWT.
     */
    private final SecretKey key;
    /**
     * Tiempo de expiración de los tokens en minutos.
     */
    private final long expMinutes;

    /**
     * Constructor que inicializa la clave secreta y el tiempo de expiración.
     * @param secret clave secreta en base64 o texto plano
     * @param expMinutes minutos de expiración del token
     */
    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.exp-min}") long expMinutes) {
        // Decodifica la clave secreta desde base64 si corresponde, o la usa como texto plano
        byte[] raw = secret.matches("^[A-Za-z0-9+/=]+$") ? Decoders.BASE64.decode(secret) : secret.getBytes();
        // Genera la clave secreta para firmar/verificar JWT
        this.key = Keys.hmacShaKeyFor(raw);
        // Guarda el tiempo de expiración configurado
        this.expMinutes = expMinutes;
    }

    /**
     * Genera un token JWT con el sujeto y los roles proporcionados.
     * @param subject identificador del usuario (username)
     * @param roles lista de roles del usuario
     * @return token JWT firmado
     */
    public String generate(String subject, List<String> roles) {
        Instant now = Instant.now(); // Obtiene el instante actual
        List<String> safeRoles = roles == null ? List.of() : roles; // Asegura que la lista de roles no sea nula
        // Construye el token JWT
        return Jwts.builder()
                .subject(subject) // Establece el sujeto (username)
                .claims(Map.of("roles", safeRoles)) // Agrega los roles como claim personalizado
                .issuedAt(Date.from(now)) // Fecha de emisión
                .expiration(Date.from(now.plusSeconds(expMinutes * 60))) // Fecha de expiración
                // Firma el token con la clave y algoritmo seguro
                .signWith(key)
                .signWith(key, Jwts.SIG.HS256)
                .compact(); // Finaliza y retorna el token JWT
    }

    /**
     * Valida y parsea un token JWT, devolviendo sus claims.
     * @param token token JWT a validar
     * @return claims extraídos del token
     */
    public Claims parse(String token) {
        // Elimina el prefijo "Bearer " si está presente
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        // Parsea y valida el token JWT, devolviendo los claims
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
