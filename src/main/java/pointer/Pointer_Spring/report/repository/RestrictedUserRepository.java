package pointer.Pointer_Spring.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pointer.Pointer_Spring.report.domain.Report;
import pointer.Pointer_Spring.report.domain.RestrictedUser;

@Repository
public interface RestrictedUserRepository extends JpaRepository<RestrictedUser, Long> {
    RestrictedUser findByReportTargetUserUserIdAndReportRoomRoomIdAndReportType(Long userId, Long roomId, Report.ReportType reportType);
    boolean existsByTargetUserIdAndRoomIdAndReportType(Long userId, Long roomId, Report.ReportType reportType);

}
