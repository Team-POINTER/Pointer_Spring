package pointer.Pointer_Spring.friend.repository;


import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pointer.Pointer_Spring.friend.domain.Friend;

import org.springframework.data.domain.PageRequest;



import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {

    // pageable
    List<Friend> findAllByUserUserIdAndRelationshipNotAndStatus(Long userUserId, Friend.Relation relation, int status, Pageable pageable);
    List<Friend> findAllByUserUserIdAndRelationshipAndStatus(Long userUserId, Friend.Relation relation, int status, Pageable pageable);

    Optional<Friend> findByUserUserIdAndUserFriendIdAndStatus(Long userUserId, Long userFriendId, int status);
    Optional<Friend> findByUserUserIdAndUserFriendIdAndRelationshipNotAndStatus(Long userUserId, Long userFriendId, Friend.Relation relation, int status);
    Friend save(Friend friend);

    List<Friend> findAllByUserUserIdAndRelationshipAndStatusAndFriendNameContaining(Long userUserId, Friend.Relation relation, int status, String kwd,  PageRequest pageRequest);
    List<Friend> findAllByFriendNameAfterAndUserUserIdAndRelationshipAndStatusAndFriendNameContainingOrderByFriendNameAsc(Long userFriendId, String friendName, Long userUserId, Friend.Relation relation, int status, String kwd, Pageable pageable);

    List<Friend> findByUserUserIdOrUserFriendIdAndStatus(Long userUserId, Long userFriendId, int status);

    // 친구 검색 (제외: 차단 친구)
    @Query("SELECT f FROM Friend f JOIN f.user u " +
            "WHERE f.userFriendId IN (SELECT u.userId FROM User u " +
            "WHERE (u.id LIKE %:keyword% OR u.name LIKE %:keyword%) AND u.status = :status) " +
            "AND f.relationship = 3 AND f.user.userId = :userUserId AND f.status = :status " +
            "ORDER BY u.name")
    List<Friend> findUsersAndFriends(@Param("userUserId") Long userUserId,
                                     @Param("keyword") String keyword,
                                     @Param("status") int status,
                                     PageRequest pageRequest);

    @Query("SELECT COUNT(f) FROM Friend f JOIN f.user u " +
            "WHERE f.userFriendId IN (SELECT u.userId FROM User u " +
            "WHERE (u.id LIKE %:keyword% OR u.name LIKE %:keyword%) AND u.status = :status) " +
            "AND f.relationship = 3 AND f.user.userId = :userUserId AND f.status = :status" )
    Long countUsersByFriendCriteria(@Param("userUserId") Long userUserId,
                                    @Param("keyword") String keyword,
                                    @Param("status") int status);

    // 상대 친구 조회
    @Query("SELECT f FROM Friend f JOIN f.user u " +
            "WHERE NOT f.relationship = 0 AND f.user.userId = :userUserId AND f.status = :status " +
            "ORDER BY u.name")
    List<Friend> findFriendUsersAndFriends(@Param("userUserId") Long userUserId,
                                           @Param("status") int status,
                                           PageRequest pageRequest);

    @Query("SELECT COUNT(f) FROM Friend f JOIN f.user u " +
            "WHERE u.status = :status AND NOT f.relationship = 0 AND f.user.userId = :userUserId AND f.status = :status" )
    Long countFriendUsersByFriendCriteria(@Param("userUserId") Long userUserId,
                                          @Param("status") int status);


    // 차단 친구 검색 : 업데이트 순
    @Query("SELECT f FROM Friend f JOIN f.user u " +
            "WHERE f.userFriendId IN (SELECT u.userId FROM User u " +
            "WHERE (u.id LIKE %:keyword% OR u.name LIKE %:keyword%) AND u.status = :status) " +
            "AND f.relationship = 0 AND f.user.userId = :userUserId AND f.status = :status " +
            "ORDER BY f.updatedAt DESC")
    List<Friend> findUsersAndBlockFriends(@Param("userUserId") Long userUserId,
                                          @Param("keyword") String keyword,
                                          @Param("status") int status,
                                          PageRequest pageRequest);

    @Query("SELECT COUNT(f) FROM Friend f JOIN f.user u " +
            "WHERE f.userFriendId IN (SELECT u.userId FROM User u " +
            "WHERE (u.id LIKE %:keyword% OR u.name LIKE %:keyword%) AND u.status = :status) " +
            "AND f.relationship = 0 AND f.user.userId = :userUserId AND f.status = :status ")
    Long countUsersByBlockFriendCriteria(@Param("userUserId") Long userUserId,
                                         @Param("keyword") String keyword,
                                         @Param("status") int status);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Friend WHERE user_friend_id = :userFriendId OR user_user_id = :userUserId", nativeQuery = true)
    void deleteAllByUserFriendIdOrUserUserId(Long userFriendId, Long userUserId);

}
