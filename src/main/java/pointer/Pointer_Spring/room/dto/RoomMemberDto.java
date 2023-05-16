package pointer.Pointer_Spring.room.dto;

import lombok.Data;

public class RoomMemberDto {
    @Data
    public static class ModifyRoomNmRequest {
        Long userId;
        Long roomId;
        String privateRoomNm;//개별 roomName
    }
}
