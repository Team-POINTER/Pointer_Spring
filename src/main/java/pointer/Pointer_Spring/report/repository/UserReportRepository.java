package pointer.Pointer_Spring.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pointer.Pointer_Spring.report.domain.UserReport;

import java.util.List;
@Repository
public interface UserReportRepository extends JpaRepository<UserReport, Long> {

    UserReport save(UserReport userReport);
    List<UserReport> findAllByReportingUserId(Long userId);
    boolean existsByTargetUserUserIdAndReportingUserId(Long reportingUserId, Long targetUserId);
    UserReport findByTargetUserUserIdAndReportingUserId(Long userId, Long targetUserId);
}
