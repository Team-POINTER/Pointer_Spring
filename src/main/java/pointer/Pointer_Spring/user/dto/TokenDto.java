package pointer.Pointer_Spring.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import pointer.Pointer_Spring.validation.ExceptionCode;

@Getter
@Setter
@Builder
public class TokenDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ExceptionCode exceptionCode;

    private Long userId;
    private String accessToken;
    private String refreshToken;
}
