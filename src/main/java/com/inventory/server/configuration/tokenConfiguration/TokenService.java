package com.inventory.server.configuration.tokenConfiguration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(String username, List<String> roles, Instant now, Instant validity) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("API Inventory")
                    .withSubject(username)
                    //.withClaim("roles", roles)
                    .withIssuedAt(now)
                    .withExpiresAt(validity)
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new RuntimeException("Error on generating JWT access token", exception);
        }
    }

    public String getSubject(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("API Inventory")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception){
            throw new RuntimeException("Invalid JWT token", exception);
        }
    }

    private Instant expirationDate() {
        //ZoneOffSet of Brasil
        return LocalDateTime.now().plusMinutes(30).toInstant(ZoneOffset.of("-03:00"));
    }

    public TokensData createAccessToken(String username, List<String> permissions) {
        Instant now = Instant.now();
        Instant validity = expirationDate();
        String accessToken = generateToken(username, permissions, now, validity);
        String refreshToken = generateRefreshToken(username, permissions, now);

        return new TokensData(accessToken, refreshToken);
    }

    private String generateRefreshToken(String username, List<String> roles, Instant now) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                    .withIssuer("API Inventory")
                    .withSubject(username)
                    //.withClaim("roles", roles)
                    .withIssuedAt(now)
                    .withExpiresAt(expirationDate().plusSeconds(10800)) // Plus 3 hours
                    .sign(algorithm);
        } catch (JWTCreationException exception){
            throw new RuntimeException("Error on generating JWT refresh token", exception);
        }
    }

    private DecodedJWT decodedToken(String token) {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secret)).build();
        return jwtVerifier.verify(token);
    }
}
