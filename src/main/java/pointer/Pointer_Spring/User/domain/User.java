package pointer.Pointer_Spring.User.domain;

import lombok.AccessLevel;
import lombok.*;
import javax.persistence.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pointer.Pointer_Spring.config.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "USER")
public class User extends BaseEntity {
    @Getter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", unique = true)
    private Long userId;

    @Column(nullable = false)
    private String id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String nickname;

    private String password;

    public enum Type {
        APP, KAKAO, APPLE
    }

    @Column(nullable = false)
    private Type type;

    @Builder
    public User(String id, String email, String nickname, Type type) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.type = type;
    }
}
