package pointer.Pointer_Spring.vote.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pointer.Pointer_Spring.question.domain.Question;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.vote.domain.VoteHistory;

import java.util.List;

public interface VoteRepository extends JpaRepository<VoteHistory, Long> {

    //@Query("SELECT COUNT(DISTINCT vh.user.userId) FROM VoteHistory vh WHERE vh.question = :question")
    int countDistinctUsersByQuestion(Question question);

    boolean existsByUserUserIdAndQuestion(Long userId, Question currentQuestion);

    int countByQuestion(Question question);

    int countByCandidate(User user);

    boolean existsByUser(User member);

    int countByQuestionAndCandidate(Question question, User member);

    List<VoteHistory> findAllByQuestionAndCandidate(Question question, User user);
}
