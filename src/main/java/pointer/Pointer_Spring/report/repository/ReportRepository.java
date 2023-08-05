package pointer.Pointer_Spring.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pointer.Pointer_Spring.report.domain.Report;

import java.util.List;

@Repository
public interface ReportRepository  extends JpaRepository<Report, Long> {
    List<Report> findAllByReportingUserId(Long userId);
    boolean existsByTargetUserUserIdAndReportingUserId(Long reportingUserId, Long targetUserId);
    Report findByTargetUserUserIdAndReportingUserId(Long userId, Long targetUserId);
    List<Report> findAllByTargetUserUserId(Long targetUserId);
    boolean existsByReportingUserIdAndTargetUserUserIdAndRoomRoomIdAndAndTypeAndDataId(Long reportingUserId ,Long targetUserId, Long roomId, Report.ReportType reportType, Long dataId);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Report WHERE room_room_id = :roomRoomId", nativeQuery = true)
    void deleteAllByRoomRoomId(Long roomRoomId);
}
