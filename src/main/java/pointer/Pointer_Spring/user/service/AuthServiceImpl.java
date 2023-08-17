package pointer.Pointer_Spring.user.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import pointer.Pointer_Spring.alarm.dto.AlarmDto;
import pointer.Pointer_Spring.alarm.repository.AlarmRepository;
import pointer.Pointer_Spring.alarm.service.KakaoPushNotiService;
import pointer.Pointer_Spring.friend.repository.FriendRepository;
import pointer.Pointer_Spring.question.repository.QuestionRepository;
import pointer.Pointer_Spring.report.repository.BlockedUserRepository;
import pointer.Pointer_Spring.report.repository.ReportRepository;
import pointer.Pointer_Spring.report.repository.RestrictedUserRepository;
import pointer.Pointer_Spring.report.repository.UserReportRepository;
import pointer.Pointer_Spring.room.domain.Room;
import pointer.Pointer_Spring.room.domain.RoomMember;
import pointer.Pointer_Spring.room.repository.RoomMemberRepository;
import pointer.Pointer_Spring.room.repository.RoomRepository;
import pointer.Pointer_Spring.security.TokenProvider;
import pointer.Pointer_Spring.security.UserPrincipal;
import pointer.Pointer_Spring.user.apple.service.AppleAuthServiceImpl;
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
import pointer.Pointer_Spring.vote.repository.VoteRepository;

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

    @Value("${default.profile.image.path}")
    private String profileImg;

    @Value("${default.background.image.path}")
    private String backgroundImg;


    private final UserRepository userRepository;
    //private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;

    private final AppleAuthServiceImpl appleAuthService;
    private final KakaoPushNotiService kakaoPushNotiService;

    private final ImageRepository imageRepository;
    private final FriendRepository friendRepository; // 자기 기준, 상대쪽 모두 제거
    private final RoomMemberRepository roomMemberRepository;
    private final RoomRepository roomRepository; // 혼자만 있는 방
    private final RestrictedUserRepository restrictedUserRepository;
    private final AlarmRepository alarmRepository;
    private final QuestionRepository questionRepository;
    private final ReportRepository reportRepository;
    private final UserReportRepository userReportRepository;
    private final BlockedUserRepository blockedUserRepository;
    private final VoteRepository voteRepository;

    private final Integer CHECK = 1;
    private final Integer COMPLETE = 2;

    private static final SecureRandom random = new SecureRandom();

    public String getKakaoAccessToken(String code, boolean web) {
        String token;
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

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(result);
            token = jsonNode.get("access_token").asText();

            br.close();
            bw.close();
        } catch (IOException e) {
            throw new CustomException(ExceptionCode.USER_KAKAO_INVALID);
        }
        return token;
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
            //String name = element.get("properties").getAsJsonObject().get("nickname").getAsString();

            System.out.println("response body : " + result);
            return KakaoRequestDto.builder() // kakao id -> password로 이용
                    .id(id)
                    .email(email)
                    .name(email.substring(0, email.indexOf("@"))) // name 가져오기
                    .token(token)
                    .build();
        } catch (Exception e) {
            throw new CustomException(ExceptionCode.USER_KAKAO_INVALID);
        }
    }

    public void kakaoLogout(String token) {
        System.out.println("token = " + token);
        String reqURL = "https://kapi.kakao.com/v1/user/logout";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + token);

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String result = "";
            String line;

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println(result);
        } catch (Exception e) {
            throw new CustomException(ExceptionCode.LOGOUT_INVALID);
        }
    }

    @Override
    @Transactional
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
            try {
                user.setId(userInfo.getId(), COMPLETE);
                userRepository.flush(); // DB 반영
            } catch (DataIntegrityViolationException e) {
                throw new CustomException(ExceptionCode.USER_DUPLICATED_ID);
            }
            // DB에 저장된 id
            String realId = userRepository.findByIdAndStatus(userInfo.getId(), STATUS).get().getId();
            return new UserDto.DuplicateUserResponse(ExceptionCode.USER_SAVE_ID_OK, realId);
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

        Optional<User> findUser = userRepository.findByEmailAndStatus(kakaoDto.getEmail(),1);
        User user;
        ExceptionCode exception;

        if (findUser.isEmpty()) {
            // 제한된 회원 확인
            if (blockedUserRepository.existsByEmail(kakaoDto.getEmail())) {
                return new UserDto.UserResponse(ExceptionCode.SIGNUP_LIMITED_ID);
            }
            user = signup(kakaoDto, User.SignupType.KAKAO.name()+kakaoDto.getEmail(), password);
        } else if (findUser.get().getType().equals(User.SignupType.APPLE)) { // email 중복
            return new UserDto.UserResponse(ExceptionCode.SIGNUP_DUPLICATED_EMAIL);
        }
        else {
            user = findUser.get();
            user.setSocialToken(code);
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

        String[] name = kakaoRequestDto.getEmail().split("@");
        User user = new User(kakaoRequestDto.getEmail(), id, name[0],
                encoder.encode(password), User.SignupType.KAKAO, kakaoRequestDto.getToken());
        userRepository.save(user);

        // 기본 이미지 추가
        imageRepository.save(new Image(profileImg, Image.ImageType.PROFILE, user));
        imageRepository.save(new Image(backgroundImg, Image.ImageType.BACKGROUND, user));

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
            // 제한된 회원 확인 : user -> 제한 회원 table?
            if (blockedUserRepository.existsByEmail(kakaoDto.getEmail())) {
                return new UserDto.UserResponse(ExceptionCode.SIGNUP_LIMITED_ID);
            }

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
        } else if (findUser.get().getType().equals(User.SignupType.APPLE)) { // email 중복
            return new UserDto.UserResponse(ExceptionCode.SIGNUP_DUPLICATED_EMAIL);
        }
        else {
            user = findUser.get();
            user.setSocialToken(code);
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

    @Transactional
    @Override
    public Object logout(UserPrincipal userPrincipal) {

        User user = userRepository.findByUserIdAndStatus(userPrincipal.getId(), STATUS).get();

        if (user.getType().equals(User.SignupType.KAKAO)) {
            kakaoLogout(user.getSocialToken());

            AlarmDto.KakaoTokenDeRegisterRequest kakaoTokenDeRegisterRequest = AlarmDto.KakaoTokenDeRegisterRequest.builder()
                    .uuid(user.getId())
                    .pushToken(user.getPushToken())
                    .pushType(user.getPushToken())
                    .build();

            kakaoPushNotiService.deRegisterKakaoToken(user.getUserId(), kakaoTokenDeRegisterRequest);
        }
        else {
            appleAuthService.logout(userPrincipal);
        }

        SecurityContextHolder.getContext().setAuthentication(null);

        return new UserDto.UserResponse(ExceptionCode.LOGOUT_OK);
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
    public Object updateMarketing(UserPrincipal userPrincipal, UserDto.UserMarketing marketing) {
        User user = userRepository.findByUserIdAndStatus(userPrincipal.getId(), STATUS).get();
        user.setMarketing(marketing.isMarketing());
        userRepository.save(user);
        return new UserDto.UserResponse(ExceptionCode.USER_MARKETING_OK);
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

    @Transactional
    @Override
    public Object resign(UserPrincipal userPrincipal) {

        User user = userRepository.findByUserIdAndStatus(userPrincipal.getId(),STATUS).get();

        friendRepository.deleteAllByUserFriendIdOrUserUserId(user.getUserId(), user.getUserId());
        imageRepository.deleteAllByUserUserId(user.getUserId());

        // roomMember status 0 처리
        List<RoomMember> roomMembers = roomMemberRepository.findByUserUserIdAndStatus(user.getUserId(), STATUS);
        for (RoomMember member : roomMembers) {
            member.getRoom().minusMemberNum();
            member.delete();
            roomMemberRepository.save(member);
        }

        // 위 내용에 의해 빈 room이 생성된 경우, room 삭제
        List<Room> rooms = roomRepository.findAllByMemberNum(0);
        for (Room room : rooms) {
            roomMemberRepository.deleteAllByUserUserId(user.getUserId());
            questionRepository.deleteAllByRoomId(room.getRoomId());
            reportRepository.deleteAllByRoomRoomId(room.getRoomId());
            // voteRepository.deleteAllByRoomId(room.getRoomId()); 투표 기록 유지

            // 삭제된 상태에서 남은 roomMember 제거
            roomMemberRepository.deleteAllByRoomRoomrId(room.getRoomId());
            roomRepository.delete(room);
        }

        // alarm 부분 제거 필요
        alarmRepository.deleteAllByReceiveUserIdOrSendUserId(user.getUserId(), user.getUserId());

        // user status 0 처리
        user.setEmail("resign"+ user.getUserId()); // 재 가입 대비 email 변경
        user.setId("resign"+ user.getUserId()); // 투표에 기록되는 내용
        user.setName("resign");
        user.delete();
        userRepository.save(user);

        userReportRepository.deleteAllByTargetUserUserId(user.getUserId());

        SecurityContextHolder.getContext().setAuthentication(null);

        if(user.getPushToken()!=null) {
            AlarmDto.KakaoTokenDeRegisterRequest kakaoTokenDeRegisterRequest = AlarmDto.KakaoTokenDeRegisterRequest.builder()
                    .uuid(user.getId())
                    .pushToken(user.getPushToken())
                    .pushType("apns")
                    .build();

            kakaoPushNotiService.deRegisterKakaoToken(user.getUserId(), kakaoTokenDeRegisterRequest);
            user.setPushToken(null);
            user.setDeviceId(null);
            user.setApnsEnv(null);
        }
        userRepository.save(user);

        return new UserDto.UserResponse(ExceptionCode.RESIGN_OK);
    }
}
