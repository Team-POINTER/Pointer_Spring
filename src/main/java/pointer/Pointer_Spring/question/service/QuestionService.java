package pointer.Pointer_Spring.question.service;

import org.springframework.stereotype.Service;
import pointer.Pointer_Spring.common.exception.DynamicException;
import pointer.Pointer_Spring.question.domain.Question;
import pointer.Pointer_Spring.question.dto.QuestionDto;
import pointer.Pointer_Spring.question.repository.QuestionRepository;
import pointer.Pointer_Spring.room.domain.Room;
import pointer.Pointer_Spring.room.domain.RoomMember;
import pointer.Pointer_Spring.room.repository.RoomMemberRepository;
import pointer.Pointer_Spring.room.repository.RoomRepository;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.user.repository.UserRepository;
import pointer.Pointer_Spring.vote.domain.VoteHistory;
import pointer.Pointer_Spring.vote.repository.VoteRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final VoteRepository voteRepository;

    public QuestionService(QuestionRepository questionRepository, RoomRepository roomRepository, UserRepository userRepository, RoomMemberRepository roomMemberRepository, VoteRepository voteRepository) {
        this.questionRepository = questionRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.roomMemberRepository = roomMemberRepository;
        this.voteRepository = voteRepository;
    }

    @Transactional
    public QuestionDto.CreateResponse createQuestion(QuestionDto.CreateRequest request) {

        // 방 조회
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> {
                    throw new RuntimeException("방 조회에 실패했습니다.");
                });

        // 유저 조회
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> {
                    throw new RuntimeException("유저 조회에 실패했습니다.");
                });

        // 질문 생성 가능한지 확인
        validQuestionTime();

        Question question = Question.builder()
                .room(room)
                .user(user)
                .question(request.getContent())
                .build();

        questionRepository.save(question);

        return QuestionDto.CreateResponse.builder()
                .questionId(question.getId())
                .content(question.getQuestion())
                .build();
    }

    private void validQuestionTime() {
        Question prevQuestion = questionRepository.findTopByOrderByIdDesc().orElse(null);

        if(prevQuestion != null) {
            LocalDateTime now = LocalDateTime.now();

            if(now.isBefore(prevQuestion.getCreatedAt().plusDays(1))) {
                int roomMemberCount = roomMemberRepository.countByRoom(prevQuestion.getRoom());
                int voteCount = voteRepository.countDistinctUserByQuestion(prevQuestion);

                if(roomMemberCount == voteCount) {
                    prevQuestion.setCreatedAt(now);
                    questionRepository.save(prevQuestion);
                    return ;
                }

                throw new RuntimeException("24시간이 지나지 않았습니다.");
            }
        }
    }

    public QuestionDto.GetCurrentResponse getCurrentQuestion(Long userId, Long roomId) {
        LocalDateTime now = LocalDateTime.now().minusDays(1);

        Room room = roomRepository.findById(roomId).orElseThrow(() -> {
            throw new RuntimeException("방 조회에 실패했습니다.");
        });

        Question currentQuestion = questionRepository.findByCreatedAtAfter(now).orElseThrow(() -> {
            throw new RuntimeException("현재 질문이 없습니다.");
        });

        boolean isVoted = voteRepository.existsByUserUserIdAndQuestion(userId, currentQuestion);

        List<QuestionDto.GetMemberResponse> roomMembers = roomMemberRepository.findAllByRoom(room).stream()
                .map((m) -> QuestionDto.GetMemberResponse.builder()
                        .userId(m.getUser().getUserId())
                        .nickname(m.getUser().getName())
                        .build())
                .toList();


        return QuestionDto.GetCurrentResponse.builder()
                .questionId(currentQuestion.getId())
                .content(currentQuestion.getQuestion())
                .isVoted(isVoted)
                .members(roomMembers)
                .build();
    }

    public List<QuestionDto.GetResponse> getQuestions(Long userId, Long roomId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new DynamicException("유저 조회에 실패했습니다.");
        });

        Room room = roomRepository.findById(roomId).orElseThrow(() -> {
            throw new DynamicException("방 조회에 실패했습니다.");
        });

        List<Question> questions = questionRepository.findAllByRoomOrderByCreatedAtDesc(room);
        List<QuestionDto.GetResponse> response = new ArrayList<>();
        for(Question question : questions) {
            //int roomMemberCount = roomMemberRepository.countByRoom(room);
            //int voteCount = voteRepository.countDistinctUsersByQuestion(question);
            int allVoteCount = voteRepository.countByQuestion(question);
            int voteCount = voteRepository.countByCandidateAndQuestion(user, question);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy.MM.dd");

            response.add(QuestionDto.GetResponse.builder()
                    .questionId(question.getId())
                    .question(question.getQuestion())
                    .votedMemberCnt(voteCount)
                    .allVoteCnt(allVoteCount)
                    .createdAt(question.getCreatedAt().format(formatter))
                    .build());
        }

        return response;
    }

    @Transactional
    public void modifyQuestion(Long userId, Long questionId, QuestionDto.ModifyRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new RuntimeException("유저 조회에 실패했습니다.");
        });
        Question question = questionRepository.findById(questionId).orElseThrow(() -> {
            throw new RuntimeException("질문 조회에 실패했습니다.");
        });

        if(question.getUser().getUserId() != user.getUserId()) {
            throw new RuntimeException("질문 수정 권한이 없습니다.");
        }

        question.modify(request.getContent());
    }

    @Transactional
    public void deleteQuestion(Long userId, Long questionId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new RuntimeException("유저 조회에 실패했습니다.");
        });
        Question question = questionRepository.findById(questionId).orElseThrow(() -> {
            throw new RuntimeException("질문 조회에 실패했습니다.");
        });

        if(question.getUser().getUserId() != user.getUserId()) {
            throw new RuntimeException("질문 삭제 권한이 없습니다.");
        }

        questionRepository.delete(question);
    }
}
