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

        Long userFriendId;//친구의 고유 아이디
        String friendName;

        public FriendList(User user) {
            this.userFriendId = user.getUserId();
            this.friendName = user.getName();
        }
    }

    @Data
    public static class FriendInfoList {

        Long userFriendId;
        String friendName;
        Friend.Relation relationship;

        public FriendInfoList(User user, Friend.Relation relationship) {
            this.userFriendId = user.getUserId();
            this.friendName = user.getName();
            this.relationship = relationship;
        }
    }

    @Data
    public static class FriendUserDto {
        private Long userFriendId;
    }

    @Data
    public static class FindFriendDto {
        private String keyword;
    }

    @Data
    public static class UserDto {
        private User user;
    }

//    @Data
//    public static class RequestFriendDto {
//        private String id;
//        private String memberId;
//    }
}