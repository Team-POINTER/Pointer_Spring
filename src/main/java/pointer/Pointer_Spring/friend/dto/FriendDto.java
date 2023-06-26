package pointer.Pointer_Spring.friend.dto;

import lombok.Data;
import pointer.Pointer_Spring.friend.domain.Friend;
import pointer.Pointer_Spring.user.domain.User;

import java.util.List;

public class FriendDto {

    @Data
    public static class FriendListResponse {

        List<FriendList> userList;
        Long total;

        public FriendListResponse(List<FriendList> userList, Long total) {
            this.total = total;
            this.userList = userList;
        }
        public FriendListResponse(List<FriendList> userList) {
            this.userList = userList;
        }
    }

    @Data
    public static class FriendInfoListResponse {

        List<FriendInfoList> friendInfoList;
        Long total;

        public FriendInfoListResponse(Long total, List<FriendInfoList> friendInfoList) {
            this.total = total;
            this.friendInfoList = friendInfoList;
        }
        public FriendInfoListResponse(List<FriendInfoList> friendInfoList) {
            this.friendInfoList = friendInfoList;
        }
    }

    @Data
    public static class FriendList {
        Long friendId;
        String id;
        String friendName;
        String file;

        public FriendList(User user) {
            this.friendId = user.getUserId();
            this.id = user.getId();
            this.friendName = user.getName();
        }

        public FriendList setFile(String file) {
            this.file = file;
            return this;
        }
    }

    @Data
    public static class FriendInfoList {

        Long friendId;
        String id;
        String friendName;
        String file;
        Friend.Relation relationship;

        public FriendInfoList(Friend friend, User user, Friend.Relation relationship) {
            this.friendId = friend.getId();
            this.id = user.getId();
            this.friendName = friend.getUserFriendName();
            this.relationship = relationship;
        }

        public FriendInfoList setFile(String file) {
            this.file = file;
            return this;
        }
    }

    @Data
    public static class FriendUserDto {
        private Long userId;
        // pageable
        private int lastPage;

    }

    @Data
    public static class FindFriendDto {
        private Long userId;
        private String keyword;
        // pageable
        private int lastPage;

    }

    @Data
    public static class UserDto {
        private User user;
    }

    @Data
    public static class RequestFriendDto {
        private Long userId;
        private Long memberId;
    }
}

