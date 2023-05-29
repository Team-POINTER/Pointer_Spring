package pointer.Pointer_Spring.room.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import pointer.Pointer_Spring.friend.domain.Friend;
import pointer.Pointer_Spring.room.domain.Room;
import pointer.Pointer_Spring.room.domain.RoomMember;
import pointer.Pointer_Spring.user.domain.User;

public class RoomDto {
    @Data
    public static class FindRoomRequest{
        Long userId;
    }

    @Data
    public static class ListResponse {

        List<ListRoom> roomList;

        public ListResponse(List<ListRoom> roomList) {
            this.roomList = roomList;
        }
    }

    @Data
    public static class ListRoom {

        Long roomId;
        String roomNm;
        String question;
        int memberCnt;
        //String firstNm;

        public ListRoom(RoomMember roomMember) {
            this.roomId = roomMember.getRoom().getRoomId();
            this.roomNm = roomMember.getPrivateRoomNm();
        }

        public void setRoomInfo() {
            this.question = question;
            this.memberCnt = memberCnt;
            //this.firstNm = firstNm;
        }
    }


    @Data
    public static class CreateRequest {
        Long userId;
        String roomNm;//roomName
    }

    @Data
    public static class CreateResponse {

        String accessToken;
        String refreshToken;
        DetailResponse detailResponse;

        public CreateResponse(String accessToken, String refreshToken,
            DetailResponse detailResponse) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.detailResponse = detailResponse;
        }
    }

    @Data
    public static class DetailResponse {

        Long roomId;
        String roomNm;
        private Integer memberNum;
        private Integer votingNum;
        private List<RoomMemberResopnose> roomMembers;

        public DetailResponse(Room room, List<RoomMemberResopnose> roomMembers) {
            this.roomId = room.getRoomId();
            this.roomNm = room.getName(); // defaultname
            this.memberNum = room.getMemberNum();
            this.votingNum = room.getVotingNum();
            this.roomMembers = roomMembers;
        }
    }
    @Data
    public static class RoomMemberResopnose{//나중에 token으로 user 구분 시 없애기
        Long userId;
        String id;
        String name;
        String privateRoomNm;
        public RoomMemberResopnose(RoomMember roomMember){
            this.userId = roomMember.getUser().getUserId();
            this.id = roomMember.getUser().getId();
            this.name = roomMember.getUser().getName();
            this.privateRoomNm = roomMember.getPrivateRoomNm();
        }
    }
    @Data
    public static class ExitRequest{//나중에 token으로 user 구분 시 없애기
        String id;//user 고유 string id
    }

    @Data
    public static class InviteRequest {
        Long id;//초대하는 유저
        Long roomId;
        List<Long> friendIdList;
    }

    @Data
    public static class InviteResponse {

        String accessToken;
        String refreshToken;
        List<InviteMember> inviteMemberList;

        public InviteResponse(String accessToken, String refreshToken,
            List<InviteMember> inviteMemberList) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.inviteMemberList = inviteMemberList;
        }
    }

    @Data
    public static class InviteMember {

        Long userId;
        String nickNm;

        public InviteMember(RoomMember roomMember) {
            this.userId = roomMember.getUser().getUserId();
            this.nickNm = roomMember.getUser().getName();
        }
    }

    @Data
    public static class IsInviteMember{
        boolean isInvite;
        public enum Reason{
            INVITE, OVERLIMIT, ALREADY
        }
        Reason reason;
        Long userId;
        String nickNm;
        LocalDateTime updateAt;

        public IsInviteMember( User user, Friend f) {
            this.isInvite = true;
            this.userId = user.getUserId();
            this.nickNm = f.getFriendName();
            this.reason = Reason.INVITE;
            this.updateAt = f.getUpdateAt();
        }
        public void updateIsInvite(boolean isInvite, Reason reason){
            this.isInvite = isInvite;
            this.reason = reason;
        }
    }

    @Data
    public static class InviteMemberResponse{
        List<IsInviteMember> isInviteMembers;

        public InviteMemberResponse(List<IsInviteMember> isInviteMembers) {
            this.isInviteMembers = isInviteMembers;
        }

    }

}
