package pointer.Pointer_Spring.room.domain;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import pointer.Pointer_Spring.config.BaseEntity;
import pointer.Pointer_Spring.question.domain.Question;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "Room")
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id", unique = true)
    private Long roomId;
    @Column(name = "name", nullable = false)
    private String name; //default room name
    @Column(name = "member_num")
    private Integer memberNum;//룸 안 멤버
    @Column(name = "voting_num")
    private Integer votingNum;
    private String code;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "writer_id")
//    @JoinColumn(name = "user_id")
    @Column(name = "creator_id", unique = true)//연관관계 맵핑 안 함
    private Long creatorId;


    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Question> questions = new ArrayList<>();
    public void updateMemberNum(Integer memberNum){
        this.memberNum = memberNum;
    }


    public void addQuestion(Question question) {
        this.questions.add(question);
    }

    @Builder
    public Room(Long creatorId, String name) {
        this.creatorId = creatorId;
        this.name = name;
        this.memberNum = 1;
        this.votingNum = 0;
    }


}
