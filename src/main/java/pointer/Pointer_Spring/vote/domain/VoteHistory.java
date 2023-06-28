package pointer.Pointer_Spring.vote.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pointer.Pointer_Spring.config.BaseEntity;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "VotingHistory")
public class VoteHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_history_id", unique = true)
    private Long voteHistoryId;

    //@ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "room_id")
    private Long roomId;

    //@ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "member_id")
    private Long memberId;

    //@ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "question_id")
    private Long questionId;

    //@ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "candidate_id")
    private Long candidateId;

    @Column(name = "candidate_name")
    private String candidateName;

    @Column(name = "hint")
    private String hint;

//    @Column(name = "status")
//    @Enumerated(EnumType.ORDINAL)
//    private VoteStatus status;

    @Builder
    public VoteHistory(Long roomId, Long memberId, Long questionId, Long candidateId, String candidateName, String hint) {
        this.roomId = roomId;
        this.memberId = memberId;
        this.questionId = questionId;
        this.candidateId = candidateId;
        this.candidateName = candidateName;
        this.hint = hint;
    }

}
