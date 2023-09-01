package pointer.Pointer_Spring.report.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pointer.Pointer_Spring.report.domain.BlockedUser;

import java.util.List;

@Repository
public interface BlockedUserRepository extends JpaRepository<BlockedUser, Long> {
    boolean existsByEmail(String email);
    List<BlockedUser> findAllByBlockedUserIdLessThanOrderByBlockedUserIdDesc(Long lastBlockedUser, Pageable pagable);
    BlockedUser findFirstByOrderByBlockedUserIdDesc();
}
