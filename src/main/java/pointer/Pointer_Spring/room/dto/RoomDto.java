package pointer.Pointer_Spring.room.dto;

import java.util.List;
import lombok.Data;
import pointer.Pointer_Spring.room.domain.Room;
import pointer.Pointer_Spring.room.domain.RoomMember;

public class RoomDto {

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
        String questionNm;
        int memberCnt;
        String firstNm;

        public ListRoom(Room room) {
            this.roomId = room.getRoomId();
            this.roomNm = room.getName();
        }

        public void setRoomInfo() {
            this.questionNm = questionNm;
            this.memberCnt = memberCnt;
            this.firstNm = firstNm;
        }
    }


    @Data
    public static class CreateRequest {

        Long userId;
        String roomNm;
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

        public DetailResponse(Room room) {
            this.roomId = room.getRoomId();
            this.roomNm = room.getName();
        }
    }

    @Data
    public static class InviteRequest {

        Long roomId;
        Long[] userIdArr;
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

}
