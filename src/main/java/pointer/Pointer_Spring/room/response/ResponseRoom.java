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
    private List<String> friends;


    public ResponseRoom(ExceptionCode exceptionCode, Room room, List<RoomMember> members) {
        super(exceptionCode);
        this.roomId = room.getRoomId();
        this.roomNm = room.getName();

        friends = new ArrayList<>();
        for (RoomMember member : members) {
            friends.add(member.getUser().getUsername());
        }
    }

}
