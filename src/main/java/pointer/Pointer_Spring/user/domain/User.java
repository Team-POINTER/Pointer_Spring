package pointer.Pointer_Spring.user.domain;

import javax.persistence.*;

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

    @Column(name = "name", nullable = false)
    private String name;
    private String password;


    // social login
    public enum SignupType {
        KAKAO, APPLE
    }
    @Enumerated(EnumType.ORDINAL)
    private SignupType type;

    // role
    public enum Role {
        USER, ADMIN
    }

    @Column(nullable = false,columnDefinition = "boolean default false")
    private boolean checkId;

    @Enumerated(EnumType.ORDINAL)
    private Role role;
    @Column(name = "service_agree")
    private boolean serviceAgree; // 필수
    @Column(name = "service_age")
    private boolean serviceAge;
    @Column(name = "marketing")
    private boolean marketing;

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
    public User(String email, String id, String name, String password, SignupType type) {
        this.email = email;
        this.id = id;
        this.name = name;
        this.password = password;
        this.type = type;
    }

    @Builder(builderMethodName = "AuthorityBuilder")
    public User(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public void setService(boolean serviceAgree, boolean serviceAge, boolean marketing) {
        this.serviceAge = serviceAge;
        this.serviceAgree = serviceAgree;
        this.marketing = marketing;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCheckId(boolean checkId) {
        this.checkId = checkId;
    }

    public void updateRoomLimit(Integer roomLimit) {
        this.roomLimit = roomLimit;
    }

}
