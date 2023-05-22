package pointer.Pointer_Spring.friend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pointer.Pointer_Spring.friend.domain.Friend;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findByUserUserIdAndRelationshipAndStatus(Long userUserId, Friend.Relation relation, int status);
    List<Friend> findByUserUserIdAndStatus(Long userUserId, int status);
    Optional<Friend> findByUserUserIdAndUserFriendIdAndStatus(Long userUserId, Long userFriendId, int status);
    List<Friend> findByUserUserIdAndNotRelationshipAndStatus
            (Long userUserId, Friend.Relation relation, int status);

    Long save(Friend friend);
}
