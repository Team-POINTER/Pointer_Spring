package pointer.Pointer_Spring.room.dto;

import lombok.Data;

public class RoomMemberDto {
    @Data
    public static class ModifyRoomNmRequest {
        Long roomId;
        String privateRoomNm;//개별 roomName
    }
}
