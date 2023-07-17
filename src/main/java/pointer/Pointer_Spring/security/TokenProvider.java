package pointer.Pointer_Spring.security;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pointer.Pointer_Spring.config.AppProperties;
import pointer.Pointer_Spring.validation.ExceptionCode;
import java.time.ZonedDateTime;
import java.util.Date;

@PropertySource("classpath:application.yml")
@Service
public class TokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    @Autowired
    private AppProperties appProperties;

    public String createToken(Authentication authentication, boolean day) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate;
        if (day) {
            expiryDate = new Date(now.getTime() + appProperties.getAuth().getTokenExpirationMsec()); // 5일
        } else {
            expiryDate = new Date(now.getTime() + 3*appProperties.getAuth().getTokenExpirationMsec()); // 15일
        }

/*        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, appProperties.getAuth().getTokenSecret())
                .compact();*/

        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, appProperties.getAuth().getTokenSecret())
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(appProperties.getAuth().getTokenSecret())
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public ExceptionCode validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(appProperties.getAuth().getTokenSecret()).parseClaimsJws(authToken);
            return ExceptionCode.TOKEN_SUCCESS;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
            return ExceptionCode.INVALID_JWT_SIGNATURE;
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
            return ExceptionCode.INVALID_JWT_TOKEN;
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
            return ExceptionCode.EXPIRED_JWT_TOKEN;
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
            return ExceptionCode.UNSUPPORTED_JWT_TOKEN;
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
            return ExceptionCode.JWT_CLAIMS_STRING_IS_EMPTY;
        }
    }
}
