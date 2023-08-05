package pointer.Pointer_Spring.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pointer.Pointer_Spring.report.domain.UserReport;

import java.util.List;
@Repository
public interface UserReportRepository extends JpaRepository<UserReport, Long> {

    UserReport save(UserReport userReport);
    List<UserReport> findAllByReportingUserId(Long userId);//userid == reportingUserId
    boolean existsByTargetUserUserIdAndReportingUserId(Long reportingUserId, Long targetUserId);
    UserReport findByTargetUserUserIdAndReportingUserId(Long userId, Long targetUserId);

    List<UserReport> findAllByTargetUserUserId(Long targetId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM UserReport WHERE target_user_id = :userId", nativeQuery = true)
    void deleteAllByTargetUserUserId(Long userId);
}
