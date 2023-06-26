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
    private Long questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Room_room_id")
    private Room room;

    @Column(name = "creater_id", unique = true)//연관관계 맵핑 안 함
    private Long creatorId;

    private String question;

    @Builder
    public Question(Room room, String question, Long creatorId) {
        this.room = room;
        this.question = question;
        this.creatorId = creatorId;
    }

}
