package pointer.Pointer_Spring.user.repository;

import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pointer.Pointer_Spring.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User save(User user);
    Optional<User> findByEmailAndStatus(String email, int status); // email 중복 없게
    Optional<User> findByEmailAndTypeAndStatus(String email, User.SignupType type, int status);
    Optional<User> findByIdAndStatus(String id, int status);
    Optional<User> findByUserIdAndStatus(Long userId, int status);
    Optional<User> findByUserIdAndTokenExpiredAndStatus(Long userId, boolean tokenToken, int status);
    Optional<User> findByTokenAndStatus(String token, int status);
    boolean existsByEmailAndTypeAndStatus(String email, User.SignupType type, int status);

    Optional<User> findByUserId(Long userId);
    boolean existsById(String id);
    boolean existsByUserIdAndStatus(Long userId, int status);

    List<User> findAllByEventAlarmFlag(boolean b);

    // 유저 검색 (제외: 본인, 차단 친구)
    @Query("SELECT u FROM User u " +
            "WHERE u.userId NOT IN (SELECT f.userFriendId FROM Friend f WHERE f.user.userId = :userId AND f.relationship = 0 AND f.status = :status) " +
            "AND NOT u.userId = :userId AND (u.id LIKE %:keyword% OR u.name LIKE %:keyword%) AND u.status = :status " +
            "ORDER BY u.name")
    List<User> findUsersWithKeywordAndStatusNotFriendOfUser(@Param("userId")Long userId,
                                                            @Param("keyword")String keyword,
                                                            @Param("status")int status,
                                                            Pageable pageRequest);
    @Query("SELECT COUNT(u) FROM User u " +
            "WHERE u.userId NOT IN (SELECT f.userFriendId FROM Friend f WHERE f.user.userId = :userId AND f.relationship = 0 AND f.status = :status) " +
            "AND NOT u.userId = :userId AND (u.id LIKE %:keyword% OR u.name LIKE %:keyword%) AND u.status = :status ")
    Long countUsersWithKeywordAndStatusNotFriendOfUser(@Param("userId")Long userId,
                                                       @Param("keyword")String keyword,
                                                       @Param("status")int status);
}