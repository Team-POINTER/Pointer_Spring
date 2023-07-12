package pointer.Pointer_Spring.vote.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pointer.Pointer_Spring.question.domain.Question;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.vote.domain.VoteHistory;

import java.util.List;

public interface VoteRepository extends JpaRepository<VoteHistory, Long> {

    @Query(value = "select count(distinct member_id) from VotingHistory where question_id = ?1", nativeQuery = true)
    int countDistinctUserByQuestion(Long questionId);

    boolean existsByMemberIdAndQuestionId(Long userId, Long currentQuestionId);

    int countByQuestionId(Long questionId);

    int countByCandidateIdAndQuestionId(Long userId, Long questionId);

    boolean existsByMemberId(Long memberId);

    int countByQuestionIdAndCandidateId(Long questionId, Long userId);

    List<VoteHistory> findAllByQuestionIdAndCandidateId(Long questionId, Long userId);

    VoteHistory findTopByQuestionIdAndCandidateIdOrderByUpdatedAtDesc(Long questionId, Long userId);
    VoteHistory findAllByRoomId(Long roomId);
}
