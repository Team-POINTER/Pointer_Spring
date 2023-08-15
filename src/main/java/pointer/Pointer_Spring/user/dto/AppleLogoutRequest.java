package pointer.Pointer_Spring.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AppleLogoutRequest {
    private String uuid;
    private String pushType;
    private String pushToken;
}
