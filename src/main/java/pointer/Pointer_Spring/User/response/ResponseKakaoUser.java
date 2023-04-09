package pointer.Pointer_Spring.User.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import pointer.Pointer_Spring.User.dto.TokenDto;
import pointer.Pointer_Spring.config.ResponseType;
import pointer.Pointer_Spring.validation.ExceptionCode;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseKakaoUser extends ResponseType {
    public ResponseKakaoUser(ExceptionCode exceptionCode) {
        super(exceptionCode);
    }

    @Nullable
    @Autowired
    private TokenDto token;

    public ResponseKakaoUser(ExceptionCode exceptionCode, @Nullable TokenDto token) {
        super(exceptionCode);
        this.token = token;
    }
}
