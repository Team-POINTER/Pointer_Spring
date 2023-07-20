package pointer.Pointer_Spring.user.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoRequestDto {

    private String id;
    private String email;
    private String name;
    private String token;
    //private String password;

    /*public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, "1111");
    }*/
}
