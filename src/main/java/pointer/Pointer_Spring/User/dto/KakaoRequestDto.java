package pointer.Pointer_Spring.User.dto;

import org.springframework.security.crypto.password.PasswordEncoder;
import pointer.Pointer_Spring.User.domain.Authority;
import pointer.Pointer_Spring.User.domain.User;
import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KakaoRequestDto {

    private String id;
    private String email;
    private String nickname;
    private String username;
    private String password;


    public User toUser(PasswordEncoder passwordEncoder) {
        return User.builder()
                .email(email)

                .password(passwordEncoder.encode(password))
                .authority(Authority.ROLE_USER)
                .build();
    }

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(email, password);
    }
}
