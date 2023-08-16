package pointer.Pointer_Spring.report.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pointer.Pointer_Spring.report.domain.UserReport;

import java.util.List;
@Repository
public interface UserReportRepository extends JpaRepository<UserReport, Long> {

    UserReport save(UserReport userReport);
    List<UserReport> findAllByReportingUserIdAndStatus(Long userId, int status);//userid == reportingUserId
    boolean existsByTargetUserUserIdAndReportingUserIdAndStatus(Long reportingUserId, Long targetUserId, int status);
    UserReport findByTargetUserUserIdAndReportingUserIdAndStatus(Long userId, Long targetUserId, int status);

    List<UserReport> findAllByTargetUserUserIdAndStatus(Long targetId, int status);
    List<UserReport> findAllByUserReportIdLessThanAndStatusEqualsOrderByUserReportIdDesc(Long lastReportId, int status, Pageable pageable);
    UserReport findFirstByOrderByUserReportIdDesc();

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM UserReport WHERE target_user_id = :userId", nativeQuery = true)
    void deleteAllByTargetUserUserId(@Param("userId") Long userId);
}
