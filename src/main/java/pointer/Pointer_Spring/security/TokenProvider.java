package pointer.Pointer_Spring.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.WeakKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pointer.Pointer_Spring.config.AppProperties;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.user.repository.UserRepository;
import pointer.Pointer_Spring.validation.ExceptionCode;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

@PropertySource("classpath:application.yml")
@Service
public class TokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private UserRepository userRepository;

    public String createToken(Authentication authentication, boolean refresh) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        int time;
        if (refresh) {
            time = (int) (appProperties.getAuth().getTokenExpirationDay() * 3 * 60 * 24); // 15일
        } else {
            time = (int) (appProperties.getAuth().getTokenExpirationDay() * 60 * 24); // 5일
        }

        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusDays(time).toInstant()))
                .signWith( SignatureAlgorithm.HS256, appProperties.getAuth().getTokenSecret())
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder().
                setSigningKey(appProperties.getAuth().getTokenSecret()).build()
                .parseClaimsJws(token).getBody();

        return Long.parseLong(claims.getSubject());
    }

    public ExceptionCode validateToken(String authToken) {
        try {
            Jwts.parserBuilder().
                    setSigningKey(appProperties.getAuth().getTokenSecret()).build().parseClaimsJws(authToken).getBody();
            return ExceptionCode.TOKEN_SUCCESS;
        } catch (WeakKeyException ex) {
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

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder().
                    setSigningKey(appProperties.getAuth().getTokenSecret()).build()
                    .parseClaimsJws(token).getBody();

            Date expirationDate = claims.getExpiration();
            return expirationDate.before(new Date());
        } catch (ExpiredJwtException ex) {
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isLogout(Long userId) {
        Optional<User> findUser = userRepository.findByUserIdAndTokenExpiredAndStatus(userId, true, 1);
        return findUser.isPresent();
    }
}
