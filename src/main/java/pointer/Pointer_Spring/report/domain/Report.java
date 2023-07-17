package pointer.Pointer_Spring.report.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pointer.Pointer_Spring.config.BaseEntity;
import pointer.Pointer_Spring.report.ReportReason;
import pointer.Pointer_Spring.room.domain.Room;
import pointer.Pointer_Spring.user.domain.User;

import javax.persistence.*;

@Getter
@Entity(name = "Report")
@RequiredArgsConstructor
public class Report extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id", unique = true)
    private Long reportId;

    @ManyToOne
    private Room room;

    private String data;
    public enum ReportType{
        QUESTION, HINT
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type")
    private ReportType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user")
    private User targetUser;

    private String reason;
    @Enumerated(EnumType.STRING)
    @Column(name = "report_code")
    private ReportReason reportCode;

    @Column(name = "reporting_user_id")
    private Long reportingUserId;


    @Builder
    public Report(Room room, String data, ReportType type, String reason, User targetUser, ReportReason reportCode, Long reportingUserId ){
        this.room = room;
        this.data = data;
        this.type = type;
        this.reason = reason;
        this.targetUser = targetUser;
        this.reportCode = reportCode;
        this.reportingUserId = reportingUserId;
    }

}
