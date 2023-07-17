package pointer.Pointer_Spring.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pointer.Pointer_Spring.report.domain.Report;
import pointer.Pointer_Spring.report.domain.UserReport;

import java.util.List;

@Repository
public interface ReportRepository  extends JpaRepository<Report, Long> {
    List<Report> findAllByReportingUserId(Long userId);
    boolean existsByTargetUserUserIdAndReportingUserId(Long reportingUserId, Long targetUserId);
    Report findByTargetUserUserIdAndReportingUserId(Long userId, Long targetUserId);
}
