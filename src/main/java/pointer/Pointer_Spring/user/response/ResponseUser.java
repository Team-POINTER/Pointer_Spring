package pointer.Pointer_Spring.user.response;

import lombok.Getter;
import pointer.Pointer_Spring.config.ResponseType;
import pointer.Pointer_Spring.validation.ExceptionCode;

@Getter
public class ResponseUser extends ResponseType {

    public ResponseUser(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}
