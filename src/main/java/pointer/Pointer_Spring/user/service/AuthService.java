package pointer.Pointer_Spring.user.service;

import pointer.Pointer_Spring.security.UserPrincipal;
import pointer.Pointer_Spring.user.dto.KakaoRequestDto;
import pointer.Pointer_Spring.user.dto.UserDto;

public interface AuthService {
    Object kakaoCheck(String accessToken);
    KakaoRequestDto getKakaoUser(String token);
    Object webKakaoCheck(String code);

    Object saveAgree(UserPrincipal userPrincipal, UserDto.UserAgree agree);
    Object saveId(UserPrincipal userPrincipal, UserDto.BasicUser userInfo);
    Object checkId(UserPrincipal userPrincipal, UserDto.BasicUser userInfo);
    Object reissue(UserPrincipal userPrincipal);

    Object resign(UserPrincipal userPrincipal);
}
