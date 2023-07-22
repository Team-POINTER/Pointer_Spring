package pointer.Pointer_Spring.report.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import pointer.Pointer_Spring.report.ReportReason;
import pointer.Pointer_Spring.report.domain.Report;
import pointer.Pointer_Spring.report.domain.UserReport;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public class ReportDto {
    @Getter
    public static class UserReportRequest{
        private Long targetUserId;
        private Long reportingUserId;
        private String reason;
        private ReportReason reasonCode;

        //@Builder
        public UserReportRequest(Long targetUserId, Long reportingUserId, String reason, ReportReason reasonCode){
            this.targetUserId = targetUserId;
            this.reportingUserId = reportingUserId;
            this.reason = reason;
            this.reasonCode = reasonCode;
        }
    }
    @Getter
    public static class UserReportResponse{
        private Long targetUserId;
        private Long reportingUserId;
        @JsonInclude(NON_NULL)
        private String reason;
        private ReportReason reasonCode;

        @Builder
        public UserReportResponse(Long targetUserId, Long reportingUserId, String reason, ReportReason reasonCode){
            this.targetUserId = targetUserId;
            this.reportingUserId = reportingUserId;
            this.reason = reason;
            this.reasonCode = reasonCode;
        }
        public UserReportResponse(UserReport userReport){
            this.targetUserId = userReport.getTargetUser().getUserId();
            this.reportingUserId = userReport.getReportingUserId();
            this.reason = userReport.getReason();
            this.reasonCode = userReport.getReportCode();
        }

    }


    @Getter
    public static class ReportRequest{
        private Long roomId;
        private Long dataId;
        private Report.ReportType type;
        private Long targetUserId;
        private Long reportingUserId;
        private String reason;
        private ReportReason reasonCode;

        @Builder
        public ReportRequest(Long roomId, Long dataId, Report.ReportType type, Long targetUserId, Long reportingUserId, String reason, ReportReason reasonCode){
            this.roomId = roomId;
            this.dataId = dataId;
            this.type =type;
            this.targetUserId = targetUserId;
            this.reportingUserId = reportingUserId;
            this.reason = reason;
            this.reasonCode = reasonCode;
        }
    }

    @Getter
    public static class ReportResponse{
        private Long roomId;
        private String data;
        private Report.ReportType type;
        private Long targetUserId;
        private Long reportingUserId;
        @JsonInclude(NON_NULL)
        private String reason;
        private ReportReason reasonCode;

        @Builder
        public ReportResponse(Long roomId, String data, Report.ReportType type, Long targetUserId, Long reportingUserId, String reason, ReportReason reasonCode){
            this.roomId = roomId;
            this.data = data;
            this.type =type;
            this.targetUserId = targetUserId;
            this.reportingUserId = reportingUserId;
            this.reason = reason;
            this.reasonCode = reasonCode;
        }
        public ReportResponse(Report report, String data){
            this.roomId = report.getRoom().getRoomId();
            this.data = data;
            this.type =report.getType();
            this.targetUserId = report.getTargetUser().getUserId();
            this.reportingUserId = report.getReportingUserId();
            this.reason = report.getReason();
            this.reasonCode = report.getReportCode();
        }
    }

}
