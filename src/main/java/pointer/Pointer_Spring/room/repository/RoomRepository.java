package pointer.Pointer_Spring.room.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pointer.Pointer_Spring.room.domain.Room;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
//    Optional<Room> findByInvitation(String invitation);
//    List<Room> findAllByAndNameContainingAndStatusEquals(String kwd, int status);
//    List<Room> findAllByStatusEquals();

    List<Room> findBymemberNumAndStatus(int memberNum, int status);
}
