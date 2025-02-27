package com.pn.career.configurations;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.pn.career.components.AuthEntryPointJwt;
import com.pn.career.security.KeyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.http.HttpMethod.GET;

@Configuration
@EnableMethodSecurity   // Enable method level security
public class WebSecurityConfig {
    @Autowired
    private AuthEntryPointJwt authEntryPointJwt;
    @Autowired
    KeyUtils keyUtils;
    @Value("${api.prefix}")
    private String apiPrefix;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authEntryPointJwt))
                .authorizeHttpRequests(requests -> {
                    requests
                            .requestMatchers(
                                    String.format("%s/users/register", apiPrefix),
                                    String.format("%s/users/login", apiPrefix),
                                    String.format("%s/employers/register", apiPrefix),
                                    String.format("%s/employers/login", apiPrefix),
                                    String.format("%s/auth/**", apiPrefix),
                                    //Google login
                                    String.format("%s/users/auth/social-login", apiPrefix),
                                    String.format("%s/users/auth/social/callback", apiPrefix)
                            ).permitAll()
                            .requestMatchers("/ws/**").permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/industries/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/orders/vnpay-payment-return", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/skills/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/packages/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/jobs/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/admin/events/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/job-levels/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/benefits/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/coupons/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/job-categories/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/employers/get-all-employers", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/employers/get-company", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/employers/get-employers-by-industry", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/employers/top-company", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/messages/**", apiPrefix)).permitAll()
                            .anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer((oauth2) -> oauth2
                        .jwt(Customizer.withDefaults())
                        .authenticationEntryPoint(authEntryPointJwt)
                )
                .sessionManagement((session)->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return httpSecurity.build();
    }
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
    @Bean
    @Primary
    JwtDecoder jwtAccessTokenDecoder(){
        return NimbusJwtDecoder.withPublicKey(keyUtils.getAccessTokenPublicKey()).build();
    }
    @Bean
    @Primary
    JwtEncoder jwtAccessTokenEncoder(){
        JWK jwk=new RSAKey
                .Builder(keyUtils.getAccessTokenPublicKey())
                .privateKey(keyUtils.getAccessTokenPrivateKey())
                .build();
        JWKSource<SecurityContext> jwks=new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }
    @Bean
    @Qualifier("jwtRefreshTokenDecoder")
    JwtDecoder jwtRefreshTokenDecoder(){
        return NimbusJwtDecoder.withPublicKey(keyUtils.getRefreshTokenPublicKey()).build();
    }
    @Bean
    @Qualifier("jwtRefreshTokenEncoder")
    JwtEncoder jwtRefreshTokenEncoder(){
        JWK jwk=new RSAKey
                .Builder(keyUtils.getRefreshTokenPublicKey())
                .privateKey(keyUtils.getRefreshTokenPrivateKey())
                .build();
        JWKSource<SecurityContext> jwks=new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }
    @Bean
    public JwtAuthenticationProvider jwtAuthenticationProvider() {
        JwtAuthenticationProvider provider = new JwtAuthenticationProvider(jwtAccessTokenDecoder());
        provider.setJwtAuthenticationConverter(jwtAuthenticationConverter());
        return provider;
    }

}
