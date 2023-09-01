package pointer.Pointer_Spring.report.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import pointer.Pointer_Spring.config.BaseEntity;
import pointer.Pointer_Spring.report.enumeration.ReportType;

import javax.persistence.*;

@Getter
@Entity(name = "RestrictedUser")
@NoArgsConstructor
@DynamicInsert
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
    private ReportType reportType;

    @Column(name = "temporal_num", columnDefinition = "integer default 3")
    private Integer temporalNum;

//    public enum ReportType{
//        QUESTION, HINT, CHAT
//    }

    public RestrictedUser(Report report, Long targetUserId, ReportType reportType, Long roomId){
        this.report = report;
        this.targetUserId = targetUserId;
        this.reportType = reportType;
        this.roomId = roomId;
    }
    public void updateTemporalNum(Integer temporalNum){
        this.temporalNum = temporalNum;
    }
}
