package pointer.Pointer_Spring.config;

import lombok.Getter;
import pointer.Pointer_Spring.validation.ExceptionCode;

@Getter
public class ResponseType {
    private final Integer status;
    private final String code;
    private final String message;

    public ResponseType(ExceptionCode exceptionCode) {
        this.status = exceptionCode.getStatus().getValue();
        this.code = exceptionCode.getCode();
        this.message = exceptionCode.getMessage();
    }
}
