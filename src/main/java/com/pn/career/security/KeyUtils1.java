package com.pn.career.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Objects;
import java.util.UUID;

public class KeyUtils1 {
    private static final Logger logger = LoggerFactory.getLogger(KeyUtils1.class);

    private final Environment environment;

    @Value("${jwt.key-size:2048}")
    private int keySize;
    private KeyPair accessTokenKeyPair;
    private KeyPair refreshTokenKeyPair;

    public KeyUtils1(Environment environment) {
        this.environment = environment;
    }
    @Bean
    public KeyPair accessTokenKeyPair() {
        if(Objects.isNull(accessTokenKeyPair))
            accessTokenKeyPair= generateKeyPair();
        return accessTokenKeyPair;
    }

    @Bean
    public KeyPair refreshTokenKeyPair() {
        if(Objects.isNull(refreshTokenKeyPair))
            refreshTokenKeyPair= generateKeyPair();
        return refreshTokenKeyPair;
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(keySize);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException ex) {
            logger.error("Failed to generate RSA key pair", ex);
            throw new RuntimeException("Could not generate RSA key pair", ex);
        }
    }

    @Bean
    public RSAKey accessTokenRsaKey(KeyPair accessTokenKeyPair) {
        return generateRsaKey(accessTokenKeyPair);
    }

    @Bean
    public RSAKey refreshTokenRsaKey(KeyPair refreshTokenKeyPair) {
        return generateRsaKey(refreshTokenKeyPair);
    }

    private RSAKey generateRsaKey(KeyPair keyPair) {
        return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey((RSAPrivateKey) keyPair.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    @Bean
    public JwtDecoder accessTokenDecoder(RSAKey accessTokenRsaKey) {
        return createJwtDecoder(accessTokenRsaKey);
    }

    @Bean
    public JwtDecoder refreshTokenDecoder(RSAKey refreshTokenRsaKey) {
        return createJwtDecoder(refreshTokenRsaKey);
    }

    private JwtDecoder createJwtDecoder(RSAKey rsaKey) {
        try {
            return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
        } catch (JOSEException ex) {
            logger.error("Failed to create JWT decoder", ex);
            throw new RuntimeException("Could not create JWT decoder", ex);
        }
    }

    public RSAPublicKey getAccessTokenPublicKey() {
        return (RSAPublicKey) accessTokenKeyPair().getPublic();
    }

    public RSAPrivateKey getAccessTokenPrivateKey() {
        return (RSAPrivateKey) accessTokenKeyPair().getPrivate();
    }

    public RSAPublicKey getRefreshTokenPublicKey() {
        return (RSAPublicKey) refreshTokenKeyPair().getPublic();
    }

    public RSAPrivateKey getRefreshTokenPrivateKey() {
        return (RSAPrivateKey) refreshTokenKeyPair().getPrivate();
    }


    public boolean isProduction() {
        return environment.matchesProfiles("prod");
    }
}
