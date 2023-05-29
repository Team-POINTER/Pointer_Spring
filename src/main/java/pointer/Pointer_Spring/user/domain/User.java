package pointer.Pointer_Spring.User.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pointer.Pointer_Spring.config.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "User")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", unique = true)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String id;
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;
    private String password;

    // social login

    public enum SignupType {
        KAKAO, APPLE
    }
    @Column(name = "signup_type")
    private SignupType type;

    // role
    public enum Role {
        USER, ADMIN
    }
    @Column(name = "role")
    private Role role;
    @Column(name = "service_agree")
    private boolean serviceAgree;//필수동의
    @Column(name = "marketing")
    private boolean marketing;
    @Column(name = "service_age")
    private boolean serviceAge;

    @Column(name = "room_limit")
    private Integer roomLimit;

    @Column(length = 1000)
    private String token;

    // builder
    @Builder
    public User(String id, String email, String name, SignupType type) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.type = type;
    }


    @Builder(builderMethodName = "KakaoBuilder")
    public User(String email, String id, String name, String password) {
        this.email = email;
        this.id = id;
        this.name = name;
        this.password = password;
        this.type = SignupType.KAKAO;
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

    public void setOption(boolean marketing) {
        this.marketing = marketing;
    }

    public void setAgreement(boolean serviceAgree) {
        this.serviceAgree = serviceAgree;
    }

    public void updateRoomLimit(Integer roomLimit) {
        this.roomLimit = roomLimit;
    }

}
