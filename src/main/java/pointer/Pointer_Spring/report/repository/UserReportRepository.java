package pointer.Pointer_Spring.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pointer.Pointer_Spring.report.domain.UserReport;

import java.util.List;
@Repository
public interface UserReportRepository extends JpaRepository<UserReport, Long> {

    UserReport save(UserReport userReport);
    List<UserReport> findAllByReportingUserIdAndStatus(Long userId, int status);//userid == reportingUserId
    boolean existsByTargetUserUserIdAndReportingUserIdAndStatus(Long reportingUserId, Long targetUserId, int status);
    UserReport findByTargetUserUserIdAndReportingUserIdAndStatus(Long userId, Long targetUserId, int status);

    List<UserReport> findAllByTargetUserUserIdAndStatus(Long targetId, int status);
}
