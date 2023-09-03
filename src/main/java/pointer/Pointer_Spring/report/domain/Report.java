package pointer.Pointer_Spring.report.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import pointer.Pointer_Spring.config.BaseEntity;
import pointer.Pointer_Spring.report.enumeration.ReportReason;
import pointer.Pointer_Spring.report.enumeration.ReportType;
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
    @JoinColumn(name = "Room_room_id")
    private Room room;

    @Column(name = "data_id")
    private Long dataId;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type")
    private ReportType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id")
    private User targetUser;

    private String reason;
    @Enumerated(EnumType.STRING)
    @Column(name = "report_code")
    private ReportReason reportCode;

    @Column(name = "reporting_user_id")
    private Long reportingUserId;


    @Builder
    public Report(Room room, Long dataId, ReportType type, String reason, User targetUser, ReportReason reportCode, Long reportingUserId ){
        this.room = room;
        this.dataId = dataId;
        this.type = type;
        this.reason = reason;
        this.targetUser = targetUser;
        this.reportCode = reportCode;
        this.reportingUserId = reportingUserId;
    }

}
