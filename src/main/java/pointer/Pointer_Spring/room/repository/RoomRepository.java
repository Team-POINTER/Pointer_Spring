package pointer.Pointer_Spring.room.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pointer.Pointer_Spring.room.domain.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

}
