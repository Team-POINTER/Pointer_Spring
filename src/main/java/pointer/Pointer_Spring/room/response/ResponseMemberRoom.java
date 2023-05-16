package pointer.Pointer_Spring.room.response;

import pointer.Pointer_Spring.config.ResponseType;
import pointer.Pointer_Spring.room.domain.Room;
import pointer.Pointer_Spring.room.domain.RoomMember;
import pointer.Pointer_Spring.validation.ExceptionCode;

import java.util.ArrayList;
import java.util.List;

public class ResponseMemberRoom extends ResponseType {
    private List<RoomMember> roomMembers;
    private RoomMember roomMember;

    public ResponseMemberRoom(ExceptionCode exceptionCode, List<RoomMember> roomMembers) {
        super(exceptionCode);
        for (RoomMember roomMember : roomMembers) {
            this.roomMembers.add(roomMember);
        }
    }
    public ResponseMemberRoom(ExceptionCode exceptionCode, RoomMember roomMember) {
        super(exceptionCode);
        this.roomMember = roomMember;
    }

    public ResponseMemberRoom(ExceptionCode exceptionCode) {//only need success/fail result about RoomMember
        super(exceptionCode);
    }

}
