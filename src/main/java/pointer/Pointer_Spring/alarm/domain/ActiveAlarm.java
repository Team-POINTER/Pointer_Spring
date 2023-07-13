package pointer.Pointer_Spring.alarm.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "ActiveAlarm")
public class ActiveAlarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "active_alarm_id")
    private Long id;

//    활동 알람 아이디
//    친구 요청 보낸 사람
//    친구 요청 받은 사람
//    활동 알람 타입 (콕찌르기 or 친구 요청)

    @Column(name = "request_user_id")
    private Long requestUserId;

    @Column(name = "response_user_id")
    private Long responseUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "active_alarm_type")
    private ActiveAlarmType type;

    @Column(name = "alarm_content")
    private String content;

    public enum ActiveAlarmType {
        POKE(1, "얼른 질문을 확인하고 지목해봐요."), FRIEND_REQUEST(2, "친구 요청이 왔어요.");

        private int code;
        private String message;

        ActiveAlarmType(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getMessage() {
            return POKE.message;
        }
    }

    @Builder
    public ActiveAlarm(Long requestUserId, Long responseUserId, ActiveAlarmType type, String content) {
        this.requestUserId = requestUserId;
        this.responseUserId = responseUserId;
        this.type = type;
        this.content = content;
    }


}
