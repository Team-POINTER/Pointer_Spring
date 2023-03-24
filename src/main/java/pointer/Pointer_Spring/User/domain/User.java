package pointer.Pointer_Spring.User.domain;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pointer.Pointer_Spring.config.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "USER")
public class User extends BaseEntity {

    //    @Getter(AccessLevel.NONE)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true)
    private Long userId;

    @Column(nullable = false)
    private String id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String nickname;

    private String username;
    private String password;
    private Authority authority;

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

    @Builder(builderMethodName = "KakaoBuilder")
    public User(String email, String id, String nickanme, String username) {
        this.email = email;
        this.id = id;
        this.nickname = nickanme;
        this.username = username;
        this.authority = Authority.ROLE_USER;
    }

    @Builder(builderMethodName = "AuthorityBuilder")
    public User(String email, String password, Authority authority) {
        this.email = email;
        this.password = password;
        this.authority = authority;
    }
}
