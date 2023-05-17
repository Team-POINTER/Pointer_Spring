package pointer.Pointer_Spring.room.response;

import lombok.Builder;
import lombok.Getter;
import pointer.Pointer_Spring.config.ResponseType;
import pointer.Pointer_Spring.room.domain.Room;
import pointer.Pointer_Spring.room.domain.RoomMember;
import pointer.Pointer_Spring.validation.ExceptionCode;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ResponseRoom extends ResponseType {

    private Long roomId;
    private String roomNm;
    private Integer memberNum;
    private Integer votingNum;
    private List<RoomMember> roomMembers;

    public ResponseRoom(ExceptionCode exceptionCode){
        super(exceptionCode);
    }

    public ResponseRoom(ExceptionCode exceptionCode, Room room) {
        super(exceptionCode);
        this.roomId = room.getRoomId();
        this.roomNm = room.getName();
        this.memberNum = room.getMemberNum();
        this.votingNum = room.getVotingNum();
    }
    public ResponseRoom(ExceptionCode exceptionCode, Room room, List<RoomMember> roomMembers) {
        super(exceptionCode);
        this.roomId = room.getRoomId();
        this.roomNm = room.getName();
        this.memberNum = room.getMemberNum();
        this.votingNum = room.getVotingNum();
        this.roomMembers = roomMembers;
    }

}
