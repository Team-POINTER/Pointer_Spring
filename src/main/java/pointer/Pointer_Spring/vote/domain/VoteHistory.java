package pointer.Pointer_Spring.vote.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pointer.Pointer_Spring.config.BaseEntity;
import pointer.Pointer_Spring.question.domain.Question;
import pointer.Pointer_Spring.room.domain.Room;
import pointer.Pointer_Spring.user.domain.User;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "VotingHistory")
public class VoteHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_history_id", unique = true)
    private Long voteHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id")
    private User candidate;

    @Column(name = "candidate_name")
    private String candidateName;

    @Column(name = "hint")
    private String hint;

//    @Column(name = "status")
//    @Enumerated(EnumType.ORDINAL)
//    private VoteStatus status;

    @Builder
    public VoteHistory(Room room, User user, User candidate, Question question, String candidateName, String hint) {
        this.room = room;
        this.user = user;
        this.candidate = candidate;
        this.candidateName = candidateName;
        this.hint = hint;
        this.question = question;
    }

}
