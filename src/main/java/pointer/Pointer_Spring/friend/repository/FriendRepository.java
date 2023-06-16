package pointer.Pointer_Spring.friend.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Repository;
import pointer.Pointer_Spring.friend.domain.Friend;
import pointer.Pointer_Spring.user.domain.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findAllByUserUserIdAndRelationshipAndStatus(Long userUserId, Friend.Relation relation, int status,  Pageable pageable);
    List<Friend> findAllByUserUserIdAndRelationshipAndStatusAndFriendNameContaining(Long userUserId, Friend.Relation relation, int status, String kwd,  PageRequest pageRequest);
    List<Friend> findAllByFriendNameAfterAndUserUserIdAndRelationshipAndStatusAndFriendNameContainingOrderByFriendNameAsc(Long userFriendId, String friendName, Long userUserId, Friend.Relation relation, int stauts, String kwd, Pageable pageable);
    List<Friend> findByUserUserIdAndStatus(Long userUserId, int status);
    Optional<Friend> findByUserUserIdAndUserFriendIdAndStatus(Long userUserId, Long userFriendId, int status);
    Friend save(Friend friend);
    Optional<Friend> findByFriendName(String friendNm);
}