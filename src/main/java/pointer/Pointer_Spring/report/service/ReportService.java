package pointer.Pointer_Spring.report.service;

import pointer.Pointer_Spring.report.domain.Report;
import pointer.Pointer_Spring.report.domain.UserReport;
import pointer.Pointer_Spring.report.dto.ReportDto;

import java.util.List;

public interface ReportService {
    ReportDto.UserReportResponse saveUserReport(ReportDto.UserReportRequest reportRequest);
    List<UserReport> getUserReports(Long userId);
    UserReport getUserReport(Long userId, Long targetUserId);

    ReportDto.ReportResponse saveReport(ReportDto.ReportRequest reportRequest);
    List<Report> getReports(Long userId);
    Report getReport(Long userId, Long targetUserId);
}
