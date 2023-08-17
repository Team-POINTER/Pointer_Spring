package pointer.Pointer_Spring.alarm.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import pointer.Pointer_Spring.alarm.domain.Alarm;

import java.util.List;
import java.util.Optional;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    boolean existsByReceiveUserIdAndReadCheck(Long userId, boolean readCheck);

    List<Alarm> findAllByReceiveUserIdAndIdLessThanOrderByIdDesc(Long userId, Long cursorId, PageRequest pageable);

    Optional<Alarm> findBySendUserIdAndReceiveUserIdAndType(Long userId, Long memberId, Alarm.AlarmType alarmType);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Alarm WHERE receive_user_id = :receiveUserId OR send_user_id = :sendUserId", nativeQuery = true)
    void deleteAllByReceiveUserIdOrSendUserId(@Param("receiveUserId") Long receiveUserId, @Param("sendUserId") Long sendUserId);

    List<Alarm> findAllByReceiveUserIdAndTypeAndIdLessThanOrderByIdDesc(Long userId, Alarm.AlarmType alarmType, Long cursorId, PageRequest pageable);

    Optional<Alarm> findTopBySendUserIdAndReceiveUserIdAndTypeOrderByIdDesc(Long sendUserId, Long receiveUserId, Alarm.AlarmType type);

    List<Alarm> findAllByReceiveUserIdAndTypeNotAndIdLessThanOrderByIdDesc(Long id, Alarm.AlarmType alarmType, Long cursorId, PageRequest pageable);
}
