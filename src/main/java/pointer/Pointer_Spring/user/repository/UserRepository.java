package pointer.Pointer_Spring.user.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import pointer.Pointer_Spring.friend.domain.Friend;
import pointer.Pointer_Spring.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndStatus(String email, int status);
    Optional<User> findByIdAndStatus(String id, int status);
    Optional<User> findByUserIdAndStatus(Long userId, int status);
    Optional<User> findByTokenAndStatus(String token, int status);
    boolean existsByEmailAndStatus(String email, int status);

    // pageable
    List<User> findAllByIdContainingOrNameContainingAndStatusOrderByUserIdDesc
    (String id, String name, int status, Pageable pageable);

    List<User> findAllByUserIdLessThanAndIdContainingOrNameContainingAndStatusOrderByUserIdDesc
            (Long userId, String id, String name, int status, Pageable pageable);
}