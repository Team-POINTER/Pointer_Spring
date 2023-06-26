package pointer.Pointer_Spring.validation;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import pointer.Pointer_Spring.config.ResponseType;


@Getter
public class ErrorResponse extends ResponseType {

    protected ErrorResponse(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}
