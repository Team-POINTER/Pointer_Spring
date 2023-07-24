package pointer.Pointer_Spring.alarm.domain;

import com.google.gson.internal.bind.JsonTreeReader;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import pointer.Pointer_Spring.config.BaseEntity;

import javax.persistence.*;

@Entity(name = "ChatAlarm")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatAlarm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_alarm_id")
    private Long id;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "receive_user_id")
    private Long receiveUserId;

    @Column(name = "send_user_id")
    private Long sendUserId;

    @Column(name = "read_check")
    private boolean readCheck;

    @Builder
    public ChatAlarm(Long roomId, Long receiveUserId, Long sendUserId) {
        this.roomId = roomId;
        this.receiveUserId = receiveUserId;
        this.sendUserId = sendUserId;
        this.readCheck = false;
    }

}
