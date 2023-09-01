package pointer.Pointer_Spring.alarm.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import pointer.Pointer_Spring.config.BaseEntity;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @Column(name = "alarm_title")
    private String title;

    @Column(updatable = false, name = "created_at", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Column(name = "status", columnDefinition = "int default 1")
    private int status = 1;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alarm_id")
    private ChatAlarm chatAlarm;

    @Column(name = "need_id")
    private Long needId;


    public enum AlarmType {
        CHAT(0, "", "채팅이 왔어요."),
        POKE(1, " 님이 당신을 꼭! 찔렀어요.","얼른 질문을 확인하고 지목해봐요."),
        FRIEND_REQUEST(2, " 님이 친구를 요청했어요.","얼른 친구 요청을 확인해보세요."),
        FRIEND_ACCEPT(3, " 님이 친구를 수락했어요.","얼른 친구 요청을 확인해보세요."),
        QUESTION(4, "룸에 질문이 생성되었습니다.","얼른 질문을 확인해보세요."),
        EVENT(5, "이벤트가 있어요.","얼른 이벤트를 확인해보세요.");

        private int code;
        private String title;
        private String message;

        AlarmType(int code, String title, String message) {
            this.code = code;
            this.title = title;
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
        public String getTitle() {return title;}
    }

    @Builder
    public Alarm(AlarmType type, Long sendUserId,
                 Long receiveUserId, String content, String title,
                 ChatAlarm chatAlarm, Long needId) {
        this.type = type;
        this.sendUserId = sendUserId;
        this.receiveUserId = receiveUserId;
        this.needId = needId;
        this.content = content;
        this.title = title;
        this.chatAlarm = chatAlarm;
        this.readCheck = false;
        this.createdAt = LocalDateTime.now();
    }
}
