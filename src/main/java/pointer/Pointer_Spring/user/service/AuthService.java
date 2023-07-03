package pointer.Pointer_Spring.user.service;

import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.user.dto.KakaoRequestDto;
import pointer.Pointer_Spring.user.dto.TokenDto;
import pointer.Pointer_Spring.user.dto.UserDto;

import java.util.Optional;

public interface AuthService {
    User signup(KakaoRequestDto kakaoRequestDto);

    Object kakaoCheck(String accessToken);

    TokenDto createToken(String email, User.SignupType type, String password);

    KakaoRequestDto getKakaoUser(String token);

    Object saveId(UserDto.UserInfo userInfo);
}
