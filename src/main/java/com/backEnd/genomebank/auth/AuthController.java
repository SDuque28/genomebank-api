package com.backEnd.genomebank.auth;

import com.backEnd.genomebank.dto.RegisterRequest;
import com.backEnd.genomebank.entities.Rol;
import com.backEnd.genomebank.entities.User;
import com.backEnd.genomebank.repositories.*;
import com.backEnd.genomebank.repositories.RolRepository;
import com.backEnd.genomebank.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controlador para la autenticación y registro de usuarios.
 * Proporciona endpoints para login y registro, así como manejo de errores de autenticación.
 * Utiliza JWT para la generación de tokens y roles para la autorización.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    /**
     * AuthenticationManager de Spring Security para autenticar usuarios.
     */
    private final AuthenticationManager authManager;
    /**
     * Repositorio para la gestión de usuarios.
     */
    private final UserRepository usuarioRepo;
    /**
     * Repositorio para la gestión de roles.
     */
    private final RolRepository rolRepo;
    /**
     * Servicio para la gestión de JWT.
     */
    private final JwtService jwt;
    /**
     * PasswordEncoder para encriptar contraseñas.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Endpoint para el login de usuarios.
     * Autentica las credenciales y retorna un token JWT si son válidas.
     * @param req mapa con username y password
     * @return mapa con el token, tipo y roles
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String password = req.get("password");

        // Si las credenciales son incorrectas, lanza AuthenticationException → 401 por defecto
        authManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));

        var user = usuarioRepo.findByUsername(username).orElseThrow();
        var roles = user.getRoles().stream().map(Rol::getNombre).toList();
        String token = jwt.generate(user.getUsername(), roles);

        return Map.of(
                "access_token", token,
                "token_type", "Bearer",
                "roles", roles
        );
    }

    /**
     * Endpoint para el registro de nuevos usuarios.
     * Valida los datos, asigna roles y retorna un token JWT.
     * @param req datos de registro (username, password, roles)
     * @return mapa con el token, tipo y roles
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> register(@RequestBody RegisterRequest req) {
        if (req.getUsername() == null || req.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing username or password");
        }
        if (usuarioRepo.findByUsername(req.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        List<String> roleNames = (req.getRoles() == null || req.getRoles().isEmpty())
                ? List.of("USER")
                : req.getRoles();

        // Manejo correcto de roles para evitar ConcurrentModificationException
        Set<Rol> rolEntities = new HashSet<>();
        for (String roleName : roleNames) {
            Rol rol = rolRepo.findByNombre(roleName).orElseGet(() -> {
                Rol newRol = new Rol();
                newRol.setNombre(roleName);
                return rolRepo.save(newRol);
            });
            rolEntities.add(rol);
        }

        User user = new User();
        user.setName(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRoles(rolEntities);

        usuarioRepo.save(user);

        List<String> roles = rolEntities.stream().map(Rol::getNombre).toList();
        String token = jwt.generate(user.getUsername(), roles);

        return Map.of(
                "access_token", token,
                "token_type", "Bearer",
                "roles", roles
        );
    }

    /**
     * Maneja errores de autenticación devolviendo un mensaje estándar.
     * @param e excepción de autenticación
     * @return mapa con el error
     */
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public Map<String, String> onAuthError(Exception e) {
        return Map.of("error", "Bad credentials");
    }
}