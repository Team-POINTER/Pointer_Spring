package pointer.Pointer_Spring.user.dto;

import lombok.Data;
import lombok.Getter;
import org.hibernate.validator.constraints.UniqueElements;
import pointer.Pointer_Spring.friend.dto.FriendDto;
import lombok.Setter;
import pointer.Pointer_Spring.config.ResponseType;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.validation.ExceptionCode;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

public class UserDto {

    @Setter
    @Getter
    public static class UserInfo {
        int userId;
        String id;
    }

    @Getter
    public static class UserResponse extends ResponseType {
        int userId;

        public UserResponse(ExceptionCode exceptionCode, int userId) {
            super(exceptionCode);
            this.userId = userId;
        }
    }

    @Getter
    public static class DuplicateUserResponse extends ResponseType {

        public DuplicateUserResponse(ExceptionCode exceptionCode) {
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

    @Data
    public static class UserInfo {

        Long userId;
        String id;
        String userName;
        Long point;
        ImageDto.ImageUrlResponse imageUrls;

        public UserInfo(User user, ImageDto.ImageUrlResponse imageUrlResponse) {
            this.userId = user.getUserId();
            this.id = user.getId();
            this.userName = user.getName();
            this.point = user.getPoint();
            this.imageUrls = imageUrlResponse;

        }
    }
    @Getter
    public static class UpdateUserNmRequest {
        @NotBlank(message = "이름은 필수 입력 항목입니다.")
        @Size(min = 1, max = 8, message = "이름은 1자에서 8자 사이여야 합니다.")
        private String name;
        public UpdateUserNmRequest(){}
        public UpdateUserNmRequest(String name){
            this.name = name;
        }

    }
    @Getter
    public static class UpdateIdRequest {
        private String id;
        public UpdateIdRequest(){}
        public UpdateIdRequest(String id){
            this.id = id;
        }
    }


    //더미데이터를 위한 dto
    @Getter
    public static class CreateUserRequest {
        private String id;
        private String email;
        private String name;
        private String password;
        private User.SignupType type = User.SignupType.KAKAO;
        private User.Role role = User.Role.USER;
        private boolean serviceAgree = true;
        private boolean serviceAge = true;
        private boolean marketing = true;
        private Integer roomLimit = 0;
        String token = "";
        private Long point = 0L;

        public CreateUserRequest(String id, String email, String name, String password){
            this.id = id;
            this.email = email;
            this.name = name;
            this.password = password;
        }
        public CreateUserRequest(){}
        public CreateUserRequest(String id, String email, String name, String password,String token){
            this.id = id;
            this.email = email;
            this.name = name;
            this.password = password;
            this.token = token;
        }

        public CreateUserRequest(String id, String email, String name,String token, boolean serviceAgree, boolean serviceAge, boolean marketing ){
            this.id = id;
            this.email = email;
            this.name = name;
            this.token = token;
            this.marketing= marketing;
            this.serviceAgree = serviceAgree;
            this.serviceAge = serviceAge;
        }
    }
}
