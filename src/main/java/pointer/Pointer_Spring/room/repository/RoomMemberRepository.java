package pointer.Pointer_Spring.room.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pointer.Pointer_Spring.User.domain.User;
import pointer.Pointer_Spring.room.domain.Room;
import pointer.Pointer_Spring.room.domain.RoomMember;

@Repository
public interface RoomMemberRepository extends JpaRepository<RoomMember, Long> {

    List<RoomMember> findAllByRoom(Room room);
    @Query(value = "select rm from RoomMember rm where RoomMember.user.id = :id", nativeQuery = true)
    Optional<RoomMember> findByUserId(@Param("id") String Id);
    Optional<RoomMember> findByRoom_RoomIdAndUser_UserIdAndStatus(Long roomId, Long userId, int status);
    Optional<RoomMember> findByRoomAndUserAndStatus(Room room, User user, int status);


}
