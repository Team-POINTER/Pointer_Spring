package pointer.Pointer_Spring.room.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import pointer.Pointer_Spring.config.ResponseType;
import pointer.Pointer_Spring.room.domain.Room;
import pointer.Pointer_Spring.room.domain.RoomMember;
import pointer.Pointer_Spring.validation.ExceptionCode;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Schema(name = "ResponseRoom", description = "ResponseRoom")
public class ResponseRoom extends ResponseType {
    @JsonInclude(NON_NULL)
    Object data;
    @JsonInclude(NON_NULL)
    Integer currentPage;//for offset

    public ResponseRoom(ExceptionCode exceptionCode, Object data){
        super(exceptionCode);
        this.data = data;
    }
    public ResponseRoom(ExceptionCode exceptionCode, Integer currentPage, Object data){
        super(exceptionCode);
        this.currentPage = currentPage;
        this.data = data;
    }
    public ResponseRoom(ExceptionCode exceptionCode){
        super(exceptionCode);
    }

}
