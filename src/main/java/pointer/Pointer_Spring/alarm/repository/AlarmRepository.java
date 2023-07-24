package pointer.Pointer_Spring.alarm.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import pointer.Pointer_Spring.alarm.domain.Alarm;

import java.util.List;
import java.util.Optional;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    boolean existsByReceiveUserIdAndReadCheck(Long userId, boolean readCheck);

    List<Alarm> findAllByReceiveUserIdAndIdLessThanOrderByIdDesc(Long userId, Long cursorId, PageRequest pageable);

    Optional<Alarm> findBySendUserIdAndReceiveUserIdAndType(Long userId, Long memberId, Alarm.AlarmType alarmType);
}
