package pointer.Pointer_Spring.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pointer.Pointer_Spring.report.domain.Report;
import pointer.Pointer_Spring.report.domain.RestrictedUser;

@Repository
public interface RestrictedUserRepository extends JpaRepository<RestrictedUser, Long> {
    RestrictedUser findByReportTargetUserUserIdAndReportRoomRoomIdAndReportTypeAndStatus(Long userId, Long roomId, Report.ReportType reportType, int status);
    boolean existsByTargetUserIdAndRoomIdAndReportTypeAndStatus(Long userId, Long roomId, Report.ReportType reportType, int status);

}
