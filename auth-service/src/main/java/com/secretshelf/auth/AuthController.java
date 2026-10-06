package com.secretshelf.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuthController {
    private final UserRepository users; private final PasswordEncoder encoder; private final SecretKey key;
    public AuthController(UserRepository users,PasswordEncoder encoder,@Value("${security.jwt.secret}") String secret){this.users=users;this.encoder=encoder;this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));}
    public record RegisterRequest(@NotBlank @Size(max=80) String name,@Email @NotBlank String email,@NotBlank @Size(min=8,max=72) String password){}
    public record LoginRequest(@Email @NotBlank String email,@NotBlank String password){}
    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
    public Map<String,Object> register(@Valid @RequestBody RegisterRequest request){
        if(users.existsByEmailIgnoreCase(request.email())) throw new ResponseStatusException(HttpStatus.CONFLICT,"Ese correo ya está registrado");
        var user=users.save(new UserAccount(request.name(),request.email(),encoder.encode(request.password()))); return token(user);
    }
    @PostMapping("/login")
    public Map<String,Object> login(@Valid @RequestBody LoginRequest request){
        var user=users.findByEmailIgnoreCase(request.email()).orElseThrow(()->new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Correo o contraseña incorrectos"));
        if(!encoder.matches(request.password(),user.getPasswordHash())) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Correo o contraseña incorrectos");
        return token(user);
    }
    private Map<String,Object> token(UserAccount user){
        var now=Instant.now(); var expires=now.plusSeconds(86400);
        var jwt=Jwts.builder().subject(user.getId()).claim("email",user.getEmail()).claim("name",user.getName()).issuedAt(Date.from(now)).expiration(Date.from(expires)).signWith(key).compact();
        return Map.of("token",jwt,"tokenType","Bearer","expiresAt",expires.toString(),"user",Map.of("id",user.getId(),"name",user.getName(),"email",user.getEmail()));
    }
}
