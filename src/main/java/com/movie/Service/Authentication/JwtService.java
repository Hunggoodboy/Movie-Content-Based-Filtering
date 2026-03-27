package com.movie.Service.Authentication;

import com.movie.DTO.Request.IntrospectRequest;
import com.movie.DTO.Response.IntrospectResponse;
import com.movie.DTO.Response.UserResponse;
import com.movie.Entity.User;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    public String generateToken(User user) throws JOSEException {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("devteria.com")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .claim("userId", user.getId())
                .claim("role", "User")
                .build();
        Payload payload = new Payload(claimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        jwsObject.sign(new MACSigner(secret));
        return jwsObject.serialize();
    }
    // Giải mã Token
    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        String token = request.getToken();

        JWSVerifier verifier = new MACVerifier(secret);
        SignedJWT signedJWT = SignedJWT.parse(token);
        // Xác thực token
        boolean verify = signedJWT.verify(verifier);
        Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
        return IntrospectResponse.builder()
                .valid(verify && expiration.after(new Date()))
                .build();
    }

    //Lấy userId và Role từ Token
    public UserResponse findUser(String authHeader) throws ParseException, JOSEException {
        String token = authHeader.substring(7);
        IntrospectRequest introspectRequest = IntrospectRequest.builder().token(token).build();
        if(!introspect(introspectRequest).isValid()){
            throw new RuntimeException("Tài khoản không hợp lệ");
        }
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
        return UserResponse.builder()
                .userId(claimsSet.getStringClaim("userId"))
                .role(claimsSet.getStringClaim("role"))
                .build();
    }
}
