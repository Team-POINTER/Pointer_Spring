package pointer.Pointer_Spring.user.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import pointer.Pointer_Spring.user.response.ResponseKakaoUser;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.user.dto.*;
import pointer.Pointer_Spring.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pointer.Pointer_Spring.security.JwtUtil;
import pointer.Pointer_Spring.validation.ExceptionCode;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
//@PropertySource("classpath:application.properties")
public class AuthServiceImpl implements AuthService {

    /*@Value("${kakao.restAPI}")
    private String restApiKey;

    @Value("${kakao.redirectURI}")
    private String redirectUri;*/

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /*public String getKakaoAccessToken(String code) {
        String access_Token;
        //String refresh_Token = "";
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
            //refresh_Token = jsonObject.get("refresh_token").getAsString();

            br.close();
            bw.close();
        } catch (IOException e) {
            throw new RuntimeException("Invalid code");
        }
        return access_Token;
    }*/

    @Override
    public KakaoRequestDto getKakaoUser(String token) {
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
            String name = element.get("properties").getAsJsonObject().get("nickname").getAsString();

            //System.out.println("response body : " + result);
            //System.out.println("id = " + id);
            //System.out.println("email : " + email);
            //System.out.println("name : " + name);

            return KakaoRequestDto.builder()
                    .id(id)
                    .email(email)
                    .name(name)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Invalid token");
        }
    }

    @Override
    public ResponseKakaoUser kakaoCheck(String accessToken) {
        KakaoRequestDto kakaoDto = getKakaoUser(accessToken);

        if (kakaoDto == null) {
            return new ResponseKakaoUser(ExceptionCode.USER_NOT_FOUND);
        }

        Optional<User> findUser = login(kakaoDto.getEmail()); // 회원 여부
        User user;
        ExceptionCode exception;

        if (findUser.isEmpty()) { // 회원가입
            user = signup(kakaoDto);
            exception = ExceptionCode.SIGNUP_CREATED_OK;
        }
        else {
            user = findUser.get();
            exception = ExceptionCode.SIGNUP_COMPLETE;
        }

        TokenDto tokenDto = createToken(user.getEmail(), user.getPassword());
        return new ResponseKakaoUser(exception, tokenDto);
    }

    @Transactional
    @Override
    public TokenDto createToken(String email, String password) { // token 발급
        User user = userRepository.findByEmailAndStatus(email, 1).get();
        TokenDto tokenDto;

        // token 존재 여부 or 유효기간 확인 후 (재)발급하는 함수
        // user.getToken().isEmpty() || jwtUtil.isTokenExpired(user.getToken())
        String accessToken = jwtUtil.generateToken(Map.of("mEmail", email, "mPassword",password), 7); // 유효 기간 7일
        String refreshToken = jwtUtil.generateToken(Map.of("mEmail", email, "mPassword",password), 14); // 유효 기간 14일
        tokenDto = TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        user.setToken(refreshToken);
        return tokenDto;
    }

    @Override
    public User signup(KakaoRequestDto kakaoRequestDto) { // 비밀번호 설정
        String mEmail = kakaoRequestDto.getEmail();
        boolean exist = userRepository.existsByEmailAndStatus(mEmail, 1);

        User user = User.KakaoBuilder()
                .id(kakaoRequestDto.getId())
                .email(kakaoRequestDto.getEmail())
                .name(kakaoRequestDto.getName())
                .build();
        user.changePassword(passwordEncoder.encode("1111"));

        return userRepository.save(user);
    }

    @Override
    public Optional<User> login(String email){
        return userRepository.findByEmailAndStatus(email, 1);
    }

    public ResponseKakaoUser reissue(TokenRequest tokenRequest) {
        Optional<User> findUser = userRepository.findByTokenAndStatus(tokenRequest.getRefreshToken(),1);
        String getRefreshToken = tokenRequest.getRefreshToken();
        if (findUser.isEmpty() || !(findUser.get().getToken().equals(getRefreshToken))) {
            return new ResponseKakaoUser(ExceptionCode.INVALID_REFRESH_TOKEN);
        }

        User user = findUser.get();
        TokenDto tokenDto = createToken(user.getEmail(), user.getPassword());
        return new ResponseKakaoUser(ExceptionCode.REISSUE_TOKEN, tokenDto);
    }
}
