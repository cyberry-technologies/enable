package enable.enableuserservice.Configuration.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {
    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + SecurityConstants.JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(SecurityConstants.JWT_SECRET.getBytes()))
                .compact();
    }

    public String getEmailFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(SecurityConstants.JWT_SECRET.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SecurityConstants.JWT_SECRET.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            throw new AuthenticationCredentialsNotFoundException("Invalid JWT token", ex);
        } catch (ExpiredJwtException ex) {
            throw new AuthenticationCredentialsNotFoundException("Expired JWT token", ex);
        } catch (UnsupportedJwtException ex) {
            throw new AuthenticationCredentialsNotFoundException("Unsupported JWT token", ex);
        } catch (IllegalArgumentException ex) {
            throw new AuthenticationCredentialsNotFoundException("JWT claims string is empty", ex);
        }
    }
}

