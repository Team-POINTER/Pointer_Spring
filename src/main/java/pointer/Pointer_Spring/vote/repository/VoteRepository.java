package pointer.Pointer_Spring.vote.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pointer.Pointer_Spring.question.domain.Question;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.vote.domain.VoteHistory;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<VoteHistory, Long> {

    @Query(value = "select count(distinct member_id) from VotingHistory where question_id = ?1", nativeQuery = true)
    int countDistinctUserByQuestionAndStatus(Long questionId, int status);

    boolean existsByMemberIdAndQuestionIdAndStatus(Long userId, Long currentQuestionId, int status);

    int countByQuestionIdAndStatus(Long questionId, int status);

    int countByCandidateIdAndQuestionIdAndStatus(Long userId, Long questionId, int status);

    boolean existsByMemberIdAndStatus(Long memberId, int status);

    int countByQuestionIdAndCandidateId(Long questionId, Long userId);

    List<VoteHistory> findAllByQuestionIdAndCandidateId(Long questionId, Long userId);

    VoteHistory findTopByQuestionIdAndCandidateIdOrderByUpdatedAtDesc(Long questionId, Long userId);
    VoteHistory findAllByRoomId(Long roomId);

    @Override
    Optional<VoteHistory> findById(Long aLong);

    Optional<VoteHistory> findByQuestionIdAndCandidateIdAndMemberId(Long questionId, Long cadidateId, Long userId);
}
