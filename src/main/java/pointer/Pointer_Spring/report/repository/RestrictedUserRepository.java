package pointer.Pointer_Spring.report.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pointer.Pointer_Spring.report.domain.Report;
import pointer.Pointer_Spring.report.domain.RestrictedUser;
import pointer.Pointer_Spring.report.enumeration.ReportType;

import java.util.List;

@Repository
public interface RestrictedUserRepository extends JpaRepository<RestrictedUser, Long> {
    RestrictedUser findByReportTargetUserUserIdAndReportRoomRoomIdAndReportTypeAndStatus(Long userId, Long roomId, ReportType reportType, int status);
    boolean existsByTargetUserIdAndRoomIdAndReportTypeAndStatus(Long userId, Long roomId, ReportType reportType, int status);
    List<RestrictedUser> findAllByRestrictedUserIdLessThanAndReportTypeAndStatusOrderByRestrictedUserIdDesc(Long lastRestrictedUserId, ReportType reportType, int status, Pageable pagable);
    RestrictedUser findFirstByReportTypeOrderByRestrictedUserIdDesc(ReportType reportType);
}
