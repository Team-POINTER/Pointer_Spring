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
        @JsonInclude(NON_NULL)
        private Long userReportId;
        private Long targetUserId;
        private Long reportingUserId;
        @JsonInclude(NON_NULL)
        private String reason;
        private ReportReason reasonCode;

        @Builder
        public UserReportResponse(Long userReportId, Long targetUserId, Long reportingUserId, String reason, ReportReason reasonCode){
            this.userReportId = userReportId;
            this.targetUserId = targetUserId;
            this.reportingUserId = reportingUserId;
            this.reason = reason;
            this.reasonCode = reasonCode;
        }
        public UserReportResponse(UserReport userReport){
            this.userReportId = userReport.getUserReportId();
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
        public ReportRequest(Long reportId, Long roomId, Long dataId, Report.ReportType type, Long targetUserId, Long reportingUserId, String reason, ReportReason reasonCode){

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
        @JsonInclude(NON_NULL)
        private Long reportId;
        private Long roomId;
        private String data;
        private Report.ReportType type;
        private Long targetUserId;
        private Long reportingUserId;
        @JsonInclude(NON_NULL)
        private String reason;
        private ReportReason reasonCode;

        @Builder
        public ReportResponse(Long reportId, Long roomId, String data, Report.ReportType type, Long targetUserId, Long reportingUserId, String reason, ReportReason reasonCode){
            this.reportId = reportId;
            this.roomId = roomId;
            this.data = data;
            this.type =type;
            this.targetUserId = targetUserId;
            this.reportingUserId = reportingUserId;
            this.reason = reason;
            this.reasonCode = reasonCode;
        }
        public ReportResponse(Report report, String data){
            this.reportId = report.getReportId();
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
