package pointer.Pointer_Spring.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.ContentCachingResponseWrapper;
import pointer.Pointer_Spring.validation.ExceptionCode;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    // JWT 토큰의 인증 정보를 현재 쓰레드의 SecurityContext 에 저장하는 역할 수행
    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        // Request Header 에서 토큰을 꺼냄
        String jwt = resolveToken((HttpServletRequest) request);

        // validateToken 으로 토큰 유효성 검사
        if (StringUtils.hasText(jwt)) {
            System.out.println("jwt = " + jwt);
            ContentCachingResponseWrapper responseWrapper =
                    new ContentCachingResponseWrapper((HttpServletResponse) response);
            try {
                Map<String, Object> values = jwtUtil.validateToken(jwt);
                chain.doFilter(request, responseWrapper);
            } catch (ExpiredJwtException expiredJwtException) {
                response = createResponse(ExceptionCode.EXPIRED_TOKEN, response);

            } catch (MalformedJwtException malformedJwtException) {
                response = createResponse(ExceptionCode.MALFORMED_TOKEN, response);

            } catch (Exception exception) {
                response = createResponse(ExceptionCode.UNAUTHORIZED_TOKEN, response);
            }
            //Authentication authentication = tokenProvider.getAuthentication(jwt);
            //SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }

    // Request Header 에서 토큰 정보를 꺼내오기
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private ServletResponse createResponse(ExceptionCode exceptionCode, ServletResponse response) throws IOException {
        ObjectNode json = new ObjectMapper().createObjectNode();
        json.put("state", exceptionCode.getStatus().getValue());
        json.put("code", exceptionCode.getCode());
        json.put("message", exceptionCode.getMessage());

        String newResponse = new ObjectMapper().writeValueAsString(json);
        response.setContentType("application/json");
        response.setContentLength(newResponse.length());
        response.getOutputStream().write(newResponse.getBytes());

        return response;
    }
}
