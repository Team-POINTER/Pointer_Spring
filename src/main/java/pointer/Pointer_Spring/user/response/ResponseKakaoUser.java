package pointer.Pointer_Spring.user.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import pointer.Pointer_Spring.user.dto.TokenDto;
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

    private Long userId;

    public ResponseKakaoUser(ExceptionCode exceptionCode, @Nullable TokenDto token, Long userId) {
        super(exceptionCode);
        this.token = token;
        this.userId = userId;
    }
}
