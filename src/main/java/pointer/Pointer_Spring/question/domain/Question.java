package pointer.Pointer_Spring.question.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pointer.Pointer_Spring.config.BaseEntity;
import pointer.Pointer_Spring.room.domain.Room;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "Question")
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id", unique = true)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Room_room_id")
    private Room room;


//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "creater_id")
//    private User user;
    @Column(name = "creater_id", unique = true)//연관관계 맵핑 안 함
    private Long creatorId;

    private String question;

    @Builder
    public Question(Room room, Long creatorId, String question) {
        this.room = room;
        this.creatorId = creatorId;
        this.question = question;
    }

    public void modify(String content) {
        this.question = content;
    }
}
