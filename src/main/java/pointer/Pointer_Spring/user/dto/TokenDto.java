package pointer.Pointer_Spring.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TokenDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;

    private Long userId;
    private String accessToken;
    private String refreshToken;
}
