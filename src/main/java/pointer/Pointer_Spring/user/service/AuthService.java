package pointer.Pointer_Spring.user.service;

import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.user.dto.KakaoRequestDto;
import pointer.Pointer_Spring.user.dto.TokenDto;
import pointer.Pointer_Spring.user.response.ResponseKakaoUser;

import java.util.Optional;

public interface AuthService {
    User signup(KakaoRequestDto kakaoRequestDto);
    Optional<User> login(String email);

    ResponseKakaoUser kakaoCheck(String accessToken);

    TokenDto createToken(String email, String password);
    KakaoRequestDto getKakaoUser(String token);
}
