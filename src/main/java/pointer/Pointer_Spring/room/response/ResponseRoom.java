package pointer.Pointer_Spring.room.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import pointer.Pointer_Spring.config.ResponseType;
import pointer.Pointer_Spring.room.domain.Room;
import pointer.Pointer_Spring.room.domain.RoomMember;
import pointer.Pointer_Spring.validation.ExceptionCode;

import java.util.ArrayList;
import java.util.List;

@Getter
@Schema(name = "ResponseRoom", description = "ResponseRoom")
public class ResponseRoom extends ResponseType {

//    private Long roomId;
//    private String roomNm;
//    private Integer memberNum;
//    private Integer votingNum;
//    private List<RoomMember> roomMembers;
    Object data;

    public ResponseRoom(ExceptionCode exceptionCode){
        super(exceptionCode);
    }

    public ResponseRoom(ExceptionCode exceptionCode, Object data){

        super(exceptionCode);
        this.data = data;
    }

}
