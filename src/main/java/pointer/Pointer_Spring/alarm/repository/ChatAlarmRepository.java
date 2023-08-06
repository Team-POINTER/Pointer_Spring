package pointer.Pointer_Spring.alarm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pointer.Pointer_Spring.alarm.domain.ChatAlarm;

import java.util.List;

public interface ChatAlarmRepository extends JpaRepository<ChatAlarm, Long> {
    List<ChatAlarm> findAllBySendUserIdAndReadCheckAndStatus(Long userId, boolean b, int status);
}
