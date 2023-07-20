package pointer.Pointer_Spring.report.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import pointer.Pointer_Spring.config.BaseEntity;

import javax.persistence.*;

@Getter
@Entity(name = "RestrictedUser")
@NoArgsConstructor
public class RestrictedUser extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "restricted_user_id", unique = true)
    private Long restrictedUserId;

    @ManyToOne
    @JoinColumn(name = "report_id")
    private Report report;
    @Column(name = "target_user_id", unique = true)
    private Long targetUserId;
    @Column(name = "room_id")
    private Long roomId;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type")
    private Report.ReportType reportType;

    @Column(name = "temporal_num", columnDefinition = "Integer default 3")
    private Integer temporalNum;
    @Column(name = "current_question_id")
    private Long currentQuestionId;

//    public enum ReportType{
//        QUESTION, HINT, CHAT
//    }

    public RestrictedUser(Report report, Long targetUserId, Report.ReportType reportType, Long roomId, Long currentQuestionId){
        this.report = report;
        this.targetUserId = targetUserId;
        this.reportType = reportType;
        this.roomId = roomId;
        this.currentQuestionId = currentQuestionId;
    }
    public void updateTemporalNum(Integer temporalNum){
        this.temporalNum = temporalNum;
    }
    public void updateCurrentQuestionId(Long currentQuestionId){
        this.currentQuestionId = currentQuestionId;
    }
}
