package pointer.Pointer_Spring.alarm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pointer.Pointer_Spring.alarm.domain.ActiveAlarm;

public interface ActiveAlarmRepository extends JpaRepository<ActiveAlarm, Long> {
}
