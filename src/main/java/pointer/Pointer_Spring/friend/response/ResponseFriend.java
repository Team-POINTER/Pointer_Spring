package pointer.Pointer_Spring.friend.response;

import lombok.Getter;
import pointer.Pointer_Spring.config.ResponseType;
import pointer.Pointer_Spring.validation.ExceptionCode;

import java.util.List;

@Getter
public class ResponseFriend extends ResponseType {

    public ResponseFriend(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

}
