package pointer.Pointer_Spring.user.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import pointer.Pointer_Spring.config.ResponseType;
import pointer.Pointer_Spring.validation.ExceptionCode;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
public class ResponseUser extends ResponseType {

    @JsonInclude(NON_NULL)
    private Long points;
    @JsonInclude(NON_NULL)
    private Object results;
    public ResponseUser(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
    public ResponseUser(ExceptionCode exceptionCode, Long points) {
        super(exceptionCode);
        this.points = points;
    }
    public ResponseUser(ExceptionCode exceptionCode, Object results) {
        super(exceptionCode);
        this.results = results;
    }
}
