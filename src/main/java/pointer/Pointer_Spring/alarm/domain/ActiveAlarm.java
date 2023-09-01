package pointer.Pointer_Spring.alarm.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pointer.Pointer_Spring.user.domain.User;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//Entity(name = "ActiveAlarm")
public class ActiveAlarm {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "active_alarm_id")
//    private Long id;

//    활동 알람 아이디
//    친구 요청 보낸 사람
//    친구 요청 받은 사람
//    활동 알람 타입 (콕찌르기 or 친구 요청)
//
//    @Column(name = "request_user_id")
//    private Long requestUserId;
//
//    @Column(name = "response_user_id")
//    private Long responseUserId;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "active_alarm_type")
//    private ActiveAlarmType type;
//
//
//
//    public enum ActiveAlarmType {
//
//
//
//    }
//
//    @Builder
//    public ActiveAlarm(ActiveAlarmType type) {
////        this.requestUserId = requestUserId;
////        this.responseUserId = responseUserId;
//        this.type = type;
//    }


}
