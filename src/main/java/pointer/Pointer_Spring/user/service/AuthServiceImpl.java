package pointer.Pointer_Spring.user.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import pointer.Pointer_Spring.friend.domain.Friend;
import pointer.Pointer_Spring.friend.repository.FriendRepository;
import pointer.Pointer_Spring.question.domain.Question;
import pointer.Pointer_Spring.question.repository.QuestionRepository;
import pointer.Pointer_Spring.room.domain.Room;
import pointer.Pointer_Spring.room.domain.RoomMember;
import pointer.Pointer_Spring.room.repository.RoomMemberRepository;
import pointer.Pointer_Spring.room.repository.RoomRepository;
import pointer.Pointer_Spring.security.TokenProvider;
import pointer.Pointer_Spring.security.UserPrincipal;
import pointer.Pointer_Spring.user.domain.Image;
import pointer.Pointer_Spring.user.repository.ImageRepository;
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
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

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

    @Value("${kakao.web.redirectURI}")
    private String webRedirectUri;


    private final UserRepository userRepository;
    //private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    private final ImageRepository imageRepository;
    private final FriendRepository friendRepository; // 자기 기준, 상대쪽 모두 제거
    private final RoomMemberRepository roomMemberRepository;
    private final RoomRepository roomRepository; // 혼자만 있는 방
    private final QuestionRepository questionRepository; // 삭제 되는 방의 질문


    private final Integer CHECK = 1;
    private final Integer COMPLETE = 2;

    private static final SecureRandom random = new SecureRandom();

    public String getKakaoAccessToken(String code, boolean web) {
        String access_Token;
        //String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();


            conn.setRequestMethod("POST");
            conn.setDoOutput(true); // POST 요청

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));

            String uri;
            if (web) uri = webRedirectUri;
            else uri = redirectUri;

            String sb = "grant_type=authorization_code" +
                    "&client_id=" + restApiKey + // REST_API_KEY
                    "&redirect_uri=" + uri + // REDIRECT_URI
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
                    .token(token)
                    .build();
        } catch (Exception e) {
            throw new CustomException(ExceptionCode.USER_KAKAO_INVALID);
        }
    }

    @Override
    public Object saveId(UserPrincipal userPrincipal, UserDto.BasicUser userInfo) {
        User user = userRepository.findByUserIdAndStatus(userPrincipal.getId(), STATUS).get();
        /*.orElseThrow(
                () -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                }
        );*/

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

        String password = "1111"; // 오류
        KakaoRequestDto kakaoDto = getKakaoUser(code);

        if (kakaoDto == null) {
            return new ResponseKakaoUser(ExceptionCode.USER_NOT_FOUND);
        }

        Optional<User> findUser = userRepository.findByEmailAndTypeAndStatus(kakaoDto.getEmail(), User.SignupType.KAKAO,1);
        User user;
        ExceptionCode exception;

        if (findUser.isEmpty()) {
            // 제한된 회원 확인 : user -> 제한 회원 table?
            /*if (~Repository.findByEmailAndStatus(kakaoDto.getEmail(), STATUS).isPresent()) {
                return new UserDto.UserResponse(ExceptionCode.SIGNUP_LIMITED_ID);
            }*/
            user = signup(kakaoDto, User.SignupType.KAKAO.name()+kakaoDto.getEmail(), password);
        } else {
            user = findUser.get();
        }

        if (user.getId().equals(User.SignupType.KAKAO+user.getEmail()) || user.getCheckId() < COMPLETE) { // 회원가입 : SignupType + email
            exception = ExceptionCode.SIGNUP_CREATED_OK;
        }
        else {
            exception = ExceptionCode.SIGNUP_COMPLETE;
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        "1111" // password
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenDto tokenDto = createToken(authentication, user.getUserId());
        user.setToken(tokenDto.getRefreshToken());
        userRepository.save(user);

        return new UserDto.TokenResponse(exception, tokenDto);
    }


    public TokenDto createToken(Authentication authentication, Long userId) { // token 발급

        String accessToken = tokenProvider.createToken(authentication, Boolean.FALSE); // access
        String refreshToken = tokenProvider.createToken(authentication, Boolean.TRUE); // refresh

        return TokenDto.builder()
                .userId(userId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public User signup(KakaoRequestDto kakaoRequestDto, String id, String password) { // 비밀번호 설정

        // {id}ENCODED_PASSWORD 형태
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        User user = new User(kakaoRequestDto.getEmail(), id, kakaoRequestDto.getName(),
                encoder.encode(password), User.SignupType.KAKAO, kakaoRequestDto.getToken());
        userRepository.save(user);
        return user;
    }

    public String generateRandomId() {
        int randomNumber = ThreadLocalRandom.current().nextInt(5, 15 + 1);
        return RandomStringUtils.randomAlphanumeric(randomNumber);
    }

    @Override // 카카오 소셜 로그잉
    public Object webKakaoCheck(String code) {
        String password = "1111";

        KakaoRequestDto kakaoDto = getKakaoUser(code);

        if (kakaoDto == null) {
            return new ResponseKakaoUser(ExceptionCode.USER_NOT_FOUND);
        }

        Optional<User> findUser = userRepository.findByEmailAndTypeAndStatus(kakaoDto.getEmail(), User.SignupType.KAKAO,1);
        User user;

        if (findUser.isEmpty()) {
            String id = null;
            for (int i = 0; i < 10; i++) {
                id = generateRandomId();
                if (userRepository.findByIdAndStatus(id, 1).isEmpty()) {
                    break;
                }
                else if (i == 9) {
                    return new ResponseKakaoUser(ExceptionCode.USER_EXCEED_ID); // ID 생성 횟수 초과
                }
            }
            user = signup(kakaoDto, id, password);
            user.setId(id, COMPLETE);
        } else {
            user = findUser.get();
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        "1111" // password
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenDto tokenDto = createToken(authentication, user.getUserId());
        tokenDto.setId(user.getId());

        user.setToken(tokenDto.getRefreshToken());
        userRepository.save(user);

        return new UserDto.TokenResponse(ExceptionCode.SIGNUP_COMPLETE, tokenDto);
    }

    @Override
    public Object saveAgree(UserPrincipal userPrincipal, UserDto.UserAgree agree) {
        if (!agree.isServiceAgree() || !agree.isServiceAge()) {
            return new UserDto.UserResponse(ExceptionCode.USER_AGREE_INVALID);
        }
        User user = userRepository.findByUserIdAndStatus(userPrincipal.getId(), STATUS).get();
        user.setService(agree);
        userRepository.save(user);
        return new UserDto.UserResponse(ExceptionCode.USER_AGREE_OK);
    }

    @Override
    public Object reissue(UserPrincipal userPrincipal) {
        Optional<User> findUser = userRepository.findByUserIdAndStatus(userPrincipal.getId(),STATUS);

        // reissue 비교

        /*if (findUser.isEmpty()) {
            return new ResponseKakaoUser(ExceptionCode.INVALID_REFRESH_TOKEN);
        }*/

        User user = findUser.get();
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        "1111" //user.getPassword() // password
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        TokenDto tokenDto = createToken(authentication, user.getUserId());
        user.setToken(tokenDto.getRefreshToken());
        userRepository.save(user);

        return new UserDto.TokenResponse(ExceptionCode.REISSUE_TOKEN, tokenDto);
    }

    @Override
    public Object resign(UserPrincipal userPrincipal) {
        /*Optional<User> findUser = userRepository.findByUserIdAndStatus(userPrincipal.getId(),STATUS);
        if (findUser.isEmpty()) {
            return new ResponseKakaoUser(ExceptionCode.INVALID_REFRESH_TOKEN);
        }*/

        User user = userRepository.findByUserIdAndStatus(userPrincipal.getId(),STATUS).get();

        List<Friend> friends = friendRepository.findByUserUserIdOrUserFriendIdAndStatus(user.getUserId(), user.getUserId(), STATUS);
        for (Friend friend : friends) friend.delete();

        List<Image> images = imageRepository.findByUserUserIdAndStatus(user.getUserId(), STATUS);
        for (Image image : images) image.delete();

        List<RoomMember> roomMembers = roomMemberRepository.findByUserUserIdAndStatus(user.getUserId(), STATUS);
        for (RoomMember member : roomMembers) {
            member.getRoom().updateMemberNum();
            member.delete();
        }

        List<Room> rooms = roomRepository.findBymemberNumAndStatus(0, STATUS);
        for (Room room : rooms) {
            for (Question question : room.getQuestions()) {
                question.delete();
            }
            room.delete();
        }

        // alarm 부분은 status가 없어서 임시 제거 불가?

        userRepository.delete(user);

        return new UserDto.UserResponse(ExceptionCode.RESIGN_OK);
    }
}
