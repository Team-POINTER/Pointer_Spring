package pointer.Pointer_Spring.User.domain;

import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import pointer.Pointer_Spring.User.dto.KakaoRequestDto;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Table(name = "user")
@Entity
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Setter
    private String id;
    private String email;
    @Setter
    private String username;
    @Setter
    private String nickname;
    private String password;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Builder
    public User(String email, String password, Authority authority) {
        this.email = email;
        this.password = password;
        this.authority = authority;
    }

    // 카카오
    @Builder
    public User(String id, String email, String username, String nickname) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.nickname = nickname;
        this.authority = Authority.ROLE_USER;
    }

}
