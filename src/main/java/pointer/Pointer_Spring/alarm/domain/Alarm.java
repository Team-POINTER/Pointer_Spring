package pointer.Pointer_Spring.alarm.domain;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
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

    @Column(name = "send_user_id")
    private Long sendUserId;

    @Column(name = "receive_user_id")
    private Long receiveUserId;

    @Column(name = "read_check")
    private boolean readCheck;

    @Column(name = "alarm_content")
    private String content;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alarm_id")
    private ChatAlarm chatAlarm;

    @Column(name = "need_id")
    private Long needId;


    public enum AlarmType {
        CHAT(0, "채팅이 왔어요."),
        POKE(1, "얼른 질문을 확인하고 지목해봐요."),
        FRIEND_REQUEST(2, "로부터 친구 요청이 왔어요."),
        FRIEND_ACCEPT(3, "로부터 친구 수락되었습니다."),
        QUESTION(4, "질문이 왔어요."),
        EVENT(5, "이벤트가 있어요.");

        private int code;
        private String message;

        AlarmType(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    @Builder
    public Alarm(AlarmType type, Long sendUserId, Long receiveUserId, String content, ChatAlarm chatAlarm, Long needId) {
        this.type = type;
        this.sendUserId = sendUserId;
        this.receiveUserId = receiveUserId;
        this.needId = needId;
        this.content = content;
        this.chatAlarm = chatAlarm;
        this.readCheck = false;
    }
}
