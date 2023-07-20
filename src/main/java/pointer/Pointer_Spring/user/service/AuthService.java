package pointer.Pointer_Spring.user.service;

import pointer.Pointer_Spring.security.UserPrincipal;
import pointer.Pointer_Spring.user.dto.KakaoRequestDto;
import pointer.Pointer_Spring.user.dto.UserDto;

import java.util.Optional;

public interface AuthService {
    Object kakaoCheck(String accessToken);
    KakaoRequestDto getKakaoUser(String token);
    Object webKakaoCheck(String code);

    Object saveId(UserPrincipal userPrincipal, UserDto.BasicUser userInfo);
    Object checkId(UserPrincipal userPrincipal, UserDto.BasicUser userInfo);
    Object reissue(UserPrincipal userPrincipal);

    Object resign(UserPrincipal userPrincipal);
}
