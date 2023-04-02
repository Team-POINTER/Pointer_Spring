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

    // social login
    public enum Type {
        APP, KAKAO, APPLE
    }
    private Type type;
    private boolean social;

    // role
    public enum Role {
        USER, ADMIN
    }

    private Role role;
    private boolean agreement;
    private boolean options;
    private String token;

    // builder

    @Builder(builderMethodName = "KakaoBuilder")
    public User(String email, String id, String nickname, String password) {
        this.email = email;
        this.id = id;
        this.nickname = nickname;
        this.password = password;
        this.type = Type.KAKAO;
        this.social = true;
    }

    @Builder(builderMethodName = "AuthorityBuilder")
    public User(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void addRole(Role role) {
        this.role = role;
    }

    public void setOption(boolean options) {
        this.options = options;
    }

    public void setAgreement(boolean agreement) {
        this.agreement = agreement;
    }

}
