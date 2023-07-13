package pointer.Pointer_Spring.alarm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pointer.Pointer_Spring.alarm.domain.Alarm;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
}
