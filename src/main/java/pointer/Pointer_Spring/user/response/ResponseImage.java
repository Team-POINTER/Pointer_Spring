package pointer.Pointer_Spring.user.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import pointer.Pointer_Spring.config.ResponseType;
import pointer.Pointer_Spring.validation.ExceptionCode;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
public class ResponseImage extends ResponseType {
    @JsonInclude(NON_NULL)
    private String imageUrl;
    @JsonInclude(NON_NULL)
    private Object imageUrls;

    public ResponseImage(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
    public ResponseImage(ExceptionCode exceptionCode, String imageUrl) {
        super(exceptionCode);
        this.imageUrl = imageUrl;
    }
    public ResponseImage(ExceptionCode exceptionCode, Object imageUrls) {
        super(exceptionCode);
        this.imageUrls = imageUrls;
    }
}
