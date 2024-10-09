package com.inventory.server.configuration.tokenConfiguration;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.inventory.server.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    private final JwtEncoder jwtEncoder;

    private final JwtDecoder jwtDecoder;

    public TokenService(@Lazy JwtEncoder jwtEncoder, @Lazy JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    private Instant expirationDate() {
        //ZoneOffSet of Brazil
        return LocalDateTime.now().plusMinutes(30).toInstant(ZoneOffset.of("-03:00"));
    }

    public String createToken(Authentication authentication) {
        Instant now = Instant.now();
        Instant expiration = this.expirationDate();
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        String id = String.valueOf(((User) authentication.getPrincipal()).getId());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .subject(authentication.getName())
                .claim("id", id)
                .issuedAt(now)
                .expiresAt(expiration)
                .claim("authorities", roles)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String getSubject(String token) {
        try {
            return jwtDecoder.decode(token).getSubject();
        } catch (JWTVerificationException exception){
            throw new RuntimeException("Invalid JWT token", exception);
        }
    }
}
