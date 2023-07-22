package pointer.Pointer_Spring.room.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.room.domain.Room;
import pointer.Pointer_Spring.room.domain.RoomMember;

@Repository
public interface RoomMemberRepository extends JpaRepository<RoomMember, Long> {

    List<RoomMember> findAllByRoom(Room room);

    List<RoomMember> findByRoomMemberId(Long RoomMemberId);
    int countByRoom(Room room);
    Optional<RoomMember> findByRoom_RoomIdAndUser_UserIdAndStatus(Long roomId, Long userId, int status);

    Boolean existsByUserUserIdAndRoomRoomId(Long userId, Long roomId);
    Boolean existsByUserUserIdAndRoomRoomIdAndStatusEquals(Long userId, Long roomId, int status);
    List<RoomMember> findAllByRoom_RoomIdAndStatusEquals(Long roomId, int status);
    //List<RoomMember> findAllByUser_UserIdAndRoom_StatusEqualsAndPrivateRoomNmContaining(Long roomId, int status, String kwd);
    List<RoomMember> findAllByUserUserIdAndPrivateRoomNmContainingAndRoom_StatusEqualsOrderByRoom_UpdatedAtAsc(Long userId, String kwd, int status);
    List<RoomMember> findAllByUserUserIdAndRoom_StatusEqualsOrderByRoom_UpdatedAtAsc(Long userId, int status);
    List<RoomMember> findAllByRoomAndUserIsQuestionRestrictedEquals(Room room, boolean isQuestionRestricted);
    List<RoomMember> findAllByRoomAndUserIsHintRestrictedEquals(Room room, boolean isHintRestricted);


}
