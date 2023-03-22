package pointer.Pointer_Spring.User.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import pointer.Pointer_Spring.User.domain.Authority;
import pointer.Pointer_Spring.User.domain.User;
import pointer.Pointer_Spring.User.domain.RefreshToken;
import pointer.Pointer_Spring.User.dto.*;
import pointer.Pointer_Spring.User.repository.RefreshTokenRepository;
import pointer.Pointer_Spring.User.repository.UserRepository;
import pointer.Pointer_Spring.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pointer.Pointer_Spring.validation.ExceptionCode;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:config.properties")
public class AuthService {

    @Value("${kakao.restAPI}")
    private String restApiKey;

    @Value("${kakao.redirectURI}")
    private String redirectUri;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public UserResponseDto signup(KakaoRequestDto kakaoRequestDto, String username) {
        if (userRepository.existsByEmail(kakaoRequestDto.getEmail())) {
            throw new RuntimeException(ExceptionCode.SIGNUP_COMPLETE.toString());
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException(ExceptionCode.SIGNUP_DUPLICATED_USERNAME.toString());
        }
        User user = User.builder()
                .authority(Authority.ROLE_USER)
                .email(kakaoRequestDto.getEmail())
                .build();
        user.setNickname(kakaoRequestDto.getNickname());
        user.setId(kakaoRequestDto.getId());
        user.setUsername(username);
        userRepository.save(user);
        return UserResponseDto.of(userRepository.save(user));
    }


    public String getKakaoAccessToken(String code) {
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();


            conn.setRequestMethod("POST");
            conn.setDoOutput(true); // POST 요청

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=" + restApiKey); // REST_API_KEY
            sb.append("&redirect_uri=" + redirectUri); // REDIRECT_URI
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();
            access_Token = jsonObject.get("access_token").getAsString();
            refresh_Token = jsonObject.get("refresh_token").getAsString();


            br.close();
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException("Invalid code");
        }
        return access_Token;
    }

    public KakaoRequestDto getKakaoUser(String token) { //throws BaseException
        String reqURL = "https://kapi.kakao.com/v2/user/me";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }

            br.close();

            JsonObject element = JsonParser.parseString(result).getAsJsonObject();
            String id = element.get("id").getAsString();
            boolean hasEmail = element.get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            String email = "";
            if (hasEmail) {
                email = element.get("kakao_account").getAsJsonObject().get("email").getAsString();
            }
            String nickname = element.get("properties").getAsJsonObject().get("nickname").getAsString();

            //System.out.println("response body : " + result);
            System.out.println("id = " + id);
            System.out.println("email : " + email);
            System.out.println("nickname : " + nickname);

            return KakaoRequestDto.builder()
                    .id(id)
                    .email(email)
                    .nickname(nickname)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Invalid token");
        }
    }


    @Transactional
    public Object KakaoCheck(String code, String username) {
        String accessToken = getKakaoAccessToken(code);
        KakaoRequestDto kakaoDto = getKakaoUser(accessToken);
        if (kakaoDto == null) {
            return ExceptionCode.USER_NOT_FOUND;
        }

        Optional<User> findUser = userRepository.findByEmail(kakaoDto.getEmail());
        if (findUser.isPresent()) { // 로그인]
            return ExceptionCode.SIGNUP_COMPLETE;
        }
        else {
            User user = User.builder()
                    .authority(Authority.ROLE_USER)
                    .email(kakaoDto.getEmail())
                    .build();
            user.setNickname(kakaoDto.getNickname());
            user.setId(kakaoDto.getId());
            user.setUsername(username);
            userRepository.save(user);

            return ExceptionCode.SIGNUP_CREATED_OK;
        }
    }

}
