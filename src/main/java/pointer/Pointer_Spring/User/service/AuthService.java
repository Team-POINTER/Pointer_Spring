package pointer.Pointer_Spring.User.service;

import pointer.Pointer_Spring.User.domain.User;
import pointer.Pointer_Spring.User.dto.KakaoRequestDto;
import pointer.Pointer_Spring.User.response.ResponseKakaoUser;

import java.util.Optional;

public interface AuthService {

    User join(KakaoRequestDto kakaoRequestDto);
    Optional<User> login(KakaoRequestDto kakaoRequestDto);

    ResponseKakaoUser kakaoCheck(String accessToken);

    String checkToken(String email, String password);
    KakaoRequestDto getKakaoUser(String token);
}
