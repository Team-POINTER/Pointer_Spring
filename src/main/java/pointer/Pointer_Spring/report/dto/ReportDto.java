package pointer.Pointer_Spring.report.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import pointer.Pointer_Spring.report.enumeration.ReportReason;
import pointer.Pointer_Spring.report.domain.BlockedUser;
import pointer.Pointer_Spring.report.domain.Report;
import pointer.Pointer_Spring.report.domain.RestrictedUser;
import pointer.Pointer_Spring.report.domain.UserReport;
import pointer.Pointer_Spring.report.enumeration.ReportType;

import javax.persistence.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

public class ReportDto {
    @Getter
    public static class UserReportRequest{
        private Long targetUserId;
        private String reason;
        private ReportReason reasonCode;

        //@Builder
        public UserReportRequest(Long targetUserId, String reason, ReportReason reasonCode){
            this.targetUserId = targetUserId;
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
        private ReportType type;
        private Long targetUserId;
        private String reason;
        private ReportReason reasonCode;

        @Builder
        public ReportRequest(Long reportId, Long roomId, Long dataId, ReportType type, Long targetUserId, Long reportingUserId, String reason, ReportReason reasonCode){

            this.roomId = roomId;
            this.dataId = dataId;
            this.type =type;
            this.targetUserId = targetUserId;
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
        private ReportType type;
        private Long targetUserId;
        private Long reportingUserId;
        @JsonInclude(NON_NULL)
        private String reason;
        private ReportReason reasonCode;

        @Builder
        public ReportResponse(Long reportId, Long roomId, String data, ReportType type, Long targetUserId, Long reportingUserId, String reason, ReportReason reasonCode){
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
    @Getter
    public static class BlockedUserResponse{
        private Long blockedUserId;
        private String email;
        private String id;
        public BlockedUserResponse(BlockedUser blockedUser){
            this.blockedUserId = blockedUser.getBlockedUserId();
            this.email = blockedUser.getEmail();
            this.id = blockedUser.getId();
        }
    }
    @Getter
    public static class RestrictedUserResponse{
        private Long restrictedUserId;
        private Long reportId;
        private Long targetUserId;
        private Long roomId;
        @Enumerated(EnumType.STRING)
        private ReportType reportType;
        private Integer temporalNum;

        public RestrictedUserResponse(RestrictedUser restrictedUser){
            this.restrictedUserId = restrictedUser.getRestrictedUserId();
            this.reportId = restrictedUser.getReport().getReportId();
            this.targetUserId = restrictedUser.getTargetUserId();
            this.roomId = restrictedUser.getRoomId();
            this.reportType = restrictedUser.getReportType();
            this.temporalNum = restrictedUser.getTemporalNum();
        }
    }

}
