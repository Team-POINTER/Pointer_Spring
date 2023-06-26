package pointer.Pointer_Spring.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pointer.Pointer_Spring.question.domain.Question;
import pointer.Pointer_Spring.room.domain.Room;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findTopByOrderByIdDesc();

    Optional<Question> findByCreatedAtAfter(LocalDateTime now);

    List<Question> findAllByRoomOrderByCreatedAtDesc(Room room);
    List<Question> findAllByQuestionContaining(String kwd);
}
