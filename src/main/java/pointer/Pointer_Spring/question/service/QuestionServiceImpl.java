package pointer.Pointer_Spring.question.service;

import org.springframework.stereotype.Service;
import pointer.Pointer_Spring.question.domain.Question;
import pointer.Pointer_Spring.question.dto.QuestionDto;
import pointer.Pointer_Spring.question.repository.QuestionRepository;
import pointer.Pointer_Spring.room.domain.Room;
import pointer.Pointer_Spring.room.domain.RoomMember;
import pointer.Pointer_Spring.room.repository.RoomMemberRepository;
import pointer.Pointer_Spring.room.repository.RoomRepository;
import pointer.Pointer_Spring.user.domain.User;
import pointer.Pointer_Spring.user.repository.UserRepository;
import pointer.Pointer_Spring.validation.CustomException;
import pointer.Pointer_Spring.validation.ExceptionCode;
import pointer.Pointer_Spring.vote.repository.VoteRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final VoteRepository voteRepository;

    public QuestionServiceImpl(
            QuestionRepository questionRepository,
            RoomRepository roomRepository,
            UserRepository userRepository,
            RoomMemberRepository roomMemberRepository,
            VoteRepository voteRepository
    ) {
        this.questionRepository = questionRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.roomMemberRepository = roomMemberRepository;
        this.voteRepository = voteRepository;
    }

    @Override
    @Transactional
    public QuestionDto.CreateResponse createQuestion(QuestionDto.CreateRequest request) {

        // 방 조회
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.ROOM_NOT_FOUND);
                });

        // 유저 조회
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> {
                    throw new CustomException(ExceptionCode.USER_NOT_FOUND);
                });

        // 질문 생성 가능한지 확인
        validQuestionTime();

        Question question = Question.builder()
                .room(room)
                .creatorId(user.getUserId())
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
                int voteCount = voteRepository.countDistinctUserByQuestion(prevQuestion.getId());

                if(roomMemberCount == voteCount) {
                    prevQuestion.setCreatedAt(now);
                    questionRepository.save(prevQuestion);
                    return ;
                }

                throw new CustomException(ExceptionCode.QUESTION_CREATED_FAILED);
            }
        }
    }

    @Override
    public QuestionDto.GetCurrentResponse getCurrentQuestion(Long userId, Long roomId) {
        LocalDateTime now = LocalDateTime.now().minusDays(1);

        Room room = roomRepository.findById(roomId).orElseThrow(() -> {
            throw new CustomException(ExceptionCode.ROOM_NOT_FOUND);
        });

        Question currentQuestion = questionRepository.findByCreatedAtAfterAndRoomRoomId(now, roomId).orElseThrow(() -> {
            throw new CustomException(ExceptionCode.CURRENT_QUESTION_NOT_FOUND);
        });

        boolean isVoted = voteRepository.existsByMemberIdAndQuestionId(userId, currentQuestion.getId());

        List<QuestionDto.GetMemberResponse> roomMembers = roomMemberRepository.findAllByRoom(room).stream()
                .map((m) -> QuestionDto.GetMemberResponse.builder()
                        .userId(m.getUser().getUserId())
                        .nickname(m.getUser().getName())
                        .build())
                .toList();


        return QuestionDto.GetCurrentResponse.builder()
                .roomName(room.getName())
                .questionId(currentQuestion.getId())
                .content(currentQuestion.getQuestion())
                .isVoted(isVoted)
                .members(roomMembers)
                .build();
    }

    @Override
    public List<QuestionDto.GetResponse> getQuestions(Long userId, Long roomId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new CustomException(ExceptionCode.USER_NOT_FOUND);
        });

        Room room = roomRepository.findById(roomId).orElseThrow(() -> {
            throw new CustomException(ExceptionCode.ROOM_NOT_FOUND);
        });

        List<Question> questions = questionRepository.findAllByRoomOrderByCreatedAtDesc(room);
        List<QuestionDto.GetResponse> response = new ArrayList<>();
        for(Question question : questions) {
            //int roomMemberCount = roomMemberRepository.countByRoom(room);
            //int voteCount = voteRepository.countDistinctUsersByQuestion(question);
            int allVoteCount = voteRepository.countByQuestionId(question.getId());
            int voteCount = voteRepository.countByCandidateIdAndQuestionId(user.getUserId(), question.getId());

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

    @Override
    @Transactional
    public void modifyQuestion(Long userId, Long questionId, QuestionDto.ModifyRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new CustomException(ExceptionCode.USER_NOT_FOUND);
        });
        Question question = questionRepository.findById(questionId).orElseThrow(() -> {
            throw new CustomException(ExceptionCode.QUESTION_NOT_FOUND);
        });

        boolean checkRoomMember = roomMemberRepository
                .existsByUserUserIdAndRoomRoomId(userId, question.getRoom().getRoomId());

        if(!checkRoomMember)
            throw new CustomException(ExceptionCode.QUESTION_DELETE_NOT_AUTHENTICATED);

        question.modify(request.getContent());
    }

    @Override
    @Transactional
    public void deleteQuestion(Long userId, Long questionId) {
        User user = userRepository.findById(userId).orElseThrow(() -> {
            throw new CustomException(ExceptionCode.USER_NOT_FOUND);
        });
        Question question = questionRepository.findById(questionId).orElseThrow(() -> {
            throw new CustomException(ExceptionCode.QUESTION_NOT_FOUND);
        });

        boolean checkRoomMember = roomMemberRepository
                .existsByUserUserIdAndRoomRoomId(userId, question.getRoom().getRoomId());

        if(!checkRoomMember)
            throw new CustomException(ExceptionCode.QUESTION_DELETE_NOT_AUTHENTICATED);

        questionRepository.delete(question);
    }
}
