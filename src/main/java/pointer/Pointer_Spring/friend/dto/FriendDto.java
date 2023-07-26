package pointer.Pointer_Spring.friend.dto;

import lombok.Data;
import lombok.Getter;
import pointer.Pointer_Spring.config.ResponseType;
import pointer.Pointer_Spring.friend.domain.Friend;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.validation.ExceptionCode;

import java.util.List;


public class FriendDto {

    /*@Getter
    public static class FriendListResponse extends ResponseType {

        List<FriendList> userList;
        Long total;

        public FriendListResponse(ExceptionCode exceptionCode, List<FriendList> userList, Long total) {
            super(exceptionCode);
            this.total = total;
            this.userList = userList;
        }

        public FriendListResponse(ExceptionCode exceptionCode, List<FriendList> userList) {
            super(exceptionCode);
            this.userList = userList;
        }
    }*/

    @Getter
    public static class FriendInfoListResponse extends ResponseType {

        List<FriendInfoList> friendInfoList;
        String name;
        Long total;

        public FriendInfoListResponse(ExceptionCode exceptionCode,  String name, Long total, List<FriendInfoList> friendInfoList) {
            super(exceptionCode);
            this.total = total;
            this.name = name;
            this.friendInfoList = friendInfoList;
        }
        public FriendInfoListResponse(ExceptionCode exceptionCode, List<FriendInfoList> friendInfoList) {
            super(exceptionCode);
            this.friendInfoList = friendInfoList;
        }
    }

    @Getter
    public static class FriendRoomInfoListResponse extends ResponseType {

        List<FriendInfoList> friendInfoList;
        String name;
        Long total;

        public FriendRoomInfoListResponse(ExceptionCode exceptionCode,  String name, Long total, List<FriendInfoList> friendInfoList) {
            super(exceptionCode);
            this.total = total;
            this.name = name;
            this.friendInfoList = friendInfoList;
        }
        public FriendRoomInfoListResponse(ExceptionCode exceptionCode, List<FriendInfoList> friendInfoList) {
            super(exceptionCode);
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
            this.friendId = user.getUserId();
            this.id = user.getId();
            this.friendName = friend.getFriendName();
            this.relationship = relationship;
        }

        public FriendInfoList setFile(String file) {
            this.file = file;
            return this;
        }
    }

    @Data
    public static class FriendRoomInfoList {

        Long friendId;
        String id;
        String friendName;
        int status;
        String file;
        Friend.Relation relationship;

        public FriendRoomInfoList(Friend friend, User user, int status, Friend.Relation relationship) {
            this.friendId = user.getUserId();
            this.id = user.getId();
            this.friendName = friend.getFriendName();
            this.relationship = relationship;
            this.status = status;
        }

        public FriendRoomInfoList setFile(String file) {
            this.file = file;
            return this;
        }
    }


    @Data
    public static class FriendUserDto {
        //private Long userId;
        // pageable
        private int lastPage;

    }

    @Data
    public static class FindFriendDto {
        //private Long userId;
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
        //private Long userId;
        private Long memberId;
    }
}

