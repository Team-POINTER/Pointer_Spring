package pointer.Pointer_Spring.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoDto {
    private String id;
    private String email;
    private String nickname;

    @Builder
    public KakaoDto(String id, String email, String nickname) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
    }
}
