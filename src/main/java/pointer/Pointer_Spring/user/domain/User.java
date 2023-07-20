package pointer.Pointer_Spring.user.domain;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
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

    @Column(name = "chat_alarm_flag")
    private boolean chatAlarmFlag;

    @Column(name = "active_alarm_flag")
    private boolean activeAlarmFlag;

    @Column(name = "event_alarm_flag")
    private boolean eventAlarmFlag;


    // social login
    public enum SignupType {
        KAKAO, APPLE
    }
    @Enumerated(EnumType.STRING)
    private SignupType type;

    // role
    public enum Role {
        USER, ADMIN
    }

    @ColumnDefault("0")
    private int checkId;

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

    @Column(length = 400)
    private String token;

    @Column(length = 400)
    private String socialToken;

    private Long point;

    // builder
    @Builder
    public User(String id, String email, String name, SignupType type) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.type = type;
        this.chatAlarmFlag = true;
        this.activeAlarmFlag = true;
        this.eventAlarmFlag = true;
    }

    // test builder
    @Builder
    public User(String id, String email, String name, String password) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
    }


    //@Builder(builderMethodName = "KakaoBuilder")
    public User(String email, String id, String name, String password, SignupType type, String socialToken) {
        this.email = email;
        this.id = id;
        this.name = name;
        this.password = password;
        this.socialToken = socialToken;
        this.type = type;
        this.chatAlarmFlag = true;
        this.activeAlarmFlag = true;
        this.eventAlarmFlag = true;
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

    public void changeName(String newName) {
        this.name = newName;
    }
    public void changeId(String newId) {
        this.id = newId;
    }
    public void changePassword(String password) {
        this.password = password;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setId(String id, int checkId) {
        this.id = id;
        this.checkId = checkId;
    }

    public void setCheckId(int checkId) {
        this.checkId = checkId;
    }

    public void updateRoomLimit(Integer roomLimit) {
        this.roomLimit = roomLimit;
    }

}
