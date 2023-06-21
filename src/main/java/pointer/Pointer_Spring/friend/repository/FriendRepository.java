package pointer.Pointer_Spring.friend.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pointer.Pointer_Spring.friend.domain.Friend;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findAllByUserUserIdAndRelationshipAndStatus(Long userUserId, Friend.Relation relation, int status);
    List<Friend> findAllByUserUserIdAndRelationshipNotAndStatus(Long userUserId, Friend.Relation relation, int status);

    // pageable
    List<Friend> findAllByUserUserIdAndRelationshipNotAndStatus(Long userUserId, Friend.Relation relation, int status, Pageable pageable);
    List<Friend> findAllByUserUserIdAndRelationshipAndStatus(Long userUserId, Friend.Relation relation, int status, Pageable pageable);
    Long countByUserUserIdAndRelationshipNotAndStatus(Long userUserId, Friend.Relation relation, int status);
    Long countByUserUserIdAndRelationshipAndStatus(Long userUserId, Friend.Relation relation, int status);

    List<Friend> findAllByUserUserIdAndStatus(Long userUserId, int status);
    Optional<Friend> findByUserUserIdAndUserFriendIdAndStatus(Long userUserId, Long userFriendId, int status);
    Optional<Friend> findByUserUserIdAndUserFriendIdAndRelationshipNotAndStatus(Long userUserId, Long userFriendId, Friend.Relation relation, int status);
    Friend save(Friend friend);

}
