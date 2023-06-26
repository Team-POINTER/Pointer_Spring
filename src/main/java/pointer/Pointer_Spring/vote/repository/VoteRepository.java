package pointer.Pointer_Spring.vote.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pointer.Pointer_Spring.question.domain.Question;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.vote.domain.VoteHistory;

import java.util.List;

public interface VoteRepository extends JpaRepository<VoteHistory, Long> {

    @Query(value = "select count(distinct member_id) from VotingHistory where question_id = ?1", nativeQuery = true)
    int countDistinctUserByQuestion(Question question);

    boolean existsByUserUserIdAndQuestion(Long userId, Question currentQuestion);

    int countByQuestion(Question question);

    int countByCandidateAndQuestion(User user, Question question);

    boolean existsByUser(User member);

    int countByQuestionAndCandidate(Question question, User member);

    List<VoteHistory> findAllByQuestionAndCandidate(Question question, User user);
}
