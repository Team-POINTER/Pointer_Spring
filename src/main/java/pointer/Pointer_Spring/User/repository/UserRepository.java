package pointer.Pointer_Spring.User.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pointer.Pointer_Spring.User.domain.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailAndTypeAndStatusEquals(String email, User.Type type, int status);
    User save(User user);
}
