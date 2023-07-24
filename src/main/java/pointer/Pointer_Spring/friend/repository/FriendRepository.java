package pointer.Pointer_Spring.friend.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pointer.Pointer_Spring.friend.domain.Friend;

import org.springframework.data.domain.PageRequest;



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

    Optional<Friend> findByUserUserIdAndUserFriendIdAndStatus(Long userUserId, Long userFriendId, int status);
    Optional<Friend> findByUserUserIdAndUserFriendIdAndRelationshipNotAndStatus(Long userUserId, Long userFriendId, Friend.Relation relation, int status);
    Friend save(Friend friend);

    List<Friend> findAllByUserUserIdAndRelationshipAndStatusAndFriendNameContaining(Long userUserId, Friend.Relation relation, int status, String kwd,  PageRequest pageRequest);
    List<Friend> findAllByFriendNameAfterAndUserUserIdAndRelationshipAndStatusAndFriendNameContainingOrderByFriendNameAsc(Long userFriendId, String friendName, Long userUserId, Friend.Relation relation, int stauts, String kwd, Pageable pageable);

    List<Friend> findByUserUserIdOrUserFriendIdAndStatus(Long userUserId, Long userFriendId, int status);
    Optional<Friend> findByUserUserIdAndUserFriendId(Long userId, Long userFriendId);

    List<Friend> findAllByUserUserIdAndFriendUserIdContainingOrFriendNameContainingAndRelationshipNotAndStatusOrderByUserIdDesc(Long userUserId, String friendUserId, String friendName, Friend.Relation relation, int status, PageRequest pageRequest);
    List<Friend> findAllByUserUserIdAndFriendUserIdContainingOrFriendNameContainingAndRelationshipAndStatusOrderByUserIdDesc(Long userUserId, String friendUserId, String friendName, Friend.Relation relation, int status, PageRequest pageRequest);

}
