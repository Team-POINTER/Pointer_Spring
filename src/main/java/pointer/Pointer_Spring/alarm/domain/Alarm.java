package pointer.Pointer_Spring.alarm.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "Alarm")
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "alarm_type")
    private AlarmType type;

    @Column(name = "response_user_id")
    private Long responseUserId;


    public enum AlarmType {
        CHAT, ACTIVE, EVENT
    }

    @Builder
    public Alarm(AlarmType type, Long responseUserId) {
        this.type = type;
        this.responseUserId = responseUserId;
    }
}
