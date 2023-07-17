package pointer.Pointer_Spring.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import pointer.Pointer_Spring.validation.ExceptionCode;
import pointer.Pointer_Spring.validation.HttpStatus;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"code", "message", "result"})
public class BaseResponse<T> {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private HttpStatus status;
    private final String message;
    private final String code;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;

    public BaseResponse() {
        this.message = "성공했습니다.";
        this.code = "A200";
    }

    public BaseResponse(T result) {
        this.message = "성공했습니다.";
        this.code = "A200";
        this.result = result;
    }

    public BaseResponse(ExceptionCode status) {
        this.message = status.getMessage();
        this.code = status.getCode();
    }

    public BaseResponse(ExceptionCode exceptionCode, T result) {
        this.status = exceptionCode.getStatus();
        this.message = exceptionCode.getMessage();
        this.code = exceptionCode.getCode();
        this.result = result;
    }

}
