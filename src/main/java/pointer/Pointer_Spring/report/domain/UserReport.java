package pointer.Pointer_Spring.report.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pointer.Pointer_Spring.config.BaseEntity;
import pointer.Pointer_Spring.report.ReportReason;
import pointer.Pointer_Spring.report.dto.ReportDto;
import pointer.Pointer_Spring.user.domain.User;

import javax.persistence.*;

@Getter
@Entity(name = "UserReport")
@RequiredArgsConstructor
public class UserReport extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_report_id", unique = true)
    private Long userReportId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private User targetUser;
    private String reason;
    @Column(name = "report_code")
    private ReportReason reportCode;


    @Column(name = "reporting_user_id")
    private Long reportingUserId;


    @Builder
    public UserReport(User targetUser, Long reportingUserId, String reason, ReportReason reportCode){
        this.targetUser = targetUser;
        this.reportingUserId = reportingUserId;
        this.reason = reason;
        this.reportCode = reportCode;
    }
}
