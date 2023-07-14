package pointer.Pointer_Spring.user.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import pointer.Pointer_Spring.config.ResponseType;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.validation.ExceptionCode;

import java.util.List;

public class UserDto {

    @Setter
    @Getter
    public static class BasicUser {
        String id;
    }

    @Getter
    public static class TokenResponse extends ResponseType {
        TokenDto tokenDto;

        public TokenResponse(ExceptionCode exceptionCode, TokenDto tokenDto) {
            super(exceptionCode);
            this.tokenDto = tokenDto;
        }
    }

    @Getter
    public static class DuplicateUserResponse extends ResponseType {

        public DuplicateUserResponse(ExceptionCode exceptionCode) {
            super(exceptionCode);
        }
    }

    @Getter
    public static class UserResponse extends ResponseType {

        public UserResponse(ExceptionCode exceptionCode) {
            super(exceptionCode);
        }
    }

    @Data
    public static class UserListResponse {

        List<UserList> userList;
        Long total;

        public UserListResponse(Long total, List<UserList> userList) {
            this.total = total;
            this.userList = userList;
        }
    }

    @Data
    public static class UserList {

        Long userId;
        String id;
        String userName;
        String file;

        public UserList(User user) {
            this.userId = user.getUserId();
            this.id = user.getId();
            this.userName = user.getName();
        }

        public UserList setFile(String file) {
            this.file = file;
            return this;
        }

    }

    @Data
    public static class UserInfoList {

        Long userId;
        String id;
        String userName;
        String file;

        public UserInfoList(User user) {
            this.userId = user.getUserId();
            this.id = user.getId();
            this.userName = user.getName();
        }

        public UserInfoList setFile(String file) {
            this.file = file;
            return this;
        }
    }
}
