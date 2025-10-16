package com.javaweb.web.service;

import com.javaweb.web.entity.Admin;
import com.javaweb.web.entity.Authentications;
import com.javaweb.web.entity.Users;
import com.javaweb.web.repository.AdminRepo;
import com.javaweb.web.repository.UsersRepo;
import com.javaweb.web.util.AuthenticationUtil;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.AccessLevel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    @Autowired
    UsersRepo usersRepo;
    @Autowired
    AdminRepo adminRepo;
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;
    @Autowired
    MyUserDetailsService myUserDetailsService;

    public AuthenticationUtil authenticate(Authentications authentications) {
        var user = usersRepo.findByName(authentications.getName()).orElseThrow(() -> new RuntimeException("user not found"));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated =  passwordEncoder.matches(authentications.getPassword(),user.getPasswordHash());
        if (!authenticated)throw new RuntimeException("password not match");
        UserDetails userDetails = myUserDetailsService.loadUserByUsername(user.getName());// hoặc user implement UserDetails luôn

        // 👇 Bước mới: tạo Authentication Token
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // 👇 Bước mới: set vào SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        var token = generateToken(user);
        return AuthenticationUtil.builder()
                .authenticated(true)
                .token(token)
                .build();
    }
    private String generateToken(Users users) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(users.getName())
                .issuer("devteria.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .claim("id", users.getId())
                .jwtID(UUID.randomUUID().toString())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }
    private String generateTokenAd(Admin admin) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(admin.getName())
                .issuer("devteria.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("id", admin.getId())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }
    public AuthenticationUtil authenticateAdmin(Authentications authentications) {
        var admin = adminRepo.findByName(authentications.getName()).orElseThrow(() -> new RuntimeException("admin not found"));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated =  passwordEncoder.matches(authentications.getPassword(),admin.getPasswordHash());
        if (!authenticated)throw new RuntimeException("password not match");
        var token = generateTokenAd(admin);
        return AuthenticationUtil.builder()
                .authenticated(true)
                .token(token)
                .build();
    }
    public boolean validateToken(String token) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            jwsObject.verify(new MACVerifier(SIGNER_KEY.getBytes()));

            JWTClaimsSet claims = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());

            Date expiration = claims.getExpirationTime();
            return expiration.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
    public Authentication getAuthentication(String token) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);
            jwsObject.verify(new MACVerifier(SIGNER_KEY.getBytes()));

            JWTClaimsSet claims = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());

            String username = claims.getSubject();
            int userId = claims.getIntegerClaim("id");

            // 👇 Nếu bạn có UserDetailsService:
            UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);

            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        } catch (Exception e) {
            return null;
        }
    }

}
