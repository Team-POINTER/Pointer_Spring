package pointer.Pointer_Spring.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import pointer.Pointer_Spring.question.domain.Question;
import pointer.Pointer_Spring.room.domain.Room;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findTopByRoomRoomIdOrderByIdDesc(Long roomId);

    Optional<Question> findByCreatedAtAfterAndRoomRoomId(LocalDateTime now, Long roomId);

    List<Question> findAllByRoomAndStatusOrderByCreatedAtDesc(Room room, int status);
    List<Question> findAllByQuestionContainingAndStatus(String kwd, int status);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM Question WHERE room_room_id = :roomId", nativeQuery = true)
    void deleteAllByRoomId(Long roomId);
}
