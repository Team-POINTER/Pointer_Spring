package pointer.Pointer_Spring.friend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import pointer.Pointer_Spring.config.ResponseType;
import pointer.Pointer_Spring.friend.domain.Friend;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.validation.ExceptionCode;

import java.util.List;


public class FriendDto {

    @Getter
    public static class RoomFriendListResponse extends ResponseType {

        List<FriendRoomInfoList> friendList;
        Long total;
        int currentPage;

        public RoomFriendListResponse(ExceptionCode exceptionCode, List<FriendRoomInfoList> friendList, Long total, int currentPage) {
            super(exceptionCode);
            this.total = total;
            this.friendList = friendList;
            this.currentPage = currentPage;
        }
    }

    @Getter
    public static class FriendInfoListResponse extends ResponseType {

        List<FriendInfoList> friendInfoList;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        String name;
        Long total;
        int currentPage;

        public FriendInfoListResponse(ExceptionCode exceptionCode,  String name, Long total, List<FriendInfoList> friendInfoList, int currentPage) {
            super(exceptionCode);
            this.total = total;
            this.name = name;
            this.friendInfoList = friendInfoList;
            this.currentPage = currentPage;
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
        int currentPage;

        public FriendRoomInfoListResponse(ExceptionCode exceptionCode,  String name, Long total, List<FriendInfoList> friendInfoList, int currentPage) {
            super(exceptionCode);
            this.total = total;
            this.name = name;
            this.friendInfoList = friendInfoList;
            this.currentPage = currentPage;
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
        int relationship;

        public FriendInfoList(Friend friend, User user, Friend.Relation relationship) {
            this.friendId = user.getUserId();
            this.id = user.getId();
            this.friendName = friend.getFriendName();
            this.relationship = relationship.ordinal();
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
        String file;
        int status;

        public FriendRoomInfoList(User user, int status) {
            this.friendId = user.getUserId();
            this.id = user.getId();
            this.friendName = user.getName();
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
        private Long userId;
        private String keyword;
        // pageable
        private int lastPage;

    }

    @Data
    public static class FindFriendFriendDto {
        private Long userId;
        private String keyword;
        // pageable
        private int lastPage;

        public FindFriendFriendDto(Long userId, int lastPage) {
            this.userId = userId;
            this.lastPage = lastPage;
        }
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

