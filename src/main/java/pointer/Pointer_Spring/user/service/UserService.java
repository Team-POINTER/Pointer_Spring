package pointer.Pointer_Spring.user.service;

import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.user.dto.UserDto;
import pointer.Pointer_Spring.user.response.ResponseUser;

public interface UserService {
    Long getPoints(Long userId);
    ResponseUser updateNm(Long userId, String name);
    ResponseUser updateId(Long userId, String id);
    UserDto.UserInfo getUserInfo(Long userId);
    User createUser(UserDto.CreateUserRequest createUserRequest);
}
