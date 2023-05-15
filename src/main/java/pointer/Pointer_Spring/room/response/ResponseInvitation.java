package pointer.Pointer_Spring.room.response;

import lombok.Getter;
import pointer.Pointer_Spring.config.ResponseType;
import pointer.Pointer_Spring.validation.ExceptionCode;

@Getter
public class ResponseInvitation extends ResponseType {

    private String url;

    public ResponseInvitation(ExceptionCode exceptionCode, String token) {
        super(exceptionCode);
        this.url = "/room/invitation/"+token;
    }
}
