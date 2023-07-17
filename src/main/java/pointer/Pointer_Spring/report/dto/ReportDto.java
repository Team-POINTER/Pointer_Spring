package pointer.Pointer_Spring.report.dto;

import lombok.Builder;
import lombok.Getter;
import pointer.Pointer_Spring.report.ReportReason;
import pointer.Pointer_Spring.report.domain.Report;

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
        private String reason;
        private ReportReason reasonCode;

        @Builder
        public UserReportResponse(Long targetUserId, Long reportingUserId, String reason, ReportReason reasonCode){
            this.targetUserId = targetUserId;
            this.reportingUserId = reportingUserId;
            this.reason = reason;
            this.reasonCode = reasonCode;
        }
    }


    @Getter
    public static class ReportRequest{
        private Long roomId;
        private String data;
        private Report.ReportType type;
        private Long targetUserId;
        private Long reportingUserId;
        private String reason;
        private ReportReason reasonCode;

        @Builder
        public ReportRequest(Long roomId, String data, Report.ReportType type, Long targetUserId, Long reportingUserId, String reason, ReportReason reasonCode){
            this.roomId = roomId;
            this.data = data;
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
    }

}
