package io.agileintelligence.ppmtool.security;

import io.agileintelligence.ppmtool.domain.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static io.agileintelligence.ppmtool.security.SecurityConstants.EXPIRATION_TIME;
import static io.agileintelligence.ppmtool.security.SecurityConstants.SECRET;

@Component
public class JwtTokenProvider {
    //Generate the token
    public String generateToken(Authentication auth) {
        final User user = (User)auth.getPrincipal();
        final Date now = new Date(System.currentTimeMillis());
        final Date expirationDate = new Date(now.getTime() + EXPIRATION_TIME);
        final String userId = Long.toString(user.getId());
        final Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
        claims.put("username", user.getUsername());
        claims.put("fullName", user.getFullName());
        return Jwts.builder()
                .setSubject(userId)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }
    // Validate the token
    // Get user Id from token

}
