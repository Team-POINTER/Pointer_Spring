package pointer.Pointer_Spring.friend.dto;

import lombok.Data;
import pointer.Pointer_Spring.friend.domain.Friend;
import pointer.Pointer_Spring.user.domain.User;

import java.util.List;

public class FriendDto {

    @Data
    public static class FriendListResponse {

        List<FriendList> userList;

        public FriendListResponse(List<FriendList> userList) {
            this.userList = userList;
        }
    }

    @Data
    public static class FriendInfoListResponse {

        List<FriendInfoList> friendInfoList;

        public FriendInfoListResponse(List<FriendInfoList> friendInfoList) {
            this.friendInfoList = friendInfoList;
        }
    }

    @Data
    public static class FriendList {

        String friendId;
        String friendName;
        String file;

        public FriendList(User user) {
            this.friendId = user.getId();
            this.friendName = user.getName();
        }

        public FriendList setFile(String file) {
            this.file = file;
            return this;
        }
    }

    @Data
    public static class FriendInfoList {

        String friendId;
        String friendName;
        String file;
        Friend.Relation relationship;

        public FriendInfoList(User user, Friend.Relation relationship) {
            this.friendId = user.getId();
            this.friendName = user.getName();
            this.relationship = relationship;
        }

        public FriendInfoList setFile(String file) {
            this.file = file;
            return this;
        }
    }

    @Data
    public static class FriendUserDto {
        private String id;
    }

    @Data
    public static class FindFriendDto {
        private String keyword;
    }

    @Data
    public static class UserDto {
        private User user;
    }

    @Data
    public static class RequestFriendDto {
        private String id;
        private String memberId;
    }
}
