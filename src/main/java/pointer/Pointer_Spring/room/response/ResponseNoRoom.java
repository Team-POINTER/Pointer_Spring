package pointer.Pointer_Spring.room.response;

import lombok.Getter;
import pointer.Pointer_Spring.config.ResponseType;
import pointer.Pointer_Spring.room.domain.Room;
import pointer.Pointer_Spring.room.domain.RoomMember;
import pointer.Pointer_Spring.validation.ExceptionCode;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ResponseNoRoom extends ResponseType {

    public ResponseNoRoom(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}
