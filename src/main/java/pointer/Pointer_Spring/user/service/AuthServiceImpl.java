package pointer.Pointer_Spring.user.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import pointer.Pointer_Spring.security.TokenProvider;
import pointer.Pointer_Spring.security.UserPrincipal;
import pointer.Pointer_Spring.user.response.ResponseKakaoUser;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.user.dto.*;
import pointer.Pointer_Spring.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pointer.Pointer_Spring.validation.CustomException;
import pointer.Pointer_Spring.validation.ExceptionCode;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@PropertySource("classpath:application.properties")
public class AuthServiceImpl implements AuthService {
    private static final Integer STATUS = 1;

    @Value("${kakao.restAPI}")
    private String restApiKey;

    @Value("${kakao.redirectURI}")
    private String redirectUri;


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    private final Integer CHECK = 1;
    private final Integer COMPLETE = 2;

    public String getKakaoAccessToken(String code) {
        String access_Token;
        //String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();


            conn.setRequestMethod("POST");
            conn.setDoOutput(true); // POST 요청

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            String sb = "grant_type=authorization_code" +
                    "&client_id=" + restApiKey + // REST_API_KEY
                    "&redirect_uri=" + redirectUri + // REDIRECT_URI
                    "&code=" + code;
            bw.write(sb);
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
            throw new CustomException(ExceptionCode.USER_KAKAO_INVALID);
        }
        return access_Token;
    }


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

            System.out.println("response body : " + result);
            return KakaoRequestDto.builder() // kakao id -> password로 이용
                    .id(id)
                    .email(email)
                    .name(name)
                    .build();
        } catch (Exception e) {
            throw new CustomException(ExceptionCode.USER_KAKAO_INVALID);
        }
    }

    @Override
    public Object saveId(UserPrincipal userPrincipal, UserDto.BasicUser userInfo) {
        User user = userRepository.findByUserIdAndStatus(userPrincipal.getId(), STATUS).orElseThrow(
                () -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                }
        );

        Optional<User> findUser = userRepository.findByIdAndStatus(userInfo.getId(), STATUS);
        if (findUser.isPresent()) { // 상대 id
                return new UserDto.DuplicateUserResponse(ExceptionCode.USER_NO_CHECK_ID); // ID 중복
        }
        else if (user.getCheckId() == 1) {
            user.setId(userInfo.getId(), COMPLETE);
            return new UserDto.UserResponse(ExceptionCode.USER_SAVE_ID_OK);
        }
        return new UserDto.UserResponse(ExceptionCode.USER_NO_CHECK_ID); // ID 중복

    }

    @Override
    public Object checkId(UserPrincipal userPrincipal, UserDto.BasicUser userInfo) {
        User user = userRepository.findByUserIdAndStatus(userPrincipal.getId(), STATUS).orElseThrow(
                () -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                }
        );

        Optional<User> findUser = userRepository.findByIdAndStatus(userInfo.getId(), STATUS);
        if (findUser.isPresent()) {
            return new UserDto.DuplicateUserResponse(ExceptionCode.SIGNUP_DUPLICATED_ID);
        }
        user.setCheckId(CHECK);

        return new UserDto.UserResponse(ExceptionCode.USER_CHECK_ID_OK);
    }

    @Override // 카카오 소셜 로그잉
    public Object kakaoCheck(String code) {
        KakaoRequestDto kakaoDto = getKakaoUser(code);

        if (kakaoDto == null) {
            return new ResponseKakaoUser(ExceptionCode.USER_NOT_FOUND);
        }

        Optional<User> findUser = userRepository.findByEmailAndTypeAndStatus(kakaoDto.getEmail(), User.SignupType.KAKAO,1);
        User user;
        ExceptionCode exception;

        if (findUser.isEmpty()) {
            user = signup(kakaoDto);
        } else {
            user = findUser.get();
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        kakaoDto.getId() // password
                )
        ); // CustomUserDetailsService.loadUserByUsername

        SecurityContextHolder.getContext().setAuthentication(authentication);

        if (user.getId().equals(User.SignupType.KAKAO+user.getEmail()) || user.getCheckId() < COMPLETE) { // 회원가입 : SignupType + email
            exception = ExceptionCode.SIGNUP_CREATED_OK;
        }
        else {
            exception = ExceptionCode.SIGNUP_COMPLETE;
        }

        TokenDto tokenDto = createToken(authentication);
        user.setToken(tokenDto.getRefreshToken());
        userRepository.save(user);

        return new UserDto.TokenResponse(exception, tokenDto);
    }


    public TokenDto createToken(Authentication authentication) { // token 발급

        String accessToken = tokenProvider.createToken(authentication, Boolean.FALSE); // access
        String refreshToken = tokenProvider.createToken(authentication, Boolean.FALSE); // refresh

        return TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public User signup(KakaoRequestDto kakaoRequestDto) { // 비밀번호 설정
        String password = passwordEncoder.encode(kakaoRequestDto.getId());

        User user = new User(kakaoRequestDto.getEmail(), User.SignupType.KAKAO.name()+kakaoRequestDto.getEmail(),
                kakaoRequestDto.getName(), password, User.SignupType.KAKAO);
        userRepository.save(user);

        return user;
    }

    @Override
    public Object reissue(UserPrincipal userPrincipal) {
        Optional<User> findUser = userRepository.findByUserIdAndStatus(userPrincipal.getId(),STATUS);

        // reissue 비교

        if (findUser.isEmpty()) {
            return new ResponseKakaoUser(ExceptionCode.INVALID_REFRESH_TOKEN);
        }

        User user = findUser.get();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        user.getId() // password 대신 id 사용
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenDto tokenDto = createToken(authentication);
        user.setToken(tokenDto.getRefreshToken());
        userRepository.save(user);

        return new UserDto.TokenResponse(ExceptionCode.REISSUE_TOKEN, tokenDto);
    }
}
