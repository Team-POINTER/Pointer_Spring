package pointer.Pointer_Spring.vote.service;

import org.springframework.stereotype.Service;
import pointer.Pointer_Spring.question.domain.Question;
import pointer.Pointer_Spring.room.domain.Room;
import pointer.Pointer_Spring.room.domain.RoomMember;
import pointer.Pointer_Spring.room.repository.RoomMemberRepository;
import pointer.Pointer_Spring.room.repository.RoomRepository;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.question.repository.QuestionRepository;
import pointer.Pointer_Spring.user.repository.UserRepository;
import pointer.Pointer_Spring.vote.domain.VoteHistory;
import pointer.Pointer_Spring.vote.dto.VoteDto;
import pointer.Pointer_Spring.vote.repository.VoteRepository;

import javax.transaction.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class VoteServiceImpl implements VoteService {

    private final VoteRepository voteRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;

    public VoteServiceImpl(VoteRepository voteRepository, QuestionRepository questionRepository, UserRepository userRepository, RoomRepository roomRepository, RoomMemberRepository roomMemberRepository) {
        this.voteRepository = voteRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.roomMemberRepository = roomMemberRepository;
    }

    @Transactional
    @Override
    public List<VoteDto.CreateResponse> createVote(VoteDto.CreateRequest dto) {
        // 질문 validation
        Question question = questionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> {
                    throw new RuntimeException("질문 조회에 실패했습니다.");
                });

        // 유저 validation check
        User user = userRepository.findByUserId(dto.getUserId())
                .orElseThrow(() -> {
                    throw new RuntimeException("유저 조회에 실패했습니다.");
                });

        // 투표 생성
        List<VoteDto.CreateResponse> response = new ArrayList<>();
        for(Long userId : dto.getVotedUserIds()) {
            // 유저 validation check
            User voteUser = userRepository.findByUserId(userId)
                    .orElseThrow(() -> {
                        throw new RuntimeException("유저 조회에 실패했습니다.");
                    });

            VoteHistory vote = VoteHistory.builder()
                    .question(question)
                    .user(user)
                    .candidate(voteUser)
                    .candidateName(voteUser.getName())
                    .hint(dto.getHint())
                    .build();

            voteRepository.save(vote);
            response.add(VoteDto.CreateResponse.builder()
                    .id(vote.getVoteHistoryId())
                    .userId(user.getUserId())
                    .questionId(question.getId())
                    .votedUserId(voteUser.getUserId())
                    .hint(vote.getHint())
                    .build());
        }

        return response;
    }

    @Override
    public VoteDto.GetResponse getQuestionVoteCnt(Long userId, Long questionId) {
        // 유저 validation check
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    throw new RuntimeException("유저 조회에 실패했습니다.");
                });
        Question question = questionRepository.findById(questionId).orElseThrow(() -> {
            throw new RuntimeException("질문 조회에 실패했습니다.");
        });

        // 해당 유저 정보 조회
        int allVoteCnt = voteRepository.countByQuestion(question);
        int targetVotedCnt = voteRepository.countByQuestionAndCandidate(question, user);
        VoteDto.GetMemberResponse targetUser = VoteDto.GetMemberResponse.builder()
                .userId(user.getUserId())
                .userName(user.getName())
                .allVoteCnt(allVoteCnt)
                .votedMemberCnt(targetVotedCnt)
                .build();

        List<RoomMember> roomMembers = roomMemberRepository.findAllByRoom(question.getRoom());

        // 룸 유저들 정보 조회
        List<VoteDto.GetMemberResponse> memberResponses = new ArrayList<>();
        for (RoomMember roomMember : roomMembers) {
            User member = roomMember.getUser();
            int votedCnt = voteRepository.countByQuestionAndCandidate(question, member);
            memberResponses.add(VoteDto.GetMemberResponse.builder()
                    .userId(member.getUserId())
                    .userName(member.getName())
                    .votedMemberCnt(votedCnt)
                    .allVoteCnt(allVoteCnt)
                    .build());
        }

        // 투표안한 유저 개수
        int votedUserCnt = voteRepository.countDistinctUsersByQuestion(question);
        int notVotedCnt = roomMembers.size() - votedUserCnt;

        return VoteDto.GetResponse.builder()
                .members(memberResponses)
                .targetUser(targetUser)
                .notNotedMemberCnt(notVotedCnt)
                .build();

    }


    @Override
    public List<VoteDto.GetNotVotedMember> getNotVotedMember(Long questionId) {
        Question question = questionRepository.findById(questionId).orElseThrow(() -> {
            throw new RuntimeException("질문 조회에 실패했습니다.");
        });
        // 쿼리문 변경 예정
        List<RoomMember> roomMembers = roomMemberRepository.findAllByRoom(question.getRoom());
        List<VoteDto.GetNotVotedMember> notVotedMembers = new ArrayList<>();
        for (RoomMember roomMember : roomMembers) {
            User member = roomMember.getUser();
            boolean vote = voteRepository.existsByUser(member);
            if (!vote) {
                notVotedMembers.add(VoteDto.GetNotVotedMember.builder()
                        .userId(member.getUserId())
                        .userName(member.getName())
                        .build());
            }
        }

        return notVotedMembers;
    }

    @Override
    public VoteDto.GetHintResponse getHintResponse(Long userId, Long questionId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    throw new RuntimeException("유저 조회에 실패했습니다.");
                });
        Question question = questionRepository.findById(questionId).orElseThrow(() -> {
            throw new RuntimeException("질문 조회에 실패했습니다.");
        });
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd");

        List<VoteHistory> voteHistories = voteRepository.findAllByQuestionAndCandidate(question, user);
        int allVoteCnt = voteRepository.countByQuestion(question);
        List<String> hints = new ArrayList<>();
        for(VoteHistory vote : voteHistories) {
            hints.add(vote.getHint());
        }

        return VoteDto.GetHintResponse.builder()
                .targetVotedCnt(voteHistories.size())
                .allVoteCnt(allVoteCnt)
                .hint(hints)
                .createdAt(question.getCreatedAt().format(formatter))
                .build();
    }
}
