package pointer.Pointer_Spring.User.service;

import pointer.Pointer_Spring.User.domain.User;
import pointer.Pointer_Spring.User.dto.KakaoRequestDto;
import pointer.Pointer_Spring.User.dto.TokenDto;
import pointer.Pointer_Spring.User.response.ResponseKakaoUser;

import java.util.Optional;

public interface AuthService {
    User signup(KakaoRequestDto kakaoRequestDto);
    Optional<User> login(String email);

    ResponseKakaoUser kakaoCheck(String accessToken);

    TokenDto createToken(String email, String password);
    KakaoRequestDto getKakaoUser(String token);
}
