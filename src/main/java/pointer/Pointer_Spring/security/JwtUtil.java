package pointer.Pointer_Spring.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class JwtUtil {

    /*@Value("${jwt.secret}")
    private String key;*/

    private final Key key;

    public JwtUtil(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Map<String, Object> valueMap, int days) {

        // header
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", "JWT");
        headers.put("alg", "HS256");

        // payload
        Map<String, Object> payloads = new HashMap<>();
        payloads.putAll(valueMap);

        int time = days*60*24; // 테스트를 위해서 임시로 1분 설정 -> 60*24 일 단위
        return Jwts.builder()
                .setHeader(headers)
                .setClaims(payloads)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(time).toInstant()))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();

    }

    public Map<String, Object> validateToken(String token) {
        Map<String, Object> claim;
        try {
            claim = parseClaims(token);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("an error occured during getting username from token", e);
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("the token is expired and not valid anymore", e);
        } catch(SignatureException e){
            throw new RuntimeException("Authentication Failed. Username or Password not valid.");
        }

        return claim;
    }

    public Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // 토큰 만료 3일전인지
    public Boolean isTokenExpired(String token){
        Date expiration = parseClaims(token).getExpiration();
        return expiration.before(Date.from(ZonedDateTime.now().minusDays(3).toInstant()));
    }
}