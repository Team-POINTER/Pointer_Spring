package pointer.Pointer_Spring.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.WeakKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import pointer.Pointer_Spring.config.AppProperties;
import pointer.Pointer_Spring.user.dto.UserDto;
import pointer.Pointer_Spring.validation.ExceptionCode;

import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private AppProperties appProperties;

    private static final Logger logger = LoggerFactory.getLogger(TokenAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            String jwt = getJwtFromRequest(request);
            if (StringUtils.hasText(jwt)) {
                if (tokenProvider.isTokenExpired(jwt)) {
                    createResponse(ExceptionCode.EXPIRED_JWT_TOKEN, response);
                } else {
                    Jwts.parser().setSigningKey(appProperties.getAuth().getTokenSecret()).parseClaimsJws(jwt);

                    Long userId = tokenProvider.getUserIdFromToken(jwt);
                    UserDetails userDetails = customUserDetailsService.loadUserById(userId);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                            userDetails.getPassword(), userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    filterChain.doFilter(request, response);
                }
            } else {
                filterChain.doFilter(request, response);
            }

        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
            createResponse(ExceptionCode.INVALID_JWT_SIGNATURE, response);
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
            createResponse(ExceptionCode.INVALID_JWT_TOKEN, response);
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
            createResponse(ExceptionCode.EXPIRED_JWT_TOKEN, response);
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
            createResponse(ExceptionCode.UNSUPPORTED_JWT_TOKEN, response);
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
            createResponse(ExceptionCode.JWT_CLAIMS_STRING_IS_EMPTY, response);
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void createResponse(ExceptionCode exceptionCode, ServletResponse response) throws IOException {
        ObjectNode json = new ObjectMapper().createObjectNode();
        json.put("state", HttpServletResponse.SC_UNAUTHORIZED); // exceptionCode.getStatus().getValue()
        json.put("code", exceptionCode.getCode());

        //String message = exceptionCode.getMessage();
        //String escapedDescription = UriUtils.encode(message, "UTF-8");
        json.put("message", exceptionCode.getMessage());

        String newResponse = new ObjectMapper().writeValueAsString(json);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setContentLength(newResponse.getBytes(StandardCharsets.UTF_8).length);
        response.getWriter().write(newResponse);
    }
}
