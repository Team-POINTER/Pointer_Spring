package pointer.Pointer_Spring.user.dto;

import lombok.Data;
import pointer.Pointer_Spring.user.domain.User;

import java.util.List;

public class UserDto {

    @Data
    public static class UserListResponse {

        List<UserList> userList;

        public UserListResponse(List<UserList> userList) {
            this.userList = userList;
        }
    }

    @Data
    public static class UserList {

        Long userId;
        String id;
        String userName;

        public UserList(User user) {
            this.userId = user.getUserId();
            this.id = user.getId();
            this.userName = user.getName();
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
