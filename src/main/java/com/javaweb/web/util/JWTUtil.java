package com.javaweb.web.util;

import com.javaweb.web.service.MyUserDetailsService;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Component
public class JWTUtil {
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 giờ
    @Autowired private MyUserDetailsService userDetailsService;

    public String getUsernameFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JWT", e);
        }
    }
    public int getAdminIdFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getIntegerClaim("id");
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JWT", e);
        }
    }
    public int getUserIdFromToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getIntegerClaim("id");
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JWT", e);
        }
    }
}